package com.reddate.wuhanddc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reddate.wuhanddc.constant.ErrorMessage;
import com.reddate.wuhanddc.constant.EthFunctions;
import com.reddate.wuhanddc.dto.config.DDCContract;
import com.reddate.wuhanddc.dto.ddc.BaseEventBean;
import com.reddate.wuhanddc.dto.wuhanchain.ReqJsonRpcBean;
import com.reddate.wuhanddc.dto.wuhanchain.RespJsonRpcBean;
import com.reddate.wuhanddc.dto.wuhanchain.TransactionsBean;
import com.reddate.wuhanddc.exception.DDCException;
import com.reddate.wuhanddc.listener.SignEvent;
import com.reddate.wuhanddc.listener.SignEventListener;
import com.reddate.wuhanddc.net.DDCWuhan;
import com.reddate.wuhanddc.net.RequestOptions;
import com.reddate.wuhanddc.util.AnalyzeChainInfoUtils;
import com.reddate.wuhanddc.util.HexUtils;
import com.reddate.wuhanddc.util.http.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.abi.FunctionEncoder;
import org.fisco.bcos.web3j.abi.TypeReference;
import org.fisco.bcos.web3j.abi.datatypes.Function;
import org.fisco.bcos.web3j.abi.datatypes.Type;
import org.fisco.bcos.web3j.crypto.WalletUtils;
import org.fisco.bcos.web3j.protocol.core.methods.response.AbiDefinition;
import org.fisco.bcos.web3j.tx.AbiUtil;
import org.fisco.bcos.web3j.tx.TransactionAssembleException;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.fisco.bcos.web3j.tx.txdecode.ContractAbiUtil;
import org.fisco.bcos.web3j.tx.txdecode.EventResultEntity;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;
import org.web3j.utils.Strings;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.*;

import static com.reddate.wuhanddc.constant.ContractConfig.DDCContracts;
import static com.reddate.wuhanddc.constant.EventBeanMapConfig.eventBeanMap;
import static com.reddate.wuhanddc.util.AnalyzeChainInfoUtils.analyzeEventLog;
import static com.reddate.wuhanddc.util.AnalyzeChainInfoUtils.assembleBeanByReflect;

@Slf4j
public class BaseService extends RestTemplateUtil {
    private final Logger logger = LoggerFactory.getLogger(BaseService.class);
    public static SignEventListener signEventListener;

    public ReqJsonRpcBean assembleTransaction(String sender, String functionName, ArrayList<Object> params, RequestOptions requestOptions, DDCContract contract) throws Exception {
        // config check
        checkRequestOptions(requestOptions);

        if (Objects.isNull(contract)) {
            throw new DDCException(ErrorMessage.CONTRACT_INFO_IS_EMPTY);
        }

        // nonce
        BigInteger nonce = (Objects.nonNull(requestOptions) && requestOptions.getNonce() != null) ? requestOptions.getNonce() : getTransactionCount(sender);
        if (Objects.isNull(nonce)) {
            throw new DDCException(ErrorMessage.NONCE_GET_FAILED);
        }

        // gasPrice
        BigInteger gasPrice = (Objects.nonNull(requestOptions) && requestOptions.getGasPrice() != null) ? requestOptions.getGasPrice() : getGasPrice();
        if (Objects.isNull(gasPrice)) {
            throw new DDCException(ErrorMessage.GAS_PRICE_GET_FAILED);
        }

        // encodeTransaction
        String contractAbi = contract.getContractAbi();
        String encodeTransaction = encodeTransactionByAbi(contractAbi, functionName, params);

        // gasLimit
        String contractAddress = contract.getContractAddress();
        BigInteger gasLimit = (Objects.nonNull(requestOptions) && requestOptions.getGasLimit() != null) ? requestOptions.getGasLimit() : estimateGas(sender, contractAddress, gasPrice, encodeTransaction, requestOptions);
        if (Objects.isNull(gasLimit)) {
            throw new DDCException(ErrorMessage.GAS_LIMIT_GET_FAILED);
        }

        // build transaction
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress, encodeTransaction);

