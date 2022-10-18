package com.reddate.wuhanddc.service;

import com.google.common.collect.Multimap;
import com.reddate.wuhanddc.constant.DDC1155Functions;
import com.reddate.wuhanddc.constant.ErrorMessage;
import com.reddate.wuhanddc.dto.config.DDCContract;
import com.reddate.wuhanddc.dto.wuhanchain.RespJsonRpcBean;
import com.reddate.wuhanddc.exception.DDCException;
import com.reddate.wuhanddc.net.RequestOptions;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.reddate.wuhanddc.constant.ContractConfig.DDCContracts;

/**
 * wuhanddc 1155
 *
 * @author wxq
 */
public class DDC1155Service extends BaseService {
    public static DDCContract DDC1155Contract;

    public DDC1155Service() {
        DDC1155Contract = DDCContracts.stream().filter(t -> "1155".equals(t.getConfigType())).findFirst().orElse(null);
    }

    /**
     * DDC的创建
     *
     * @param to     接收者账户
     * @param amount DDC数量
     * @param ddcURI DDCURI
     * @param data   附加数据
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String safeMint(String sender, String to, BigInteger amount, String ddcURI, byte[] data) throws Exception {
        return safeMint(sender, to, amount, ddcURI, data, null);
    }

    /**
     * DDC的创建
     *
     * @param to      接收者账户
     * @param amount  DDC数量
     * @param ddcURI  DDC URI
     * @param options config
     * @param data    附加数据
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String safeMint(String sender, String to, BigInteger amount, String ddcURI, byte[] data, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check to
        checkTo(to);

        // check amount
        checkAmount(amount);

        // check wuhanddc uri
        checkDdcURI(ddcURI);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(amount);
        arrayList.add(ddcURI);
        arrayList.add(data);

        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.SAFE_MINT, DDC1155Contract);

        return (String) respJsonRpcBean.getResult();
    }


    /**
     * DDC的批量创建
     *
     * @param to      接收者账户
     * @param ddcInfo DDC信息
     * @param data    附加数据
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String safeMintBatch(String sender, String to, Multimap<BigInteger, String> ddcInfo, byte[] data) throws Exception {
        return safeMintBatch(sender, to, ddcInfo, data, null);
    }

    /**
     * DDC的批量创建
     *
     * @param to      接收者账户
     * @param ddcInfo DDC信息
     * @param data    附加数据
     * @param options configuration
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String safeMintBatch(String sender, String to, Multimap<BigInteger, String> ddcInfo, byte[] data, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check to
        checkTo(to);

        if (null == ddcInfo || ddcInfo.isEmpty()) {
            throw new DDCException(ErrorMessage.IS_EMPTY, "ddcAddr");
        }
        List<String> amountList = new ArrayList<>();
        List<String> ddcURI = new ArrayList<>();

        ddcInfo.forEach((key, value) -> {
            // 验证accName不为空
            if (null == key || BigInteger.ZERO.compareTo(key) >= 0) {
                throw new DDCException(ErrorMessage.IS_EMPTY, "amount");
            }

            amountList.add(key.toString());
            ddcURI.add(value);
        });

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(amountList.stream().collect(Collectors.joining(",")));
        arrayList.add(ddcURI.stream().collect(Collectors.joining(",")));
        arrayList.add(data);
        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.SAFE_MINT_BATCH, DDC1155Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * DDC的授权
     *
     * @param operator 授权者账户
     * @param approved 授权标识
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String setApprovalForAll(String sender, String operator, Boolean approved) throws Exception {
        return setApprovalForAll(sender, operator, approved, null);
    }

    /**
     * DDC的授权
     *
     * @param operator 授权者账户
     * @param approved 授权标识
     * @param options  configuration
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String setApprovalForAll(String sender, String operator, Boolean approved, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check sender
        checkOperator(operator);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(operator);
        arrayList.add(approved);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.SET_APPROVAL_FOR_ALL, DDC1155Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * DDC的授权查询
     *
     * @param owner    拥有者账户
     * @param operator 授权者账户
     * @return 授权结果
     * @throws Exception Exception
     */
    public Boolean isApprovedForAll(String owner, String operator) throws Exception {
        return isApprovedForAll(owner, operator, null);
    }

