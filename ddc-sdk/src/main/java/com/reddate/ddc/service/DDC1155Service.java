package com.reddate.ddc.service;

import com.google.common.collect.Multimap;
import com.reddate.ddc.constant.DDC1155Functions;
import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.dto.config.DDCContract;
import com.reddate.ddc.dto.wuhanchain.RespJsonRpcBean;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.net.RequestOptions;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class DDC1155Service extends BaseService {
    public volatile static DDCContract ddcContract;

    public DDC1155Service(DDCContract contractConfiguration) {
        ddcContract = contractConfiguration;
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
        return safeMint(sender, to, amount, ddcURI, data, RequestOptions.builder(DDC1155Service.class).build());
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

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(amount);
        arrayList.add(ddcURI);
        arrayList.add(data);

        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.Mint);

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
        return safeMintBatch(sender, to, ddcInfo, data, RequestOptions.builder(DDC1155Service.class).build());
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
            throw new DDCException(ErrorMessage.DDC_ADDR_IS_EMPTY);
        }
        List<String> amountList = new ArrayList<>();
        List<String> ddcURI = new ArrayList<>();

        ddcInfo.forEach((key, value) -> {
            // 验证accName不为空
            if (null == key || BigInteger.ZERO.compareTo(key) >= 0) {
                throw new DDCException(ErrorMessage.AMOUNT_IS_EMPTY);
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
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.MINT_BATCH);
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
        return setApprovalForAll(sender, operator, approved, RequestOptions.builder(DDC1155Service.class).build());
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
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.SET_APPROVAL_FOR_ALL);
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
        return isApprovedForAll(owner, operator, RequestOptions.builder(DDC1155Service.class).build());
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
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC1155Functions.IS_APPROVED_FOR_ALL);

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
        return safeTransferFrom(sender, from, to, ddcId, amount, data, RequestOptions.builder(DDC1155Service.class).build());
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

        // check ddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);
        arrayList.add(to);
        arrayList.add(ddcId);
        arrayList.add(amount);
        arrayList.add(data);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.SAFE_TRANSFER_FROM);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * DDC的批量转移
     *
     * @param from 拥有者账户
     * @param to   接收者账户
     * @param ddcs 拥有者DDCID集合
     * @param data 附加数据
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String safeBatchTransferFrom(String sender, String from, String to, Map<BigInteger, BigInteger> ddcs, ArrayList<byte[]> data) throws Exception {
        return safeBatchTransferFrom(sender, from, to, ddcs, data, RequestOptions.builder(DDC1155Service.class).build());
    }

    /**
     * DDC的批量转移
     *
     * @param from    拥有者账户
     * @param to      接收者账户
     * @param ddcs    拥有者DDCID集合
     * @param data    附加数据
     * @param options configuration
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String safeBatchTransferFrom(String sender, String from, String to, Map<BigInteger, BigInteger> ddcs, ArrayList<byte[]> data, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);
        // check from
        checkFrom(from);
        // check to
        checkTo(to);
        if (null == ddcs || ddcs.isEmpty()) {
            throw new DDCException(ErrorMessage.DDC_ID_LT_EMPTY);
        }

        // input params
        ArrayList<String> ddcIds = new ArrayList();
        ArrayList<String> amounts = new ArrayList();

        ddcs.forEach((key, value) -> {
            // 验证accName不为空
            if (null == key || BigInteger.ZERO.compareTo(value) >= 0) {
                throw new DDCException(ErrorMessage.AMOUNT_IS_EMPTY);
            }

            ddcIds.add(String.valueOf(key));
            amounts.add(String.valueOf(value));
        });
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);
        arrayList.add(to);
        arrayList.add(ddcIds.stream().collect(Collectors.joining(",")));
        arrayList.add(amounts.stream().collect(Collectors.joining(",")));
        arrayList.add(data.stream().map(Object::toString).collect(Collectors.joining(",")));

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.SAFE_BATCH_TRANSFER_FROM);
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
        return freeze(sender, ddcId, RequestOptions.builder(DDC1155Service.class).build());
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
        // check ddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.FREEZE);
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
        return unFreeze(sender, ddcId, RequestOptions.builder(DDC1155Service.class).build());
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

        // check ddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.UNFREEZE);
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
        return burn(sender, owner, ddcId, RequestOptions.builder(DDC1155Service.class).build());
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
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.BURN);
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
        return burnBatch(sender, owner, ddcIds, RequestOptions.builder(DDC1155Service.class).build());
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
            throw new DDCException(ErrorMessage.DDC_ID_LT_EMPTY);
        }
        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(ddcIds.stream().map(String::valueOf).collect(Collectors.joining(",")));

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, DDC1155Functions.BURN_BATCH);
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
        return balanceOf(owner, ddcId, RequestOptions.builder(DDC1155Service.class).build());
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

        // check ddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(ddcId);

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC1155Functions.BALANCE_OF);
        return (BigInteger) inputAndOutputResult.getResult().get(0).getData();
    }

    /***
     * 批量查询账户拥有的DDC的数量
     * @param ddcs ddc owner collection
     * @return 拥有者账户所对应的每个DDCID所拥用的数量
     * @throws Exception
     */
    public List<BigInteger> balanceOfBatch(Multimap<String, BigInteger> ddcs) throws Exception {
        return balanceOfBatch(ddcs, RequestOptions.builder(DDC1155Service.class).build());
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
            throw new DDCException(ErrorMessage.DDC_ID_LT_EMPTY);
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
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC1155Functions.BALANCE_OF_BATCH);
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
        return ddcURI(ddcId, RequestOptions.builder(DDC1155Service.class).build());
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

        // check ddc id
        checkDdcId(ddcId);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, DDC1155Functions.DDC_URI);
        return (String) inputAndOutputResult.getResult().get(0).getData();
    }


}
