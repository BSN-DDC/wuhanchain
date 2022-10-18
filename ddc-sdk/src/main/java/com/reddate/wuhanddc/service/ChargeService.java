package com.reddate.wuhanddc.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.reddate.wuhanddc.constant.ChargeFunctions;
import com.reddate.wuhanddc.dto.config.DDCContract;
import com.reddate.wuhanddc.dto.wuhanchain.RespJsonRpcBean;
import com.reddate.wuhanddc.net.RequestOptions;
import org.fisco.bcos.web3j.abi.datatypes.Address;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.reddate.wuhanddc.constant.ContractConfig.DDCContracts;

/**
 * wuhanddc charge
 *
 * @author wxq
 */
public class ChargeService extends BaseService {

    public static DDCContract chargeContract;

    public ChargeService() {
        chargeContract = DDCContracts.stream().filter(t -> "charge".equals(t.getConfigType())).findFirst().orElse(null);
    }

    /**
     * 运营方、平台方调用该接口为所属同一方的同一级别账户或者下级账户充值；
     *
     * @param to     充值账户的地址
     * @param amount 充值金额
     * @return 返回交易哈希
     * @throws Exception
     */
    public String recharge(String sender, String to, BigInteger amount) throws Exception {
        return recharge(sender, to, amount, null);
    }

    /**
     * 运营方、平台方调用该接口为所属同一方的同一级别账户或者下级账户充值；
     *
     * @param sender
     * @param to      充值账户的地址
     * @param amount  充值金额
     * @param options configuration
     * @return 返回交易哈希
     * @throws Exception
     */
    public String recharge(String sender, String to, BigInteger amount, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check to
        checkTo(to);

        // check amount
        checkAmount(amount);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(to));
        arrayList.add(amount);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, ChargeFunctions.RECHARGE, chargeContract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 运营方或平台方可以通过调用该方法为所属同一方的同一级别账户或者下级账户进行批量充值；
     *
     * @param accounts 充值map key => value :  address => amount
     * @return 返回交易哈希
     * @throws Exception
     */
    public String rechargeBatch(String sender, Multimap<String, BigInteger> accounts) throws Exception {
        return rechargeBatch(sender, accounts, null);
    }

