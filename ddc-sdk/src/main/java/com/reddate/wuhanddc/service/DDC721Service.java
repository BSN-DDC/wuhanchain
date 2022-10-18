package com.reddate.wuhanddc.service;

import com.reddate.wuhanddc.constant.DDC721Functions;
import com.reddate.wuhanddc.constant.ErrorMessage;
import com.reddate.wuhanddc.dto.config.DDCContract;
import com.reddate.wuhanddc.dto.wuhanchain.RespJsonRpcBean;
import com.reddate.wuhanddc.exception.DDCException;
import com.reddate.wuhanddc.net.RequestOptions;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.reddate.wuhanddc.constant.ContractConfig.DDCContracts;


/**
 * wuhanddc 721
 *
 * @author wxq
 */
public class DDC721Service extends BaseService {

    public static DDCContract DDC721Contract;

    public DDC721Service() {
        DDC721Contract = DDCContracts.stream().filter(t -> "721".equals(t.getConfigType())).findFirst().orElse(null);
    }

    /**
     * 生成
     *
     * @param sender
     * @param to     授权者账户
     * @param ddcURI DDC资源标识符
     * @return 交易哈希
     * @throws Exception
     */
    public String mint(String sender, String to, String ddcURI) throws Exception {
        return mint(sender, to, ddcURI, null);
    }

