package com.reddate.ddc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.constant.EthFunctions;
import com.reddate.ddc.dto.config.Gateway;
import com.reddate.ddc.dto.wuhanchain.ReqJsonRpcBean;
import com.reddate.ddc.dto.wuhanchain.RespJsonRpcBean;
import com.reddate.ddc.dto.wuhanchain.TransactionsBean;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.listener.SignEvent;
import com.reddate.ddc.listener.SignEventListener;
import com.reddate.ddc.net.RequestOptions;
import com.reddate.ddc.util.AnalyzeChainInfoUtils;
import com.reddate.ddc.util.http.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.abi.FunctionEncoder;
import org.fisco.bcos.web3j.abi.TypeReference;
import org.fisco.bcos.web3j.abi.datatypes.Function;
import org.fisco.bcos.web3j.abi.datatypes.Type;
import org.fisco.bcos.web3j.protocol.core.methods.response.AbiDefinition;
import org.fisco.bcos.web3j.tx.AbiUtil;
import org.fisco.bcos.web3j.tx.TransactionAssembleException;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.fisco.bcos.web3j.tx.txdecode.ContractAbiUtil;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.jetbrains.annotations.NotNull;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;
import org.web3j.utils.Strings;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

@Slf4j
public class BaseService extends RestTemplateUtil {

    public volatile static SignEventListener signEventListener;
    public volatile static Gateway gatewayConfig = new Gateway();


    public ReqJsonRpcBean assembleTransaction(String functionName, ArrayList<Object> params, RequestOptions requestOptions) throws Exception {
        // config check
        checkRequestOptions(requestOptions);

        // address
        String address = requestOptions.getUserAddress();
        if (Strings.isEmpty(address)) {
            throw new DDCException(ErrorMessage.SIGN_USER_ADDRESS_IS_EMPTY);
        }

        // nonce
        BigInteger nonce = requestOptions.getNonce();
        if (Objects.isNull(nonce)) {
            nonce = getTransactionCount(address, requestOptions);
            if (Objects.isNull(nonce)) {
                throw new DDCException(ErrorMessage.NONCE_GET_FAILED);
            }
        }

        // gasPrice
        BigInteger gasPrice = requestOptions.getGasPrice();
        if (Objects.isNull(gasPrice)) {
            gasPrice = getGasPrice(requestOptions);
            if (Objects.isNull(gasPrice)) {
                throw new DDCException(ErrorMessage.GAS_PRICE_GET_FAILED);
            }
        }

        // encodeTransaction
        String contractAbi = requestOptions.getContractAbi();
        String encodeTransaction = encodeTransactionByAbi(contractAbi, functionName, params);

        // gasLimit
        String contractAddress = requestOptions.getContractAddress();
        BigInteger gasLimit = requestOptions.getGasLimit();
        if (Objects.isNull(gasLimit)) {
            gasLimit = estimateGas(address, contractAddress, gasPrice, encodeTransaction, requestOptions);
            if (BigInteger.ZERO.compareTo(gasLimit) >= 0) {
                throw new DDCException(ErrorMessage.GAS_LIMIT_GET_FAILED);
            }
        }

        // build transaction
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress, encodeTransaction);