    /**
     * DDC的授权查询
     *
     * @param owner    拥有者账户
     * @param operator 授权者账户
     * @param options  configuration
     * @return 授权结果
     * @throws Exception Exception
     */
    public Boolean isApprovedForAll(String owner, String operator, RequestOptions options) throws Exception {

        // check owner
        checkOwner(owner);

        // check operator
        checkOperator(operator);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(operator);

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC1155Functions.IS_APPROVED_FOR_ALL, DDC1155Contract);

        return (Boolean) inputAndOutputResult.getResult().get(0).getData();
    }


    /**
     * DDC的转移
     *
     * @param from   拥有者账户
     * @param to     接收者账户
     * @param ddcId  DDCID
     * @param amount 数量
     * @param data   附加数据
     * @return 转移结果
     * @throws Exception Exception
     */
    public String safeTransferFrom(String sender, String from, String to, BigInteger ddcId, BigInteger amount, byte[] data) throws Exception {
        return safeTransferFrom(sender, from, to, ddcId, amount, data, null);
    }

    /**
     * DDC的转移
     *
     * @param from    拥有者账户
     * @param to      接收者账户
     * @param ddcId   DDCID
     * @param amount  数量
     * @param data    附加数据
     * @param options configuration
     * @return 转移结果
     * @throws Exception Exception
     */
    public String safeTransferFrom(String sender, String from, String to, BigInteger ddcId, BigInteger amount, byte[] data, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check from
        checkFrom(from);

        // check to
        checkTo(to);

        // check wuhanddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);
        arrayList.add(to);
        arrayList.add(ddcId);
        arrayList.add(amount);
        arrayList.add(data);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.SAFE_TRANSFER_FROM, DDC1155Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * DDC的批量转移
     *
     * @param from 拥有者账户
     * @param to   接收者账户
     * @param ddcInfos 拥有者DDCID集合
     * @param data 附加数据
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String safeBatchTransferFrom(String sender, String from, String to, Map<BigInteger, BigInteger> ddcInfos, byte[] data) throws Exception {
        return safeBatchTransferFrom(sender, from, to, ddcInfos, data, null);
    }

    /**
     * DDC的批量转移
     *
     * @param from    拥有者账户
     * @param to      接收者账户
     * @param ddcInfos    拥有者DDCID集合
     * @param data    附加数据
     * @param options configuration
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String safeBatchTransferFrom(String sender, String from, String to, Map<BigInteger, BigInteger> ddcInfos, byte[] data, RequestOptions options) throws Exception {

        // check sender
        checkSender(sender);

        // check from
        checkFrom(from);

        // check to
        checkTo(to);

        if (null == ddcInfos || ddcInfos.isEmpty()) {
            throw new DDCException(ErrorMessage.IS_EMPTY, "ddcId");
        }

        // input params
        ArrayList<String> ddcIds = new ArrayList();
        ArrayList<String> amounts = new ArrayList();

        ddcInfos.forEach((key, value) -> {
            // 验证accName不为空
            if (null == key || BigInteger.ZERO.compareTo(value) >= 0) {
                throw new DDCException(ErrorMessage.IS_EMPTY, "amount");
            }

            ddcIds.add(String.valueOf(key));
            amounts.add(String.valueOf(value));
        });
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);
        arrayList.add(to);
        arrayList.add(ddcIds.stream().collect(Collectors.joining(",")));
        arrayList.add(amounts.stream().collect(Collectors.joining(",")));
        arrayList.add(data);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.SAFE_BATCH_TRANSFER_FROM, DDC1155Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * DDC的冻结
     *
     * @param ddcId DDC唯一标识
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String freeze(String sender, BigInteger ddcId) throws Exception {
        return freeze(sender, ddcId, null);
    }

    /**
     * DDC的冻结
     *
     * @param ddcId   DDC唯一标识
     * @param options configuration
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String freeze(String sender, BigInteger ddcId, RequestOptions options) throws Exception {

        // check sender
        checkSender(sender);

        // check wuhanddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.FREEZE, DDC1155Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * DDC的解冻
     *
     * @param ddcId DDC唯一标识
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String unFreeze(String sender, BigInteger ddcId) throws Exception {
        return unFreeze(sender, ddcId, null);
    }

    /**
     * DDC的解冻
     *
     * @param ddcId   DDC唯一标识
     * @param options configuration
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String unFreeze(String sender, BigInteger ddcId, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check wuhanddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.UNFREEZE, DDC1155Contract);
        return (String) respJsonRpcBean.getResult();
    }


    /**
     * DDC的销毁
     *
     * @param owner 拥有者账户
     * @param ddcId DDCID
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String burn(String sender, String owner, BigInteger ddcId) throws Exception {
        return burn(sender, owner, ddcId, null);
    }

    /**
     * DDC的销毁
     *
     * @param owner   拥有者账户
     * @param ddcId   DDCID
     * @param options configuration
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String burn(String sender, String owner, BigInteger ddcId, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check owner
        checkOwner(owner);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(ddcId);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.BURN, DDC1155Contract);
        return (String) respJsonRpcBean.getResult();
    }


    /**
     * DDC的批量销毁
     *
     * @param owner  拥有者账户
     * @param ddcIds DDCID集合
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String burnBatch(String sender, String owner, List<BigInteger> ddcIds) throws Exception {
        return burnBatch(sender, owner, ddcIds, null);
    }

    /**
     * DDC的批量销毁
     *
     * @param owner   拥有者账户
     * @param ddcIds  DDCID集合
     * @param options configuration
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String burnBatch(String sender, String owner, List<BigInteger> ddcIds, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check owner
        checkOwner(owner);

        if (null == ddcIds || ddcIds.isEmpty()) {
            throw new DDCException(ErrorMessage.IS_EMPTY, "ddcId");
        }
        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(ddcIds.stream().map(String::valueOf).collect(Collectors.joining(",")));

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.BURN_BATCH, DDC1155Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 查询当前账户拥有的DDC的数量
     *
     * @param owner 拥有者账户
     * @param ddcId DDCID
     * @return 拥有者账户所对应的DDCID所拥用的数量
     * @throws Exception
     */
    public BigInteger balanceOf(String owner, BigInteger ddcId) throws Exception {
        return balanceOf(owner, ddcId, null);
    }

    /**
     * 查询当前账户拥有的DDC的数量
     *
     * @param owner   拥有者账户
     * @param ddcId   DDCID
     * @param options configuration
     * @return 拥有者账户所对应的DDCID所拥用的数量
     * @throws Exception
     */
    public BigInteger balanceOf(String owner, BigInteger ddcId, RequestOptions options) throws Exception {


        // check owner
        checkOwner(owner);

        // check wuhanddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(ddcId);

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC1155Functions.BALANCE_OF, DDC1155Contract);
        return (BigInteger) inputAndOutputResult.getResult().get(0).getData();
    }

    /***
     * 批量查询账户拥有的DDC的数量
     * @param ddcs wuhanddc owner collection
     * @return 拥有者账户所对应的每个DDCID所拥用的数量
     * @throws Exception
     */
    public List<BigInteger> balanceOfBatch(Multimap<String, BigInteger> ddcs) throws Exception {
        return balanceOfBatch(ddcs, null);
    }

    /***
     * 批量查询账户拥有的DDC的数量
     * @param ddcs 拥有者DDCID集合
     * @param options configuration
     * @return 拥有者账户所对应的每个DDCID所拥用的数量
     * @throws Exception
     */
    public List<BigInteger> balanceOfBatch(Multimap<String, BigInteger> ddcs, RequestOptions options) throws Exception {

        if (null == ddcs || ddcs.isEmpty()) {
            throw new DDCException(ErrorMessage.IS_EMPTY, "ddcId");
        }

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();

        ArrayList<String> owners = new ArrayList<>();
        ArrayList<String> ddcIds = new ArrayList<>();

        ddcs.forEach((key, value) -> {
            owners.add(key);
            ddcIds.add(String.valueOf(value));
        });
        arrayList.add(owners.stream().collect(Collectors.joining(",")));
        arrayList.add(ddcIds.stream().collect(Collectors.joining(",")));

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC1155Functions.BALANCE_OF_BATCH, DDC1155Contract);
        return (List<BigInteger>) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 获取ddcURI
     *
     * @param ddcId ddcId
     * @return DDCURI
     * @throws Exception
     */
    public String ddcURI(BigInteger ddcId) throws Exception {
        return ddcURI(ddcId, null);
    }

    /**
     * 获取ddcURI
     *
     * @param ddcId   ddcId
     * @param options configuration
     * @return DDCURI
     * @throws Exception
     */
    public String ddcURI(BigInteger ddcId, RequestOptions options) throws Exception {

        // check wuhanddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC1155Functions.DDC_URI, DDC1155Contract);
        return (String) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 设置 ddcURI
     *
     * @param ddcId ddcId
     * @return DDCURI
     * @throws Exception Exception
     */
    public String setURI(String sender, BigInteger ddcId, String ddcURI) throws Exception {
        return setURI(sender, ddcId, ddcURI, null);
    }

    /**
     * 设置 ddcURI
     *
     * @param ddcId ddcId
     * @return DDCURI
     * @throws Exception Exception
     */
    public String setURI(String sender, BigInteger ddcId, String ddcURI, RequestOptions options) throws Exception {

        // check sender
        checkSender(sender);

        // check wuhanddc id
        checkDdcId(ddcId);

        // check wuhanddc uri
        checkDdcURI(ddcURI);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(sender);
        arrayList.add(ddcId);
        arrayList.add(ddcURI);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.SET_URI, DDC1155Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 运营方、平台方以及终端用户通过调用该方法对当前最新DDCID进行查询
     *
     * @return
     * @throws Exception
     */
    public BigInteger getLatestDDCId() throws Exception {
        return getLatestDDCId(null);
    }

    /**
     * 运营方、平台方以及终端用户通过调用该方法对当前最新DDCID进行查询
     *
     * @return
     * @throws Exception
     */
    public BigInteger getLatestDDCId(RequestOptions options) throws Exception {
        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, null, DDC1155Functions.GET_LATEST_DDC_ID, DDC1155Contract);
        return (BigInteger) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * Nonce查询
     *
     * @param from
     * @return
     * @throws Exception
     */
    public BigInteger getNonce(String from) throws Exception {
        return getNonce(from, null);
    }

    /**
     * Nonce查询
     *
     * @param from
     * @return
     * @throws Exception
     */
    public BigInteger getNonce(String from, RequestOptions options) throws Exception {
        // check ddc id
        checkFrom(from);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC1155Functions.GET_NONCE, DDC1155Contract);

        return (BigInteger) inputAndOutputResult.getResult().get(0).getData();
    }


    /**
     * 元交易生成
     *
     * @param to     接收者账户
     * @param amount DDC数量
     * @param ddcURI DDCURI
     * @param data   附加数据
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String metaSafeMint(String sender, String to, BigInteger amount, String ddcURI, byte[] data, BigInteger nonce, BigInteger deadline, byte[] sign) throws Exception {
        return metaSafeMint(sender, to, amount, ddcURI, data, nonce, deadline, sign, null);
    }

    /**
     * DDC的创建
     *
     * @param to      接收者账户
     * @param amount  DDC数量
     * @param ddcURI  DDC URI
     * @param options config
     * @param data    附加数据
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String metaSafeMint(String sender, String to, BigInteger amount, String ddcURI, byte[] data, BigInteger nonce, BigInteger deadline, byte[] sign, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check to
        checkTo(to);

        // check amount
        checkAmount(amount);

        // check wuhanddc uri
        checkDdcURI(ddcURI);

        // check nonce
        checkNonce(nonce);

        // check deadline
        checkDeadline(deadline);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(amount);
        arrayList.add(ddcURI);
        arrayList.add(data);
        arrayList.add(nonce);
        arrayList.add(deadline);
        arrayList.add(sign);

        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.META_SAFE_MINT, DDC1155Contract);

        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易DDC的批量创建
     *
     * @param to      接收者账户
     * @param ddcInfo DDC信息
     * @param data    附加数据
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String metaSafeMintBatch(String sender, String to, Multimap<BigInteger, String> ddcInfo, byte[] data, BigInteger nonce, BigInteger deadline, byte[] sign) throws Exception {
        return metaSafeMintBatch(sender, to, ddcInfo, data, nonce, deadline, sign, null);
    }

    /**
     * 元交易DDC的批量创建
     *
     * @param to      接收者账户
     * @param ddcInfo DDC信息
     * @param data    附加数据
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @param options configuration
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String metaSafeMintBatch(String sender, String to, Multimap<BigInteger, String> ddcInfo, byte[] data, BigInteger nonce, BigInteger deadline, byte[] sign, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check to
        checkTo(to);

        // check nonce
        checkNonce(nonce);

        // check deadline
        checkDeadline(deadline);

        if (null == ddcInfo || ddcInfo.isEmpty()) {
            throw new DDCException(ErrorMessage.IS_EMPTY, "ddcAddr");
        }
        List<String> amountList = new ArrayList<>();
        List<String> ddcURI = new ArrayList<>();

        ddcInfo.forEach((key, value) -> {
            // 验证accName不为空
            if (null == key || BigInteger.ZERO.compareTo(key) >= 0) {
                throw new DDCException(ErrorMessage.IS_EMPTY, "amount");
            }

            amountList.add(key.toString());
            ddcURI.add(value);
        });

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(amountList.stream().collect(Collectors.joining(",")));
        arrayList.add(ddcURI.stream().collect(Collectors.joining(",")));
        arrayList.add(data);
        arrayList.add(nonce);
        arrayList.add(deadline);
        arrayList.add(sign);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.META_SAFE_MINT_BATCH, DDC1155Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易DDC的转移
     *
     * @param from   拥有者账户
     * @param to     接收者账户
     * @param ddcId  DDCID
     * @param amount 数量
     * @param data   附加数据
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 转移结果
     * @throws Exception Exception
     */
    public String metaSafeTransferFrom(String sender, String from, String to, BigInteger ddcId, BigInteger amount, byte[] data, BigInteger nonce, BigInteger deadline, byte[] sign) throws Exception {
        return metaSafeTransferFrom(sender, from, to, ddcId, amount, data, nonce, deadline, sign, null);
    }

    /**
     * 元交易DDC的转移
     *
     * @param from    拥有者账户
     * @param to      接收者账户
     * @param ddcId   DDCID
     * @param amount  数量
     * @param data    附加数据
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @param options configuration
     * @return 转移结果
     * @throws Exception Exception
     */
    public String metaSafeTransferFrom(String sender, String from, String to, BigInteger ddcId, BigInteger amount, byte[] data, BigInteger nonce, BigInteger deadline, byte[] sign, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check from
        checkFrom(from);

        // check to
        checkTo(to);

        // check wuhanddc id
        checkDdcId(ddcId);

        // check nonce
        checkNonce(nonce);

        // check deadline
        checkDeadline(deadline);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);
        arrayList.add(to);
        arrayList.add(ddcId);
        arrayList.add(amount);
        arrayList.add(data);
        arrayList.add(nonce);
        arrayList.add(deadline);
        arrayList.add(sign);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.META_SAFE_TRANSFER_FROM, DDC1155Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易DDC的批量转移
     *
     * @param from 拥有者账户
     * @param to   接收者账户
     * @param ddcInfos 拥有者DDCID集合
     * @param data 附加数据
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String metaSafeBatchTransferFrom(String sender, String from, String to, Map<BigInteger, BigInteger> ddcInfos, byte[] data, BigInteger nonce, BigInteger deadline, byte[] sign) throws Exception {
        return metaSafeBatchTransferFrom(sender, from, to, ddcInfos, data, nonce, deadline, sign, null);
    }

    /**
     * 元交易DDC的批量转移
     *
     * @param from    拥有者账户
     * @param to      接收者账户
     * @param ddcInfos    拥有者DDCID集合
     * @param data    附加数据
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @param options configuration
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String metaSafeBatchTransferFrom(String sender, String from, String to, Map<BigInteger, BigInteger> ddcInfos, byte[] data, BigInteger nonce, BigInteger deadline, byte[] sign, RequestOptions options) throws Exception {

        // check sender
        checkSender(sender);

        // check from
        checkFrom(from);

        // check to
        checkTo(to);

        // check nonce
        checkNonce(nonce);

        // check deadline
        checkDeadline(deadline);

        if (null == ddcInfos || ddcInfos.isEmpty()) {
            throw new DDCException(ErrorMessage.IS_EMPTY, "ddcId");
        }

        // input params
        ArrayList<String> ddcIds = new ArrayList();
        ArrayList<String> amounts = new ArrayList();

        ddcInfos.forEach((key, value) -> {
            // 验证accName不为空
            if (null == key || BigInteger.ZERO.compareTo(value) >= 0) {
                throw new DDCException(ErrorMessage.IS_EMPTY, "amount");
            }

            ddcIds.add(String.valueOf(key));
            amounts.add(String.valueOf(value));
        });
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);
        arrayList.add(to);
        arrayList.add(ddcIds.stream().collect(Collectors.joining(",")));
        arrayList.add(amounts.stream().collect(Collectors.joining(",")));
        arrayList.add(data);
        arrayList.add(nonce);
        arrayList.add(deadline);
        arrayList.add(sign);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.META_SAFE_BATCH_TRANSFER_FROM, DDC1155Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * DDC的销毁
     *
     * @param owner 拥有者账户
     * @param ddcId DDCID
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String metaBurn(String sender, String owner, BigInteger ddcId, BigInteger nonce, BigInteger deadline, byte[] sign) throws Exception {
        return metaBurn(sender, owner, ddcId, nonce, deadline, sign, null);
    }

    /**
     * DDC的销毁
     *
     * @param owner   拥有者账户
     * @param ddcId   DDCID
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @param options configuration
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String metaBurn(String sender, String owner, BigInteger ddcId, BigInteger nonce, BigInteger deadline, byte[] sign, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check owner
        checkOwner(owner);

        // check nonce
        checkNonce(nonce);

        // check deadline
        checkDeadline(deadline);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(ddcId);
        arrayList.add(nonce);
        arrayList.add(deadline);
        arrayList.add(sign);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.META_BURN, DDC1155Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * DDC的批量销毁
     *
     * @param owner  拥有者账户
     * @param ddcIds DDCID集合
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String metaBurnBatch(String sender, String owner, List<BigInteger> ddcIds, BigInteger nonce, BigInteger deadline, byte[] sign) throws Exception {
        return metaBurnBatch(sender, owner, ddcIds, nonce, deadline, sign, null);
    }

    /**
     * DDC的批量销毁
     *
     * @param owner   拥有者账户
     * @param ddcIds  DDCID集合
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @param options configuration
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String metaBurnBatch(String sender, String owner, List<BigInteger> ddcIds, BigInteger nonce, BigInteger deadline, byte[] sign, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check owner
        checkOwner(owner);

        // check nonce
        checkNonce(nonce);

        // check deadline
        checkDeadline(deadline);

        if (null == ddcIds || ddcIds.isEmpty()) {
            throw new DDCException(ErrorMessage.IS_EMPTY, "ddcId");
        }
        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(ddcIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
        arrayList.add(nonce);
        arrayList.add(deadline);
        arrayList.add(sign);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.META_BURN_BATCH, DDC1155Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 运营方可以通过调用该方法对旧DDC所对应的拥有者数据同步给链上。
     * @param sender
     * @param ddcIds
     * @param owners
     * @return
     * @throws Exception
     */
    public String syncDDCOwners(String sender, List<BigInteger> ddcIds, List<List<String>> owners) throws Exception {
        return syncDDCOwners(sender, ddcIds, owners, null);
    }


    /**
     * 运营方可以通过调用该方法对旧DDC所对应的拥有者数据同步给链上。
     * @param sender
     * @param ddcIds
     * @param owners
     * @param options
     * @return
     * @throws Exception
     */
    public String syncDDCOwners(String sender, List<BigInteger> ddcIds, List<List<String>> owners, RequestOptions options) throws Exception {
        //check params length
        checkLen(ddcIds.size());

        //check params length
        checkLen(owners.size());

        // check sender
        checkSender(sender);

        List<String> ddcIdsList = new ArrayList<>();
        // check did
        ddcIds.forEach(ddcId -> {
            checkDdcId(ddcId);
            ddcIdsList.add(String.valueOf(ddcId));
        });

        // check owner
        owners.forEach(ownerList -> {
            ownerList.forEach(this::checkOwner);
        });

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcIdsList.stream().collect(Collectors.joining(",")));
        arrayList.add(owners);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.SYNC_DDC_OWNERS, DDC1155Contract);
        return (String) respJsonRpcBean.getResult();
    }

}