    /**
     * 生成
     *
     * @param sender
     * @param to     授权者账户
     * @param ddcURI DDC资源标识符
     * @return 交易哈希
     * @throws Exception
     */
    public String mint(String sender, String to, String ddcURI, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check to
        checkTo(to);

        // check wuhanddc uri
        checkDdcURI(ddcURI);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(ddcURI);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.MINT, DDC721Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 批量生成
     *
     * @param sender
     * @param to     授权者账户
     * @param ddcURIs DDC资源标识符
     * @return 交易哈希
     * @throws Exception
     */
    public String mintBatch(String sender, String to, List<String> ddcURIs) throws Exception {
        return mintBatch(sender, to, ddcURIs, null);
    }

    /**
     * 批量生成
     *
     * @param sender
     * @param to     授权者账户
     * @param ddcURIs DDC资源标识符
     * @return 交易哈希
     * @throws Exception
     */
    public String mintBatch(String sender, String to, List<String> ddcURIs, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check to
        checkTo(to);

        // check wuhanddc uri
        for (String ddcURI : ddcURIs) {
            checkDdcURI(ddcURI);
        }

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(ddcURIs.stream().collect(Collectors.joining(",")));

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.MINT_BATCH, DDC721Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 安全生成
     *
     * @param sender
     * @param to     授权者账户
     * @param ddcURI DDC资源标识符
     * @param data   附加数据
     * @return 交易哈希
     * @throws Exception
     */
    public String safeMint(String sender, String to, String ddcURI, byte[] data) throws Exception {
        return safeMint(sender, to, ddcURI, data, null);
    }

    /**
     * 安全生成
     *
     * @param sender
     * @param to     授权者账户
     * @param ddcURI DDC资源标识符
     * @param data   附加数据
     * @return 交易哈希
     * @throws Exception
     */
    public String safeMint(String sender, String to, String ddcURI, byte[] data, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check to
        checkTo(to);

        // check wuhanddc uri
        checkDdcURI(ddcURI);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(ddcURI);
        arrayList.add(data);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.SAFE_MINT, DDC721Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 安全批量生成
     *
     * @param sender
     * @param to     授权者账户
     * @param ddcURIs DDC资源标识符
     * @return 交易哈希
     * @throws Exception
     */
    public String safeMintBatch(String sender, String to, List<String> ddcURIs, byte[] data) throws Exception {
        return safeMintBatch(sender, to, ddcURIs, data, null);
    }

    /**
     * 安全批量生成
     *
     * @param sender
     * @param to     授权者账户
     * @param ddcURIs DDC资源标识符
     * @return 交易哈希
     * @throws Exception
     */
    public String safeMintBatch(String sender, String to, List<String> ddcURIs, byte[] data, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check to
        checkTo(to);

        // check wuhanddc uri
        for (String ddcURI : ddcURIs) {
            checkDdcURI(ddcURI);
        }

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(ddcURIs.stream().collect(Collectors.joining(",")));
        arrayList.add(data);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.SAFE_MINT_BATCH, DDC721Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 授权
     *
     * @param sender
     * @param to     授权者账户
     * @param ddcId  DDC唯一标识
     * @return
     * @throws Exception
     */
    public String approve(String sender, String to, BigInteger ddcId) throws Exception {
        return approve(sender, to, ddcId, null);
    }

    /**
     * 授权
     *
     * @param sender
     * @param to     授权者账户
     * @param ddcId  DDC唯一标识
     * @return
     * @throws Exception
     */
    public String approve(String sender, String to, BigInteger ddcId, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check to
        checkTo(to);

        // check wuhanddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(ddcId);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.APPROVE, DDC721Contract);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 授权查询
     *
     * @param ddcId
     * @return
     * @throws Exception
     */
    public String getApproved(BigInteger ddcId) throws Exception {
        return getApproved(ddcId, null);
    }

    /**
     * 授权查询
     *
     * @param ddcId
     * @return
     * @throws Exception
     */
    public String getApproved(BigInteger ddcId, RequestOptions options) throws Exception {
        // check ddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC721Functions.GET_APPROVED, DDC721Contract);

        return (String) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 账户授权
     *
     * @param sender
     * @param operator 授权者账户
     * @param approved 授权标识
     * @return 交易hash
     * @throws Exception
     */
    public String setApprovalForAll(String sender, String operator, Boolean approved) throws Exception {
        return setApprovalForAll(sender, operator, approved, null);
    }

    /**
     * 账户授权
     *
     * @param sender
     * @param operator 授权者账户
     * @param approved 授权标识
     * @return 交易hash
     * @throws Exception
     */
    public String setApprovalForAll(String sender, String operator, Boolean approved, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check operator
        checkOperator(operator);

        if (null == approved) {
            throw new DDCException(ErrorMessage.NOT_STANDARD_ADDRESS_FORMAT);
        }

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(operator);
        arrayList.add(approved);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.SET_APPROVAL_FOR_ALL, DDC721Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 账户授权查询
     *
     * @param owner    拥有者账户
     * @param operator 授权者账户
     * @return 授权标识
     * @throws Exception
     */
    public Boolean isApprovedForAll(String owner, String operator) throws Exception {
        return isApprovedForAll(owner, operator, null);
    }

    /**
     * 账户授权查询
     *
     * @param owner    拥有者账户
     * @param operator 授权者账户
     * @return 授权标识
     * @throws Exception
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
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC721Functions.IS_APPROVED_FOR_ALL, DDC721Contract);
        return (Boolean) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 安全转移
     *
     * @param sender
     * @param from   拥有者账户
     * @param to     授权者账户
     * @param ddcId  DDC唯一标识
     * @param data   附加数据
     * @return 交易hash
     * @throws Exception
     */
    public String safeTransferFrom(String sender, String from, String to, BigInteger ddcId, byte[] data) throws Exception {
        return safeTransferFrom(sender, from, to, ddcId, data, null);
    }

    /**
     * 安全转移
     *
     * @param sender
     * @param from   拥有者账户
     * @param to     授权者账户
     * @param ddcId  DDC唯一标识
     * @param data   附加数据
     * @return 交易hash
     * @throws Exception
     */
    public String safeTransferFrom(String sender, String from, String to, BigInteger ddcId, byte[] data, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // checkFrom
        checkFrom(from);

        // check to
        checkTo(to);

        // check wuhanddc Id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);
        arrayList.add(to);
        arrayList.add(ddcId);
        arrayList.add(data);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.SAFE_TRANSFER_FROM, DDC721Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 转移
     *
     * @param sender
     * @param from   拥有者账户
     * @param to     接收者账户
     * @param ddcId  ddc唯一标识
     * @return 交易hash
     * @throws Exception
     */
    public String transferFrom(String sender, String from, String to, BigInteger ddcId) throws Exception {
        return transferFrom(sender, from, to, ddcId, null);
    }

    /**
     * 转移
     *
     * @param sender
     * @param from   拥有者账户
     * @param to     接收者账户
     * @param ddcId  ddc唯一标识
     * @return 交易hash
     * @throws Exception
     */
    public String transferFrom(String sender, String from, String to, BigInteger ddcId, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // checkFrom
        checkFrom(from);

        // check to
        checkTo(to);

        // check wuhanddc Id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);
        arrayList.add(to);
        arrayList.add(ddcId);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.TRANSFER_FROM, DDC721Contract);
        return (String) respJsonRpcBean.getResult();
    }


    /**
     * 冻结
     *
     * @param sender
     * @param ddcId  wuhanddc 唯一标识
     * @return 交易hash
     * @throws Exception
     */
    public String freeze(String sender, BigInteger ddcId) throws Exception {
        return freeze(sender, ddcId, null);
    }

    /**
     * 冻结
     *
     * @param sender
     * @param ddcId  wuhanddc 唯一标识
     * @return 交易hash
     * @throws Exception
     */
    public String freeze(String sender, BigInteger ddcId, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check wuhanddc Id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.FREEZE, DDC721Contract);
        return (String) respJsonRpcBean.getResult();
    }


    /**
     * 解冻
     *
     * @param sender
     * @param ddcId  wuhanddc 唯一标识
     * @return 交易hash
     * @throws Exception
     */
    public String unFreeze(String sender, BigInteger ddcId) throws Exception {
        return unFreeze(sender, ddcId, null);
    }

    /**
     * 解冻
     *
     * @param sender
     * @param ddcId  wuhanddc 唯一标识
     * @return 交易hash
     * @throws Exception
     */
    public String unFreeze(String sender, BigInteger ddcId, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        //check wuhanddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.UNFREEZE, DDC721Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 销毁
     *
     * @param ddcId DDC唯一标识
     * @return 交易hash
     * @throws Exception
     */
    public String burn(String sender, BigInteger ddcId) throws Exception {
        return burn(sender, ddcId, null);
    }

    /**
     * 销毁
     *
     * @param sender
     * @param ddcId  DDC唯一标识
     * @return 交易hash
     * @throws Exception
     */
    public String burn(String sender, BigInteger ddcId, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check wuhanddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.BURN, DDC721Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 查询数量
     *
     * @param owner 拥有者账户
     * @return ddc的数量
     * @throws Exception
     */
    public BigInteger balanceOf(String owner) throws Exception {
        return balanceOf(owner, null);
    }

    /**
     * 查询数量
     *
     * @param owner 拥有者账户
     * @return ddc的数量
     * @throws Exception
     */
    public BigInteger balanceOf(String owner, RequestOptions options) throws Exception {
        // check owner
        checkOwner(owner);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC721Functions.BALANCE_OF, DDC721Contract);
        return (BigInteger) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 查询拥有者
     *
     * @param ddcId ddc唯一标识
     * @return 拥有者账户
     * @throws Exception
     */
    public String ownerOf(BigInteger ddcId) throws Exception {
        return ownerOf(ddcId, null);
    }

    /**
     * 查询拥有者
     *
     * @param ddcId ddc唯一标识
     * @return 拥有者账户
     * @throws Exception
     */
    public String ownerOf(BigInteger ddcId, RequestOptions options) throws Exception {
        // check wuhanddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC721Functions.OWNER_OF, DDC721Contract);
        return (String) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 获取名称
     *
     * @return ddc运营方名称
     * @throws Exception
     */
    public String name() throws Exception {
        return name(null);
    }

    /**
     * 获取名称
     *
     * @return ddc运营方名称
     * @throws Exception
     */
    public String name(RequestOptions options) throws Exception {
        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, null, DDC721Functions.NAME, DDC721Contract);
        return (String) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 获取符号
     *
     * @return
     * @throws Exception
     */
    public String symbol() throws Exception {
        return symbol(null);
    }

    /**
     * 获取符号
     *
     * @return
     * @throws Exception
     */
    public String symbol(RequestOptions options) throws Exception {
        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, null, DDC721Functions.SYMBOL, DDC721Contract);
        return (String) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 获取DDCURI
     *
     * @return DDC资源标识符
     * @throws Exception
     */
    public String ddcURI(BigInteger ddcId) throws Exception {
        return ddcURI(ddcId, null);
    }

    /**
     * 获取DDCURI
     *
     * @return DDC资源标识符
     * @throws Exception
     */
    public String ddcURI(BigInteger ddcId, RequestOptions options) throws Exception {
        // check wuhanddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC721Functions.DDC_URI, DDC721Contract);
        return (String) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 设置URI DDC拥有者和授权者可调用该方法
     *
     * @param sender
     * @param ddcId
     * @param ddcURI
     * @return
     * @throws Exception
     */
    public String setURI(String sender, BigInteger ddcId, String ddcURI) throws Exception {
        return setURI(sender, ddcId, ddcURI, null);
    }

    /**
     * 设置URI DDC拥有者和授权者可调用该方法
     *
     * @param sender
     * @param ddcId
     * @param ddcURI
     * @return
     * @throws Exception
     */
    public String setURI(String sender, BigInteger ddcId, String ddcURI, RequestOptions options) throws Exception {
        // check wuhanddc id
        checkDdcId(ddcId);

        // check wuhanddc uri
        checkDdcURI(ddcURI);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);
        arrayList.add(ddcURI);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.SET_URI, DDC721Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 合约拥有者调用该方法对721的名称及符号进行设置
     *
     * @param sender
     * @param name
     * @param symbol
     * @return
     * @throws Exception
     */
    public String setNameAndSymbol(String sender, String name, String symbol) throws Exception {
        return setNameAndSymbol(sender, name, symbol, null);
    }

    /**
     * 合约拥有者调用该方法对721的名称及符号进行设置
     *
     * @param sender
     * @param name
     * @param symbol
     * @return
     * @throws Exception
     */
    public String setNameAndSymbol(String sender, String name, String symbol, RequestOptions options) throws Exception {
        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(name);
        arrayList.add(symbol);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.SET_NAME_AND_SYMBOL, DDC721Contract);
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
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, null, DDC721Functions.GET_LATEST_DDC_ID, DDC721Contract);
        return (BigInteger) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 通过调用该方法对签名者账户所对应的最新nonce值进行查询，注：此查询只适用于发起元交易处理业务所对应的nonce值查询
     *
     * @param from
     * @return
     * @throws Exception
     */
    public BigInteger getNonce(String from) throws Exception {
        return getNonce(from, null);
    }

    /**
     * 通过调用该方法对签名者账户所对应的最新nonce值进行查询，注：此查询只适用于发起元交易处理业务所对应的nonce值查询
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
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC721Functions.GET_NONCE, DDC721Contract);

        return (BigInteger) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 元交易生成
     *
     * @param sender
     * @param to     授权者账户
     * @param ddcURI DDC资源标识符
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易哈希
     * @throws Exception
     */
    public String metaMint(String sender, String to, String ddcURI, BigInteger nonce, BigInteger deadline, byte[] sign) throws Exception {
        return metaMint(sender, to, ddcURI, nonce, deadline, sign, null);
    }

    /**
     * 元交易生成
     *
     * @param sender
     * @param to     授权者账户
     * @param ddcURI DDC资源标识符
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易哈希
     * @throws Exception
     */
    public String metaMint(String sender, String to, String ddcURI, BigInteger nonce, BigInteger deadline, byte[] sign, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check to
        checkTo(to);

        // check wuhanddc uri
        checkDdcURI(ddcURI);

        // check nonce
        checkNonce(nonce);

        // check deadline
        checkDeadline(deadline);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(ddcURI);
        arrayList.add(nonce);
        arrayList.add(deadline);
        arrayList.add(sign);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.META_MINT, DDC721Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易安全生成
     *
     * @param sender
     * @param to     授权者账户
     * @param ddcURI DDC资源标识符
     * @param data 附加数据
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易哈希
     * @throws Exception
     */
    public String metaSafeMint(String sender, String to, String ddcURI, byte[] data, BigInteger nonce, BigInteger deadline, byte[] sign) throws Exception {
        return metaSafeMint(sender, to, ddcURI, data, nonce, deadline, sign, null);
    }

    /**
     * 元交易安全生成
     *
     * @param sender
     * @param to     授权者账户
     * @param ddcURI DDC资源标识符
     * @param data 附加数据
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易哈希
     * @throws Exception
     */
    public String metaSafeMint(String sender, String to, String ddcURI, byte[] data, BigInteger nonce, BigInteger deadline, byte[] sign, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check to
        checkTo(to);

        // check wuhanddc uri
        checkDdcURI(ddcURI);

        // check nonce
        checkNonce(nonce);

        // check deadline
        checkDeadline(deadline);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(ddcURI);
        arrayList.add(data);
        arrayList.add(nonce);
        arrayList.add(deadline);
        arrayList.add(sign);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.META_SAFE_MINT, DDC721Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易批量生成
     *
     * @param sender
     * @param to     授权者账户
     * @param ddcURIs DDC资源标识符
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易哈希
     * @throws Exception
     */
    public String metaMintBatch(String sender, String to, List<String> ddcURIs, BigInteger nonce, BigInteger deadline, byte[] sign) throws Exception {
        return metaMintBatch(sender, to, ddcURIs, nonce, deadline, sign, null);
    }

    /**
     * 元交易批量生成
     *
     * @param sender
     * @param to     授权者账户
     * @param ddcURIs DDC资源标识符
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易哈希
     * @throws Exception
     */
    public String metaMintBatch(String sender, String to, List<String> ddcURIs, BigInteger nonce, BigInteger deadline, byte[] sign, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check to
        checkTo(to);

        // check wuhanddc uri
        for (String ddcURI : ddcURIs) {
            checkDdcURI(ddcURI);
        }

        // check nonce
        checkNonce(nonce);

        // check deadline
        checkDeadline(deadline);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(ddcURIs.stream().collect(Collectors.joining(",")));
        arrayList.add(nonce);
        arrayList.add(deadline);
        arrayList.add(sign);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.META_MINT_BATCH, DDC721Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易安全批量生成
     *
     * @param sender
     * @param to     授权者账户
     * @param ddcURIs DDC资源标识符
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易哈希
     * @throws Exception
     */
    public String metaSafeMintBatch(String sender, String to, List<String> ddcURIs, byte[] data, BigInteger nonce, BigInteger deadline, byte[] sign) throws Exception {
        return metaSafeMintBatch(sender, to, ddcURIs, data, nonce, deadline, sign, null);
    }

    /**
     * 元交易安全批量生成
     *
     * @param sender
     * @param to     授权者账户
     * @param ddcURIs DDC资源标识符
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易哈希
     * @throws Exception
     */
    public String metaSafeMintBatch(String sender, String to, List<String> ddcURIs, byte[] data, BigInteger nonce, BigInteger deadline, byte[] sign, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check to
        checkTo(to);

        // check wuhanddc uri
        for (String ddcURI : ddcURIs) {
            checkDdcURI(ddcURI);
        }

        // check nonce
        checkNonce(nonce);

        // check deadline
        checkDeadline(deadline);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(ddcURIs.stream().collect(Collectors.joining(",")));
        arrayList.add(data);
        arrayList.add(nonce);
        arrayList.add(deadline);
        arrayList.add(sign);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.META_SAFE_MINT_BATCH, DDC721Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易转移
     *
     * @param sender
     * @param from   拥有者账户
     * @param to     接收者账户
     * @param ddcId  ddc唯一标识
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易hash
     * @throws Exception
     */
    public String metaTransferFrom(String sender, String from, String to, BigInteger ddcId, BigInteger nonce, BigInteger deadline, byte[] sign) throws Exception {
        return metaTransferFrom(sender, from, to, ddcId, nonce, deadline, sign, null);
    }

    /**
     * 元交易转移
     *
     * @param sender
     * @param from   拥有者账户
     * @param to     接收者账户
     * @param ddcId  ddc唯一标识
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易hash
     * @throws Exception
     */
    public String metaTransferFrom(String sender, String from, String to, BigInteger ddcId, BigInteger nonce, BigInteger deadline, byte[] sign, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // checkFrom
        checkFrom(from);

        // check to
        checkTo(to);

        // check wuhanddc Id
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
        arrayList.add(nonce);
        arrayList.add(deadline);
        arrayList.add(sign);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.META_TRANSFER_FROM, DDC721Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易安全转移
     *
     * @param sender
     * @param from   拥有者账户
     * @param to     授权者账户
     * @param ddcId  DDC唯一标识
     * @param data   附加数据
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易hash
     * @throws Exception
     */
    public String metaSafeTransferFrom(String sender, String from, String to, BigInteger ddcId, byte[] data, BigInteger nonce, BigInteger deadline, byte[] sign) throws Exception {
        return metaSafeTransferFrom(sender, from, to, ddcId, data, nonce,deadline, sign, null);
    }

    /**
     * 元交易安全转移
     *
     * @param sender
     * @param from   拥有者账户
     * @param to     授权者账户
     * @param ddcId  DDC唯一标识
     * @param data   附加数据
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易hash
     * @throws Exception
     */
    public String metaSafeTransferFrom(String sender, String from, String to, BigInteger ddcId, byte[] data, BigInteger nonce, BigInteger deadline, byte[] sign, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // checkFrom
        checkFrom(from);

        // check to
        checkTo(to);

        // check wuhanddc Id
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
        arrayList.add(data);
        arrayList.add(nonce);
        arrayList.add(deadline);
        arrayList.add(sign);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.META_SAFE_TRANSFER_FROM, DDC721Contract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易销毁
     *
     * @param ddcId DDC唯一标识
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易hash
     * @throws Exception
     */
    public String metaBurn(String sender, BigInteger ddcId, BigInteger nonce, BigInteger deadline, byte[] sign) throws Exception {
        return metaBurn(sender, ddcId, nonce, deadline, sign, null);
    }

    /**
     * 元交易销毁
     *
     * @param sender
     * @param ddcId  DDC唯一标识
     * @param nonce 校验值
     * @param deadline 有效期
     * @param sign 签名
     * @return 交易hash
     * @throws Exception
     */
    public String metaBurn(String sender, BigInteger ddcId, BigInteger nonce, BigInteger deadline, byte[] sign, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check wuhanddc id
        checkDdcId(ddcId);

        // check nonce
        checkNonce(nonce);

        // check deadline
        checkDeadline(deadline);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);
        arrayList.add(nonce);
        arrayList.add(deadline);
        arrayList.add(sign);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.META_BURN, DDC721Contract);
        return (String) respJsonRpcBean.getResult();
    }

}