        // event signature
        SignEvent signEvent = new SignEvent();
        signEvent.setRawTransaction(rawTransaction);
        SignEventListener userEventListener = Objects.isNull(requestOptions.getSignEventListener()) ? signEventListener : requestOptions.getSignEventListener();
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
        return getTransactionReceipt(hash, RequestOptions.getDefault());
    }

    /**
     * 获取交易回执
     *
     * @param hash
     * @return
     */
    public TransactionReceipt getTransactionReceipt(String hash, RequestOptions options) throws Exception {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod(EthFunctions.ETH_GET_TRANSACTION_RECEIPT);

        ArrayList<Object> params = new ArrayList<>();
        params.add(hash);

        reqJsonRpcBean.setParams(params);

        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, options);
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
        return getTransactionByHash(hash, RequestOptions.getDefault());
    }

    /**
     * 获取交易信息
     *
     * @param hash
     * @return
     */
    public TransactionsBean getTransactionByHash(String hash, RequestOptions options) throws Exception {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod(EthFunctions.ETH_GET_TRANSACTION_BY_HASH);

        ArrayList<Object> params = new ArrayList<>();
        params.add(hash);
        reqJsonRpcBean.setParams(params);

        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, options);
        resultCheck(respJsonRpcBean);
        String result = JSONObject.toJSONString(respJsonRpcBean.getResult());
        return JSONObject.parseObject(result, TransactionsBean.class);
    }

    /**
     * 根据交易哈希查询交易状态是否成功
     *
     * @param hash
     * @return Boolean
     */
    public Boolean getTransByStatus(String hash) throws Exception {
        return getTransByStatus(hash, RequestOptions.getDefault());
    }

    /**
     * 根据交易哈希查询交易状态是否成功
     *
     * @param hash
     * @return
     */
    public Boolean getTransByStatus(String hash, RequestOptions options) throws Exception {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod(EthFunctions.ETH_GET_TRANSACTION_RECEIPT);

        ArrayList<Object> params = new ArrayList<>();
        params.add(hash);
        reqJsonRpcBean.setParams(params);

        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, options);
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
        return getTransactionCount(address, RequestOptions.getDefault());
    }

    /**
     * getTransactionCount
     *
     * @param address
     * @return
     */
    public BigInteger getTransactionCount(String address, RequestOptions options) throws Exception {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod(EthFunctions.ETH_GET_TRANSACTION_COUNT);

        ArrayList<Object> params = new ArrayList<>();
        params.add(address);
        params.add("latest");
        reqJsonRpcBean.setParams(params);

        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, options);
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
        return getGasPrice(RequestOptions.getDefault());
    }

    /**
     * gasPrice
     *
     * @param
     * @return
     */
    public BigInteger getGasPrice(RequestOptions options) throws Exception {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod(EthFunctions.ETH_GAS_PRICE);
        reqJsonRpcBean.setParams(new ArrayList<>());
        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, options);
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
        return getBlockNumber(RequestOptions.getDefault());
    }

    /**
     * gasPrice
     *
     * @param
     * @return
     */
    public BigInteger getBlockNumber(RequestOptions options) throws Exception {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod(EthFunctions.ETH_BLOCK_NUMBER);
        reqJsonRpcBean.setParams(new ArrayList<>());
        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, options);
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
        return getBlockByNumber(blockNumber, RequestOptions.getDefault());
    }

    /**
     * gasPrice
     *
     * @param
     * @return
     */
    public RespJsonRpcBean getBlockByNumber(BigInteger blockNumber, RequestOptions options) throws Exception {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod(EthFunctions.ETH_GET_BLOCK_BY_NUMBER);
        ArrayList<Object> params = new ArrayList<>();
        params.add(Numeric.toHexStringWithPrefix(blockNumber));
        params.add(true);
        reqJsonRpcBean.setParams(params);
        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, options);
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
    public InputAndOutputResult sendCallTransactionAndDecodeOutput(RequestOptions options, ArrayList<Object> arrayList, String functionName) throws Exception {

        // function encoder
        Function function = transactionAssembleForMethodInvoke(options.getContractAbi(), functionName, arrayList);
        String encodedFunction = FunctionEncoder.encode(function);

        // eth call
        ReqJsonRpcBean reqJsonRpcBean = assembleCallTransaction(encodedFunction, options);
        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, options);

        // check
        resultCheck(respJsonRpcBean);

        // decode
        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(options.getContractAbi(), options.getContractBytecode(), encodedFunction, respJsonRpcBean.getResult().toString());
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
    public RespJsonRpcBean assembleTransactionAndSend(RequestOptions options, ArrayList<Object> arrayList, String functionName) throws Exception {

        // assembleTransaction
        ReqJsonRpcBean reqJsonRpcBean = assembleTransaction(functionName, arrayList, options);

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
        if (Strings.isEmpty(requestOptions.getGateWayUrl())) {
            throw new DDCException(ErrorMessage.EMPTY_GATEWAY_URL_SPECIFIED);
        }
        if (Strings.isEmpty(requestOptions.getContractAbi())) {
            throw new DDCException(ErrorMessage.CONTRACT_ABI_IS_EMPTY);
        }
        if (Strings.isEmpty(requestOptions.getContractBytecode())) {
            throw new DDCException(ErrorMessage.CONTRACT_BYTECODE_IS_EMPTY);
        }
        if (Strings.isEmpty(requestOptions.getUserAddress())) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_EMPTY);
        }
        if (Strings.isEmpty(requestOptions.getContractAddress())) {
            throw new DDCException(ErrorMessage.CONTRACT_ADDRESS_IS_EMPTY);
        }
        if (Objects.isNull(requestOptions.getSignEventListener())) {
            throw new DDCException(ErrorMessage.SIGN_EVENT_LISTENER_IS_EMPTY);
        }
        if (Objects.nonNull(requestOptions.getGasLimit()) && BigInteger.ZERO.compareTo(requestOptions.getGasLimit()) >= 0) {
            throw new DDCException(ErrorMessage.GAS_LIMIT_DEFINITION_ERROR);
        }
        if (Objects.nonNull(requestOptions.getGasPrice()) && BigInteger.ZERO.compareTo(requestOptions.getGasPrice()) >= 0) {
            throw new DDCException(ErrorMessage.GAS_PRICE_DEFINITION_ERROR);
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

    private ReqJsonRpcBean assembleCallTransaction(String encodedFunction, RequestOptions requestOptions) {
        Transaction transaction = Transaction.createEthCallTransaction(requestOptions.getUserAddress(), requestOptions.getContractAddress(), encodedFunction);
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

}