        // event signature
        SignEvent signEvent = new SignEvent();
        signEvent.setRawTransaction(rawTransaction);
        signEvent.setSender(sender);
        SignEventListener userEventListener = signEventListener;
        String signedMessage = userEventListener.signEvent(signEvent);

        if (Strings.isEmpty(signedMessage)) {
            throw new DDCException(ErrorMessage.SIGN_EVENT_LISTENER_RESPONSE_FAILED);
        }
        return getReqJsonRpcBean(signedMessage);
    }


    /**
     * 获取交易回执
     *
     * @param hash
     * @return
     */
    public TransactionReceipt getTransactionReceipt(String hash) throws Exception {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod(EthFunctions.ETH_GET_TRANSACTION_RECEIPT);

        ArrayList<Object> params = new ArrayList<>();
        params.add(hash);

        reqJsonRpcBean.setParams(params);

        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, null);
        resultCheck(respJsonRpcBean);

        String result = JSONObject.toJSONString(respJsonRpcBean.getResult());
        return JSONObject.parseObject(result, TransactionReceipt.class);
    }

    /**
     * 获取交易信息
     *
     * @param hash
     * @return
     */
    public TransactionsBean getTransactionByHash(String hash) throws Exception {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod(EthFunctions.ETH_GET_TRANSACTION_BY_HASH);

        ArrayList<Object> params = new ArrayList<>();
        params.add(hash);
        reqJsonRpcBean.setParams(params);

        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, null);
        resultCheck(respJsonRpcBean);
        String result = JSONObject.toJSONString(respJsonRpcBean.getResult());
        return JSONObject.parseObject(result, TransactionsBean.class);
    }

    /**
     * 根据交易哈希查询交易状态是否成功
     *
     * @param hash
     * @return
     */
    public Boolean getTransStatusByHash(String hash) throws Exception {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod(EthFunctions.ETH_GET_TRANSACTION_RECEIPT);

        ArrayList<Object> params = new ArrayList<>();
        params.add(hash);
        reqJsonRpcBean.setParams(params);

        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, null);
        if (null == respJsonRpcBean) {
            throw new DDCException(ErrorMessage.REQUEST_FAILED);
        }
        if (respJsonRpcBean.getError() != null) {
            return false;
        }
        String result = JSONObject.toJSONString(respJsonRpcBean.getResult());
        if (Strings.isEmpty(result)) {
            throw new DDCException(ErrorMessage.TRANSACTION_RESULT_IS_EMPTY);
        }
        JSONObject resultObject = JSONObject.parseObject(result);
        if (Objects.isNull(resultObject)) {
            throw new DDCException(ErrorMessage.RESULT_FORMAT_CONVERSION_FAILED);
        }
        return "0x1".equalsIgnoreCase(resultObject.getString("status")) ? true : false;
    }

    /**
     * getTransactionCount
     *
     * @param address
     * @return
     */
    public BigInteger getTransactionCount(String address) throws Exception {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod(EthFunctions.ETH_GET_TRANSACTION_COUNT);

        ArrayList<Object> params = new ArrayList<>();
        params.add(address);
        params.add("pending");
        reqJsonRpcBean.setParams(params);

        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, null);
        resultCheck(respJsonRpcBean);
        return Numeric.toBigInt(respJsonRpcBean.getResult().toString());
    }

    /**
     * gasPrice
     *
     * @param
     * @return
     */
    public BigInteger getGasPrice() throws Exception {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod(EthFunctions.ETH_GAS_PRICE);
        reqJsonRpcBean.setParams(new ArrayList<>());
        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, null);
        resultCheck(respJsonRpcBean);
        return Numeric.toBigInt(respJsonRpcBean.getResult().toString());
    }


    /**
     * gasPrice
     *
     * @param
     * @return
     */
    public BigInteger getBlockNumber() throws Exception {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod(EthFunctions.ETH_BLOCK_NUMBER);
        reqJsonRpcBean.setParams(new ArrayList<>());
        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, null);
        resultCheck(respJsonRpcBean);
        return Numeric.toBigInt(respJsonRpcBean.getResult().toString());
    }


    /**
     * gasPrice
     *
     * @param
     * @return
     */
    public RespJsonRpcBean getBlockByNumber(BigInteger blockNumber) throws Exception {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod(EthFunctions.ETH_GET_BLOCK_BY_NUMBER);
        ArrayList<Object> params = new ArrayList<>();
        params.add(Numeric.toHexStringWithPrefix(blockNumber));
        params.add(true);
        reqJsonRpcBean.setParams(params);
        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, null);
        resultCheck(respJsonRpcBean);
        return respJsonRpcBean;
    }

    /**
     * sendCallTransactionAndDecodeOutput
     *
     * @param options      configuration
     * @param arrayList    function params
     * @param functionName function name
     * @return InputAndOutputResult
     * @throws Exception
     */
    @NotNull
    public InputAndOutputResult sendCallTransactionAndDecodeOutput(RequestOptions options, ArrayList<Object> arrayList, String functionName, DDCContract contract) throws Exception {

        // function encoder
        Function function = transactionAssembleForMethodInvoke(contract.getContractAbi(), functionName, arrayList);
        String encodedFunction = FunctionEncoder.encode(function);

        // eth call
        ReqJsonRpcBean reqJsonRpcBean = assembleCallTransaction(encodedFunction, contract);
        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, options);

        // check
        resultCheck(respJsonRpcBean);

        // decode
        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(contract.getContractAbi(), contract.getContractBytecode(), encodedFunction, respJsonRpcBean.getResult().toString());
        if (inputAndOutputResult.getResult().size() == 0) {
            throw new DDCException(ErrorMessage.INPUT_AND_OUTPUT_RESULT_IS_EMPTY);
        }
        return inputAndOutputResult;
    }

    /**
     * assembleTransactionAndSend
     *
     * @param options      configuration
     * @param arrayList    params
     * @param functionName
     * @return
     * @throws Exception
     */
    public RespJsonRpcBean assembleTransactionAndSend(String sender, RequestOptions options, ArrayList<Object> arrayList, String functionName, DDCContract contract) throws Exception {

        // assembleTransaction
        ReqJsonRpcBean reqJsonRpcBean = assembleTransaction(sender, functionName, arrayList, options, contract);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, options);

        // check result
        resultCheck(respJsonRpcBean);

        return respJsonRpcBean;
    }

    /**
     * 校验交易结果
     *
     * @param respJsonRpcBean
     */
    public static void resultCheck(RespJsonRpcBean respJsonRpcBean) {
        if (null == respJsonRpcBean) {
            throw new DDCException(ErrorMessage.REQUEST_FAILED);
        }

        if (respJsonRpcBean.getError() != null) {
            throw new DDCException(ErrorMessage.REQUEST_FAILED.getCode(), respJsonRpcBean.getError().getMessage());
        }
    }


    private void checkRequestOptions(RequestOptions requestOptions) {
        if (Objects.isNull(signEventListener)) {
            throw new DDCException(ErrorMessage.SIGN_EVENT_LISTENER_IS_EMPTY);
        }
        if (Strings.isEmpty(DDCWuhan.getGatewayUrl())) {
            throw new DDCException(ErrorMessage.EMPTY_GATEWAY_URL_SPECIFIED);
        }
        if (Objects.nonNull(requestOptions)) {
            if (null != requestOptions.getGasLimit() && BigInteger.ZERO.compareTo(requestOptions.getGasLimit()) >= 0) {
                throw new DDCException(ErrorMessage.GAS_LIMIT_DEFINITION_ERROR);
            }
            if (null != requestOptions.getGasPrice() && BigInteger.ZERO.compareTo(requestOptions.getGasPrice()) >= 0) {
                throw new DDCException(ErrorMessage.GAS_PRICE_DEFINITION_ERROR);
            }
        }
    }


    private AbiDefinition getFunctionAbiDefinition(String functionName, String contractAbi) {
        JSONArray abiArr = JSONArray.parseArray(contractAbi);
        AbiDefinition result = null;
        Iterator var4 = abiArr.iterator();

        while (var4.hasNext()) {
            Object object = var4.next();
            AbiDefinition abiDefinition = JSON.parseObject(object.toString(), AbiDefinition.class);
            if ("function".equals(abiDefinition.getType()) && functionName.equals(abiDefinition.getName())) {
                result = abiDefinition;
                break;
            }
        }
        return result;
    }

    /**
     * build rawTransaction
     *
     * @param signedMessage
     * @return
     */
    private ReqJsonRpcBean getReqJsonRpcBean(String signedMessage) {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod(EthFunctions.ETH_SEND_RAW_TRANSACTION);
        List<Object> jsonParams = new ArrayList<>();
        jsonParams.add(signedMessage);
        reqJsonRpcBean.setParams(jsonParams);
        return reqJsonRpcBean;
    }

    /**
     * build rawTransaction
     *
     * @param transaction
     * @return
     */
    private ReqJsonRpcBean getCallJsonRpcBean(Transaction transaction) {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod(EthFunctions.ETH_CALL);
        List<Object> jsonParams = new ArrayList<>();
        jsonParams.add(transaction);
        jsonParams.add("latest");
        reqJsonRpcBean.setParams(jsonParams);
        return reqJsonRpcBean;
    }

    /**
     * estimateGas
     *
     * @param
     * @return
     */
    private BigInteger estimateGas(String from, String to, BigInteger gasPrice, String data, RequestOptions options) throws Exception {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod(EthFunctions.ETH_ESTIMATE_GAS);

        ArrayList<Object> params = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>(1);
        map.put("from", from);
        map.put("to", to);
        map.put("gaPrice", gasPrice.toString());
        map.put("data", data);
        params.add(map);
        reqJsonRpcBean.setParams(params);

        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, options);
        resultCheck(respJsonRpcBean);
        return Numeric.toBigInt(respJsonRpcBean.getResult().toString());
    }

    private String encodeTransactionByAbi(String contractAbi, String funcName, List<Object> funcParam) throws IOException, BaseException {

        AbiDefinition abiDefinition = getFunctionAbiDefinition(funcName, contractAbi);
        if (Objects.isNull(abiDefinition)) {
            throw new TransactionAssembleException("contract funcName is error");
        } else {
            List<String> funcInputTypes = ContractAbiUtil.getFuncInputType(abiDefinition);
            if (funcParam == null) {
                funcParam = new ArrayList();
            }

            if (funcInputTypes.size() != funcParam.size()) {
                throw new TransactionAssembleException("contract funcParam size is error");
            } else {
                List<Type> finalInputs = AbiUtil.inputFormat(funcInputTypes, funcParam);
                List<String> funOutputTypes = AbiUtil.getFuncOutputType(abiDefinition);
                List<TypeReference<?>> finalOutputs = AbiUtil.outputFormat(funOutputTypes);
                Function function = new Function(funcName, finalInputs, finalOutputs);
                return FunctionEncoder.encode(function);
            }
        }
    }

    private ReqJsonRpcBean assembleCallTransaction(String encodedFunction, DDCContract contract) {
        Transaction transaction = Transaction.createEthCallTransaction(null, contract.getContractAddress(), encodedFunction);
        return getCallJsonRpcBean(transaction);
    }

    private Function transactionAssembleForMethodInvoke(String contractAbi, String funcName, List<Object> funcParam) throws IOException, BaseException {

        AbiDefinition abiDefinition = getFunctionAbiDefinition(funcName, contractAbi);
        if (Objects.isNull(abiDefinition)) {
            throw new TransactionAssembleException("contract funcName is error");
        } else {
            List<String> funcInputTypes = ContractAbiUtil.getFuncInputType(abiDefinition);
            if (funcParam == null) {
                funcParam = new ArrayList();
            }

            if (funcInputTypes.size() != funcParam.size()) {
                throw new TransactionAssembleException("contract funcParam size is error");
            } else {
                List<Type> finalInputs = AbiUtil.inputFormat(funcInputTypes, funcParam);
                List<String> funOutputTypes = AbiUtil.getFuncOutputType(abiDefinition);
                List<TypeReference<?>> finalOutputs = AbiUtil.outputFormat(funOutputTypes);
                return new Function(funcName, finalInputs, finalOutputs);
            }
        }
    }

    /***
     * check Sender
     * @param sender
     */
    public void checkSender(String sender) {
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!WalletUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_A_STANDARD_ADDRESS_format);
        }
    }

    /**
     * check owner
     *
     * @param owner
     */
    public void checkOwner(String owner) {
        if (Strings.isEmpty(owner)) {
            throw new DDCException(ErrorMessage.OWNER_IS_EMPTY);
        }

        if (!WalletUtils.isValidAddress(owner)) {
            throw new DDCException(ErrorMessage.OWNER_IS_NOT_ADDRESS_FORMAT);
        }
    }

    /**
     * check Operator
     *
     * @param operator
     */
    public void checkOperator(String operator) {
        if (Strings.isEmpty(operator)) {
            throw new DDCException(ErrorMessage.OPERATOR_IS_EMPTY);
        }

        if (!WalletUtils.isValidAddress(operator)) {
            throw new DDCException(ErrorMessage.OPERATOR_IS_NOT_ADDRESS_FORMAT);
        }
    }

    /**
     * check ddc id
     *
     * @param ddcId
     */
    public void checkDdcId(BigInteger ddcId) {
        if (null == ddcId) {
            throw new DDCException(ErrorMessage.DDC_ID_LT_EMPTY);
        }

        if (BigInteger.ZERO.compareTo(ddcId) >= 0) {
            throw new DDCException(ErrorMessage.DDC_ID_LT_ZERO);
        }
    }

    /**
     * check ddcURI
     *
     * @param ddcURI
     */
    public void checkDdcURI(String ddcURI) {
        if (null == ddcURI) {
            throw new DDCException(ErrorMessage.DDC_URI_IS_EMPTY);
        }
    }

    /***
     * check ddcAddr
     * @param ddcAddr
     */
    public void checkDdcAddr(String ddcAddr) {
        if (Strings.isEmpty(ddcAddr)) {
            throw new DDCException(ErrorMessage.DDC_ADDR_IS_EMPTY);
        }

        if (!WalletUtils.isValidAddress(ddcAddr)) {
            throw new DDCException(ErrorMessage.DDC_ADDR_IS_NOT_ADDRESS_FORMAT);
        }
    }

    /**
     * sig
     *
     * @param sig
     */
    public void checkSig(String sig) {
        if (Strings.isEmpty(sig)) {
            throw new DDCException(ErrorMessage.SIG_IS_EMPTY);
        }

        if (!HexUtils.isValid4ByteHash(sig)) {
            throw new DDCException(ErrorMessage.SIG_IS_NOT_4BYTE_HASH);
        }
    }

    /**
     * check amount
     *
     * @param amount
     */
    public void checkAmount(BigInteger amount) {
        if (amount == null) {
            throw new DDCException(ErrorMessage.AMOUNT_IS_EMPTY);
        }

        if (BigInteger.ZERO.compareTo(amount) >= 0) {
            throw new DDCException(ErrorMessage.AMOUNT_LT_ZERO);
        }
    }

    /**
     * check From
     *
     * @param from
     */
    public void checkFrom(String from) {
        if (Strings.isEmpty(from)) {
            throw new DDCException(ErrorMessage.FROM_ACCOUNT_IS_EMPTY);
        }
        if (!WalletUtils.isValidAddress(from)) {
            throw new DDCException(ErrorMessage.FROM_ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
    }

    /**
     * check To
     *
     * @param to
     */
    public void checkTo(String to) {
        if (Strings.isEmpty(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }
        if (!WalletUtils.isValidAddress(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
    }

    /**
     * check Account
     *
     * @param account
     */
    public void checkAccount(String account) {
        if (Strings.isEmpty(account)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_EMPTY);
        }

        if (!WalletUtils.isValidAddress(account)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
    }

    /**
     * checkAccAddr
     *
     * @param accAddr
     */
    public void checkAccAddr(String accAddr) {
        if (org.fisco.bcos.web3j.utils.Strings.isEmpty(accAddr)) {
            throw new DDCException(ErrorMessage.ACC_ADDR_IS_EMPTY);
        }

        if (!WalletUtils.isValidAddress(accAddr)) {
            throw new DDCException(ErrorMessage.ACC_ADDR_IS_NOT_ADDRESS_FORMAT);
        }
    }

    /**
     * check account name
     *
     * @param accountName
     */
    public void checkAccountName(String accountName) {
        if (Strings.isEmpty(accountName)) {
            throw new DDCException(ErrorMessage.ACCOUNT_NAME_IS_EMPTY);
        }
    }

    /**
     * Analyze transaction data
     *
     * @param txTimestamp
     * @param transactions
     * @param arrayList
     * @param <T>
     * @throws Exception
     */
    public <T extends BaseEventBean> void transactionData(BigInteger txTimestamp, List<EthBlock.TransactionResult> transactions, ArrayList<T> arrayList) throws Exception {
        for (EthBlock.TransactionResult<EthBlock.TransactionObject> transactionResult : transactions) {
            EthBlock.TransactionObject transaction = transactionResult.get();
            TransactionReceipt receipt = getTransactionReceipt(transaction.getHash());
            if (Objects.isNull(receipt)) {
                throw new DDCException(ErrorMessage.GET_TRANSACTION_RECEIPT_ERROR);
            }
            List<Log> logList = receipt.getLogs();
            for (Log log : logList) {
                // Get the contract for this event
                DDCContract contract = DDCContracts.stream().filter(t -> t.getContractAddress().equalsIgnoreCase(log.getAddress())).findAny().orElse(null);
                if (Objects.isNull(contract)) {
                    logger.info(String.format("BlockNum:%s,Contract:%s,Non-DDC official contracts do not have statistical data...", transaction.getBlockNumber(), log.getAddress()));
                    continue;
                }
                // contract info
                String contractAbi = contract.getContractAbi();
                String contractByteCode = contract.getContractBytecode();

                if (Strings.isEmpty(contractAbi) || Strings.isEmpty(contractByteCode)) {
                    throw new DDCException(ErrorMessage.CONTRACT_INFO_IS_EMPTY);
                }

                List<Log> logInfo = new ArrayList<>();
                logInfo.add(log);
                Map<String, List<List<EventResultEntity>>> map = analyzeEventLog(contractAbi, contractByteCode, JSONObject.toJSONString(logInfo));
                // Event to Object
                if (eventBeanMap.isEmpty()) {
                    throw new DDCException(ErrorMessage.CONTRACT_INFO_IS_EMPTY);
                }
                // 事件合约地址
                String contractAddress = log.getAddress();
                for (Map.Entry<String, Class> entry : eventBeanMap.entrySet()) {
                    String keyWithAddress = entry.getKey();
                    // 合约地址
                    String mapAddress = keyWithAddress.substring(0, contractAddress.length());
                    // 合约方法
                    String mapFunction = keyWithAddress.substring(contractAddress.length());
                    if (!(map.containsKey(mapFunction) && contractAddress.equalsIgnoreCase(mapAddress))) {
                        continue;
                    }

                    List<List<EventResultEntity>> eventLists = map.get(mapFunction);
                    for (List<EventResultEntity> eventList : eventLists) {
                        try {
                            T eventBean = (T) assembleBeanByReflect(eventList, entry.getValue());
                            eventBean.setBlockHash(transaction.getHash());
                            eventBean.setTransactionInfoBean(transaction);
                            eventBean.setBlockNumber(transaction.getBlockNumber().toString());
                            eventBean.setTimestamp(txTimestamp + "000");
                            arrayList.add(eventBean);
                        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
                            e.printStackTrace();
                            try {
                                throw e;
                            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}