    public String rechargeBatch(String sender, Multimap<String, BigInteger> accounts, RequestOptions options) throws Exception {
        //check params length
        int len = accounts.size();
        checkLen(len);

        // check sender
        checkSender(sender);

        List<String> accountArray = Lists.newArrayList();
        List<String> amountArray = Lists.newArrayList();

        accounts.forEach((k,v)->{
            checkAccount(k);
            checkAmount(v);
            accountArray.add(k);
            amountArray.add(String.valueOf(v));
        });

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(accountArray.stream().collect(Collectors.joining(",")));
        arrayList.add(amountArray.stream().collect(Collectors.joining(",")));

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, ChargeFunctions.RECHARGE_BATCH, chargeContract);
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
        return balanceOf(accAddr, null);
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
        // check accAddr
        checkAccAddr(accAddr);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(accAddr));

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, ChargeFunctions.BALANCE_OF, chargeContract);
        return (BigInteger) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 运营方、平台方或终端用户可以调用方法批量查询账户的余额。
     *
     * @param accAddrs 查询的账户地址集合
     * @return 返回账户所对应的业务费余额
     * @throws Exception
     */
    public List<BigInteger> balanceOfBatch(List<String> accAddrs) throws Exception {
        return balanceOfBatch(accAddrs, null);
    }

    public List<BigInteger> balanceOfBatch(List<String> accAddrs, RequestOptions options) throws Exception {
        //check params length
        int len = accAddrs.size();
        checkLen(len);
        List<String> accAddrArrays = Lists.newArrayList();
        accAddrs.forEach(t->{
            // check accAddr
            checkAccAddr(t);
            accAddrArrays.add(t);
        });

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(accAddrArrays.stream().collect(Collectors.joining(",")));

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, ChargeFunctions.BALANCE_OF_BATCH, chargeContract);
        return (List<BigInteger>) inputAndOutputResult.getResult().get(0).getData();
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
        return queryFee(ddcAddr, sig, null);
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
        // check wuhanddc contract address
        checkDdcAddr(ddcAddr);

        // check sig
        checkSig(sig);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcAddr);
        arrayList.add(sig);

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, ChargeFunctions.QUERY_FEE, chargeContract);
        return (BigInteger) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 运营方调用为自己的账户增加业务费。
     *
     * @param sender
     * @param amount 对运营方账户进行充值的业务费
     * @return 返回交易哈希
     * @throws Exception
     */
    public String selfRecharge(String sender, BigInteger amount) throws Exception {
        return selfRecharge(sender, amount, null);
    }

    /**
     * 运营方调用为自己的账户增加业务费。
     *
     * @param sender
     * @param amount  对运营方账户进行充值的业务费
     * @param options configuration
     * @return 返回交易哈希
     * @throws Exception
     */
    public String selfRecharge(String sender, BigInteger amount, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check amount
        checkAmount(amount);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(amount);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, ChargeFunctions.SELF_RECHARGE, chargeContract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 运营方调用接口设置指定的DDC主合约的方法调用费用。
     *
     * @param sender
     * @param ddcAddr DDC业务主逻辑合约地址
     * @param sig     Hex格式的合约方法ID
     * @param amount  业务费用
     * @return 返回交易哈希
     * @throws Exception
     */
    public String setFee(String sender, String ddcAddr, String sig, BigInteger amount) throws Exception {
        return setFee(sender, ddcAddr, sig, amount, null);
    }

    /**
     * 运营方调用接口设置指定的DDC主合约的方法调用费用。
     *
     * @param sender
     * @param ddcAddr DDC业务主逻辑合约地址
     * @param sig     Hex格式的合约方法ID
     * @param amount  业务费用
     * @param options configuration
     * @return 返回交易哈希
     * @throws Exception
     */
    public String setFee(String sender, String ddcAddr, String sig, BigInteger amount, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check wuhanddc contract address
        checkDdcAddr(ddcAddr);

        // check sig
        checkSig(sig);

        // check amount
        checkAmount(amount);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcAddr);
        arrayList.add(sig);
        arrayList.add(amount);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, ChargeFunctions.SET_FEE, chargeContract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 运营方调用接口删除指定的DDC主合约的方法调用费用。
     *
     * @param sender
     * @param ddcAddr DDC业务主逻辑合约地址
     * @param sig     Hex格式的合约方法ID
     * @return 返回交易哈希
     * @throws Exception
     */
    public String delFee(String sender, String ddcAddr, String sig) throws Exception {
        return delFee(sender, ddcAddr, sig, null);
    }

    /**
     * 运营方调用接口删除指定的DDC主合约的方法调用费用。
     *
     * @param sender
     * @param ddcAddr DDC业务主逻辑合约地址
     * @param sig     Hex格式的合约方法ID
     * @param options configuration
     * @return 返回交易哈希
     * @throws Exception
     */
    public String delFee(String sender, String ddcAddr, String sig, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check wuhanddc contract address
        checkDdcAddr(ddcAddr);

        // check sig
        checkSig(sig);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcAddr);
        arrayList.add(sig);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, ChargeFunctions.DELETE_FEE, chargeContract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 运营方调用该接口删除指定的DDC业务主逻辑合约授权。
     *
     * @param sender
     * @param ddcAddr DDC业务主逻辑合约地址
     * @return 返回交易哈希
     * @throws Exception
     */
    public String delDDC(String sender, String ddcAddr) throws Exception {
        return delDDC(sender, ddcAddr, null);
    }

    /**
     * 运营方调用该接口删除指定的DDC业务主逻辑合约授权。
     *
     * @param sender
     * @param ddcAddr DDC业务主逻辑合约地址
     * @param options configuration
     * @return 返回交易哈希
     * @throws Exception
     */
    public String delDDC(String sender, String ddcAddr, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check wuhanddc contract address
        checkDdcAddr(ddcAddr);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(ddcAddr));

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, ChargeFunctions.DELETE_DDC, chargeContract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 运营方通过调用该API接口对启用批量开关进行设置。
     *
     * @param sender The address the transaction is sent from.
     * @param isOpen state
     * @return 返回交易哈希
     * @throws Exception
     */
    public String setSwitcherStateOfBatch(String sender, boolean isOpen) throws Exception {
        return setSwitcherStateOfBatch(sender, isOpen, null);
    }

    public String setSwitcherStateOfBatch(String sender, boolean isOpen, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(isOpen);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, ChargeFunctions.SET_SWITCHER_STATE_OF_BATCH, chargeContract);
        return (String) respJsonRpcBean.getResult();
    }

}
