package com.reddate.ddc.service;

import com.reddate.ddc.constant.ChargeFunctions;
import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.dto.config.DDCContract;
import com.reddate.ddc.dto.wuhanchain.RespJsonRpcBean;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.net.RequestOptions;
import com.reddate.ddc.util.HexUtils;
import org.fisco.bcos.web3j.abi.datatypes.Address;
import org.fisco.bcos.web3j.crypto.WalletUtils;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.fisco.bcos.web3j.utils.Strings;

import java.math.BigInteger;
import java.util.ArrayList;

public class ChargeService extends BaseService {

    public volatile static DDCContract ddcContract;

    public ChargeService(DDCContract contractConfiguration) {
        ddcContract = contractConfiguration;
    }

    /**
     * 运营方、平台方调用该接口为所属同一方的同一级别账户或者下级账户充值；
     *
     * @param to     充值账户的地址
     * @param amount 充值金额
     * @return 返回交易哈希
     * @throws Exception
     */
    public String recharge(String to, BigInteger amount) throws Exception {
        return recharge(to, amount, RequestOptions.builder(ChargeService.class).build());
    }

    /**
     * 运营方、平台方调用该接口为所属同一方的同一级别账户或者下级账户充值；
     *
     * @param to      充值账户的地址
     * @param amount  充值金额
     * @param options configuration
     * @return 返回交易哈希
     * @throws Exception
     */
    public String recharge(String to, BigInteger amount, RequestOptions options) throws Exception {
        if (Strings.isEmpty(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }

        if (!WalletUtils.isValidAddress(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }

        if (amount == null || BigInteger.ZERO.compareTo(amount) >= 0) {
            throw new DDCException(ErrorMessage.AMOUNT_IS_EMPTY);
        }

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(to));
        arrayList.add(amount);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(options, arrayList, ChargeFunctions.RECHARGE);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 查询指定账户的余额。
     *
     * @param accAddr 查询的账户地址
     * @return 返回账户所对应的业务费余额
     * @throws Exception
     */
    public BigInteger balanceOf(String accAddr) throws Exception {
        return balanceOf(accAddr, RequestOptions.builder(ChargeService.class).build());
    }

    /**
     * 查询指定账户的余额。
     *
     * @param accAddr 查询的账户地址
     * @param options configuration
     * @return 返回账户所对应的业务费余额
     * @throws Exception
     */
    public BigInteger balanceOf(String accAddr, RequestOptions options) throws Exception {
        if (Strings.isEmpty(accAddr)) {
            throw new DDCException(ErrorMessage.ACC_ADDR_IS_EMPTY);
        }

        if (!WalletUtils.isValidAddress(accAddr)) {
            throw new DDCException(ErrorMessage.ACC_ADDR_IS_NOT_ADDRESS_FORMAT);
        }
        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(accAddr));

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, ChargeFunctions.BALANCE_OF);

        return (BigInteger) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 查询指定的DDC业务主逻辑合约的方法所对应的调用业务费用。
     *
     * @param ddcAddr DDC业务主逻辑合约地址
     * @param sig     Hex格式的合约方法ID
     * @return 返回DDC合约业务费
     * @throws Exception
     */
    public BigInteger queryFee(String ddcAddr, String sig) throws Exception {
        return queryFee(ddcAddr, sig, RequestOptions.builder(ChargeService.class).build());
    }

    /**
     * 查询指定的DDC业务主逻辑合约的方法所对应的调用业务费用。
     *
     * @param ddcAddr DDC业务主逻辑合约地址
     * @param sig     Hex格式的合约方法ID
     * @param options configuration
     * @return 返回DDC合约业务费
     * @throws Exception
     */
    public BigInteger queryFee(String ddcAddr, String sig, RequestOptions options) throws Exception {
        if (Strings.isEmpty(ddcAddr)) {
            throw new DDCException(ErrorMessage.DDC_ADDR_IS_EMPTY);
        }

        if (!WalletUtils.isValidAddress(ddcAddr)) {
            throw new DDCException(ErrorMessage.DDC_ADDR_IS_NOT_ADDRESS_FORMAT);
        }

        if (Strings.isEmpty(sig)) {
            throw new DDCException(ErrorMessage.SIG_IS_EMPTY);
        }

        if (!HexUtils.isValid4ByteHash(sig)) {
            throw new DDCException(ErrorMessage.SIG_IS_NOT_4BYTE_HASH);
        }

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcAddr);
        arrayList.add(sig);

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, ChargeFunctions.QUERY_FEE);

        return (BigInteger) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 运营方调用为自己的账户增加业务费。
     *
     * @param amount 对运营方账户进行充值的业务费
     * @return 返回交易哈希
     * @throws Exception
     */
    public String selfRecharge(BigInteger amount) throws Exception {
        return selfRecharge(amount, RequestOptions.builder(ChargeService.class).build());
    }

