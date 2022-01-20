package com.reddate.ddc.service;

import com.reddate.ddc.constant.DDC721Functions;
import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.dto.config.DDCContract;
import com.reddate.ddc.dto.wuhanchain.RespJsonRpcBean;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.net.RequestOptions;
import org.fisco.bcos.web3j.crypto.WalletUtils;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.fisco.bcos.web3j.utils.Strings;

import java.math.BigInteger;
import java.util.ArrayList;


/**
 * @author wxq
 */
public class DDC721Service extends BaseService {

    public volatile static DDCContract ddcContract;

    public DDC721Service(DDCContract contractConfiguration) {
        ddcContract = contractConfiguration;
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
        return mint(sender, to, ddcURI, RequestOptions.builder(DDC721Service.class).build());
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

        if (Strings.isEmpty(ddcURI)) {
            throw new DDCException(ErrorMessage.DDC_URI_IS_EMPTY);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(ddcURI);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.MINT);
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
        return safeMint(sender, to, ddcURI, data, RequestOptions.builder(DDC721Service.class).build());
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
        checkSender(sender);
        if (Strings.isEmpty(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }
        if (Strings.isEmpty(ddcURI)) {
            throw new DDCException(ErrorMessage.DDC_URI_IS_EMPTY);
        }
        if (!WalletUtils.isValidAddress(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(ddcURI);
        arrayList.add(data);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.SAFE_MINT);
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
        return approve(sender, to, ddcId, RequestOptions.builder(DDC721Service.class).build());
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

        // check ddc id
        checkDdcId(ddcId);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(ddcId);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.APPROVE);

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
        return getApproved(ddcId, RequestOptions.builder(DDC721Service.class).build());
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
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC721Functions.GET_APPROVED);

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
        return setApprovalForAll(sender, operator, approved, RequestOptions.builder(DDC721Service.class).build());
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
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(operator);
        arrayList.add(approved);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.SET_APPROVAL_FOR_ALL);

        resultCheck(respJsonRpcBean);
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
        return isApprovedForAll(owner, operator, RequestOptions.builder(DDC721Service.class).build());
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
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC721Functions.IS_APPROVED_FOR_ALL);

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
        return safeTransferFrom(sender, from, to, ddcId, data, RequestOptions.builder(DDC721Service.class).build());
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
        // check ddc Id
        checkDdcId(ddcId);
        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);
        arrayList.add(to);
        arrayList.add(ddcId);
        arrayList.add(data);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.SAFE_TRANSFER_FROM);

        resultCheck(respJsonRpcBean);
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
        return transferFrom(sender, from, to, ddcId, RequestOptions.builder(DDC721Service.class).build());
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
        // check ddc Id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);
        arrayList.add(to);
        arrayList.add(ddcId);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.TRANSFER_FROM);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }


    /**
     * 冻结
     *
     * @param sender
     * @param ddcId  ddc 唯一标识
     * @return 交易hash
     * @throws Exception
     */
    public String freeze(String sender, BigInteger ddcId) throws Exception {
        return freeze(sender, ddcId, RequestOptions.builder(DDC721Service.class).build());
    }

    /**
     * 冻结
     *
     * @param sender
     * @param ddcId  ddc 唯一标识
     * @return 交易hash
     * @throws Exception
     */
    public String freeze(String sender, BigInteger ddcId, RequestOptions options) throws Exception {
        checkSender(sender);
        if (null == ddcId || BigInteger.ZERO.compareTo(ddcId) >= 0) {
            throw new DDCException(ErrorMessage.DDC_ID_LT_ZERO);
        }
        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.FREEZE);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }


    /**
     * 解冻
     *
     * @param sender
     * @param ddcId  ddc 唯一标识
     * @return 交易hash
     * @throws Exception
     */
    public String unFreeze(String sender, BigInteger ddcId) throws Exception {
        return unFreeze(sender, ddcId, RequestOptions.builder(DDC721Service.class).build());
    }

    /**
     * 解冻
     *
     * @param sender
     * @param ddcId  ddc 唯一标识
     * @return 交易hash
     * @throws Exception
     */
    public String unFreeze(String sender, BigInteger ddcId, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);
        //check ddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.UNFREEZE);

        resultCheck(respJsonRpcBean);
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
        return burn(sender, ddcId, RequestOptions.builder(DDC721Service.class).build());
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
        // check ddc id
        checkDdcId(ddcId);
        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC721Functions.BURN);

        resultCheck(respJsonRpcBean);
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
        return balanceOf(owner, RequestOptions.builder(DDC721Service.class).build());
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
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC721Functions.BALANCE_OF);

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
        return ownerOf(ddcId, RequestOptions.builder(DDC721Service.class).build());
    }

    /**
     * 查询拥有者
     *
     * @param ddcId ddc唯一标识
     * @return 拥有者账户
     * @throws Exception
     */
    public String ownerOf(BigInteger ddcId, RequestOptions options) throws Exception {

        // check ddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC721Functions.OWNER_OF);

        return (String) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 获取名称
     *
     * @return ddc运营方名称
     * @throws Exception
     */
    public String name() throws Exception {
        return name(RequestOptions.builder(DDC721Service.class).build());
    }

    /**
     * 获取名称
     *
     * @return ddc运营方名称
     * @throws Exception
     */
    public String name(RequestOptions options) throws Exception {

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, null, DDC721Functions.NAME);
        return (String) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 获取符号
     *
     * @return
     * @throws Exception
     */
    public String symbol() throws Exception {
        return symbol(RequestOptions.builder(DDC721Service.class).build());
    }

    /**
     * 获取符号
     *
     * @return
     * @throws Exception
     */
    public String symbol(RequestOptions options) throws Exception {
        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, null, DDC721Functions.SYMBOL);
        return (String) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 获取DDCURI
     *
     * @return DDC资源标识符
     * @throws Exception
     */
    public String ddcURI(BigInteger ddcId) throws Exception {
        return ddcURI(ddcId, RequestOptions.builder(DDC721Service.class).build());
    }

    /**
     * 获取DDCURI
     *
     * @return DDC资源标识符
     * @throws Exception
     */
    public String ddcURI(BigInteger ddcId, RequestOptions options) throws Exception {

        // check ddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC721Functions.DDC_URI);
        return (String) inputAndOutputResult.getResult().get(0).getData();
    }


}