    /**
     * 运营方调用为自己的账户增加业务费。
     *
     * @param amount  对运营方账户进行充值的业务费
     * @param options configuration
     * @return 返回交易哈希
     * @throws Exception
     */
    public String selfRecharge(BigInteger amount, RequestOptions options) throws Exception {
        if (amount == null ||  BigInteger.ZERO.compareTo(amount) >= 0) {
            throw new DDCException(ErrorMessage.AMOUNT_IS_EMPTY);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(amount);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(options, arrayList, ChargeFunctions.SELF_RECHARGE);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 运营方调用接口设置指定的DDC主合约的方法调用费用。
     *
     * @param ddcAddr DDC业务主逻辑合约地址
     * @param sig     Hex格式的合约方法ID
     * @param amount  业务费用
     * @return 返回交易哈希
     * @throws Exception
     */
    public String setFee(String ddcAddr, String sig, BigInteger amount) throws Exception {
        return setFee(ddcAddr, sig, amount, RequestOptions.builder(ChargeService.class).build());
    }

    /**
     * 运营方调用接口设置指定的DDC主合约的方法调用费用。
     *
     * @param ddcAddr DDC业务主逻辑合约地址
     * @param sig     Hex格式的合约方法ID
     * @param amount  业务费用
     * @param options configuration
     * @return 返回交易哈希
     * @throws Exception
     */
    public String setFee(String ddcAddr, String sig, BigInteger amount, RequestOptions options) throws Exception {
        if (Strings.isEmpty(ddcAddr)) {
            throw new DDCException(ErrorMessage.DDC_ADDR_IS_EMPTY);
        }

        if (!WalletUtils.isValidAddress(ddcAddr)) {
            throw new DDCException(ErrorMessage.DDC_ADDR_IS_NOT_ADDRESS_FORMAT);
        }

        if (Strings.isEmpty(sig)) {
            throw new DDCException(ErrorMessage.SIG_IS_EMPTY);
        }

        if (!HexUtils.isValid4ByteHash(sig)) {
            throw new DDCException(ErrorMessage.SIG_IS_NOT_4BYTE_HASH);
        }

        if (amount == null) {
            throw new DDCException(ErrorMessage.AMOUNT_IS_EMPTY);
        }

        if (BigInteger.ZERO.compareTo(amount) >= 0) {
            throw new DDCException(ErrorMessage.AMOUNT_LT_ZERO);
        }
        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcAddr);
        arrayList.add(sig);
        arrayList.add(amount);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(options, arrayList, ChargeFunctions.SET_FEE);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 运营方调用接口删除指定的DDC主合约的方法调用费用。
     *
     * @param ddcAddr DDC业务主逻辑合约地址
     * @param sig     Hex格式的合约方法ID
     * @return 返回交易哈希
     * @throws Exception
     */
    public String delFee(String ddcAddr, String sig) throws Exception {
        return delFee(ddcAddr, sig, RequestOptions.builder(ChargeService.class).build());
    }

    /**
     * 运营方调用接口删除指定的DDC主合约的方法调用费用。
     *
     * @param ddcAddr DDC业务主逻辑合约地址
     * @param sig     Hex格式的合约方法ID
     * @param options configuration
     * @return 返回交易哈希
     * @throws Exception
     */
    public String delFee(String ddcAddr, String sig, RequestOptions options) throws Exception {
        if (Strings.isEmpty(ddcAddr)) {
            throw new DDCException(ErrorMessage.DDC_ADDR_IS_EMPTY);
        }

        if (!WalletUtils.isValidAddress(ddcAddr)) {
            throw new DDCException(ErrorMessage.DDC_ADDR_IS_NOT_ADDRESS_FORMAT);
        }

        if (Strings.isEmpty(sig)) {
            throw new DDCException(ErrorMessage.SIG_IS_EMPTY);
        }

        if (!HexUtils.isValid4ByteHash(sig)) {
            throw new DDCException(ErrorMessage.SIG_IS_NOT_4BYTE_HASH);
        }

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcAddr);
        arrayList.add(sig);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(options, arrayList, ChargeFunctions.DELETE_FEE);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 运营方调用该接口删除指定的DDC业务主逻辑合约授权。
     *
     * @param ddcAddr DDC业务主逻辑合约地址
     * @return 返回交易哈希
     * @throws Exception
     */
    public String delDDC(String ddcAddr) throws Exception {
        return delDDC(ddcAddr, RequestOptions.builder(ChargeService.class).build());
    }

    /**
     * 运营方调用该接口删除指定的DDC业务主逻辑合约授权。
     *
     * @param ddcAddr DDC业务主逻辑合约地址
     * @param options configuration
     * @return 返回交易哈希
     * @throws Exception
     */
    public String delDDC(String ddcAddr, RequestOptions options) throws Exception {
        if (Strings.isEmpty(ddcAddr)) {
            throw new DDCException(ErrorMessage.DDC_ADDR_IS_EMPTY);
        }

        if (!WalletUtils.isValidAddress(ddcAddr)) {
            throw new DDCException(ErrorMessage.DDC_ADDR_IS_NOT_ADDRESS_FORMAT);
        }

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(ddcAddr));

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(options, arrayList, ChargeFunctions.DELETE_DDC);
        return (String) respJsonRpcBean.getResult();
    }


}
