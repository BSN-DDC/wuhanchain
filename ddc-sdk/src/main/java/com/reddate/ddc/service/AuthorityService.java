package com.reddate.ddc.service;

import com.reddate.ddc.constant.AuthorityFunctions;
import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.dto.config.DDCContract;
import com.reddate.ddc.dto.ddc.AccountInfo;
import com.reddate.ddc.dto.ddc.AccountRole;
import com.reddate.ddc.dto.ddc.AccountState;
import com.reddate.ddc.dto.wuhanchain.ReqJsonRpcBean;
import com.reddate.ddc.dto.wuhanchain.RespJsonRpcBean;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.net.RequestOptions;
import com.reddate.ddc.util.http.RestTemplateUtil;
import org.fisco.bcos.web3j.abi.datatypes.Address;
import org.fisco.bcos.web3j.abi.datatypes.Utf8String;
import org.fisco.bcos.web3j.crypto.WalletUtils;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.fisco.bcos.web3j.utils.Strings;

import java.util.ArrayList;

import static java.lang.String.*;

public class AuthorityService extends BaseService {

    public volatile static DDCContract ddcContract;

    public AuthorityService(DDCContract contractConfiguration) {
        ddcContract = contractConfiguration;
    }
    /**
     * 运营方或平台方通过调用该方法进行DDC账户信息的创建，上级角色可进行下级角色账户的操作，如运营方可以为平台方添加账户、平台方可以为终端用户添加账户，但运营方不能直接为终端用户添加账户。
     *
     * @param account DDC链账户地址
     * @param accName DDC账户对应的账户名称
     * @param accDID  DDC账户对应的DID信息（普通用户可为空）
     * @return 返回交易哈希
     * @throws Exception
     */
    public String addAccount(String account, String accName, String accDID) throws Exception {
        return addAccount(account, accName, accDID, RequestOptions.builder(AuthorityService.class).build());
    }

    /**
     * 运营方或平台方通过调用该方法进行DDC账户信息的创建，上级角色可进行下级角色账户的操作，如运营方可以为平台方添加账户、平台方可以为终端用户添加账户，但运营方不能直接为终端用户添加账户。
     *
     * @param account DDC链账户地址
     * @param accName DDC账户对应的账户名称
     * @param accDID  DDC账户对应的DID信息（普通用户可为空）
     * @param options 配置信息
     * @return 返回交易哈希
     * @throws Exception
     */
    public String addAccount(String account, String accName, String accDID, RequestOptions options) throws Exception {
        if (Strings.isEmpty(account)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_EMPTY);
        }

        if (!WalletUtils.isValidAddress(account)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }

        if (Strings.isEmpty(accName)) {
            throw new DDCException(ErrorMessage.ACCOUNT_NAME_IS_EMPTY);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(account);
        arrayList.add(accName);
        arrayList.add(accDID);

        ReqJsonRpcBean reqJsonRpcBean = assembleTransaction(AuthorityFunctions.ADD_ACCOUNT, arrayList, options);
        RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqJsonRpcBean, RespJsonRpcBean.class, options);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }


    /**
     * 运营方通过调用该方法可以直接对平台方的终端用户进行创建。
     *
     * @param account   DDC链账户地址
     * @param accName   DDC账户对应的账户名称
     * @param accDID    DDC账户对应的DID信息
     * @param leaderDID 该普通账户对应的上级账户的DID
     * @return 返回交易哈希
     * @throws Exception
     */
    public String addConsumerByOperator(String account, String accName, String accDID, String leaderDID) throws Exception {
        return addConsumerByOperator(account, accName, accDID, leaderDID, RequestOptions.builder(AuthorityService.class).build());
    }

    /**
     * 运营方通过调用该方法可以直接对平台方的终端用户进行创建。
     *
     * @param account   DDC链账户地址
     * @param accName   DDC账户对应的账户名称
     * @param accDID    DDC账户对应的DID信息
     * @param leaderDID 该普通账户对应的上级账户的DID
     * @return 返回交易哈希
     * @throws Exception
     */
    public String addConsumerByOperator(String account, String accName, String accDID, String leaderDID, RequestOptions options) throws Exception {
        if (Strings.isEmpty(account)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_EMPTY);
        }

        if (!WalletUtils.isValidAddress(account)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }

        if (Strings.isEmpty(accName)) {
            throw new DDCException(ErrorMessage.ACCOUNT_NAME_IS_EMPTY);
        }

        if (Strings.isEmpty(leaderDID)) {
            throw new DDCException(ErrorMessage.ACCOUNT_LEADER_DID_IS_EMPTY);
        }

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(account));
        arrayList.add(new Utf8String(accName));
        arrayList.add(new Utf8String(accDID));
        arrayList.add(new Utf8String(leaderDID));

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(options, arrayList, AuthorityFunctions.ADD_CONSUMER_BY_OPERATOR);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 删除账户
     *
     * @param account DDC链账户地址
     * @return 返回交易哈希
     * @throws Exception
     */
    public String delAccount(String account) throws Exception {
      return delAccount(account,RequestOptions.builder(AuthorityService.class).build());
    }

    /**
     * 删除账户
     *
     * @param account DDC链账户地址
     * @return 返回交易哈希
     * @throws Exception
     */
    public String delAccount(String account,RequestOptions options) throws Exception {
        if (Strings.isEmpty(account)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_EMPTY);
        }

        // input parameter
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(account));

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(options, arrayList, AuthorityFunctions.DEL_ACCOUNT);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 运营方或平台方通过该方法进行DDC账户信息的查询，上级角色可进行下级角色账户的操作。
     *
     * @param account DDC用户链账户地址
     * @return 返回DDC账户信息
     * @throws Exception
     */
    public AccountInfo getAccount(String account) throws Exception {
       return getAccount(account,RequestOptions.builder(AuthorityService.class).build());
    }

    /**
     * 运营方或平台方通过该方法进行DDC账户信息的查询，上级角色可进行下级角色账户的操作。
     *
     * @param account DDC用户链账户地址
     * @return 返回DDC账户信息
     * @throws Exception
     */
    public AccountInfo getAccount(String account,RequestOptions options) throws Exception {
        if (Strings.isEmpty(account)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_EMPTY);
        }

        if (!WalletUtils.isValidAddress(account)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
        // input parameter
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(account);

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, AuthorityFunctions.GET_ACCOUNT);

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountDID(valueOf(inputAndOutputResult.getResult().get(0).getData()));
        accountInfo.setAccountName(valueOf(inputAndOutputResult.getResult().get(1).getData()));
        accountInfo.setLeaderDID(valueOf(inputAndOutputResult.getResult().get(3).getData()));
        accountInfo.setField(valueOf(inputAndOutputResult.getResult().get(6).getData()));
        String accountRole = valueOf(inputAndOutputResult.getResult().get(2).getData());
        if (accountRole != null && !accountRole.trim().isEmpty()) {
            accountInfo.setAccountRole(AccountRole.getByVal(Integer.parseInt(accountRole)));
        }
        String platformState = valueOf(inputAndOutputResult.getResult().get(4).getData());
        if (platformState != null && !platformState.trim().isEmpty()) {
            accountInfo.setPlatformState(AccountState.getByVal(Integer.parseInt(platformState)));
        }

        String operatorState = valueOf(inputAndOutputResult.getResult().get(5).getData());
        if (operatorState != null && !operatorState.trim().isEmpty()) {
            accountInfo.setOperatorState(AccountState.getByVal(Integer.parseInt(operatorState)));
        }

        return accountInfo;
    }

    /**
     * 运营方或平台方通过该方法进行DDC账户信息状态的更改。
     *
     * @param account DDC用户链账户地址
     * @param state   状态 ：Frozen - 冻结状态 ； Active - 活跃状态
     * @return 返回交易哈希
     * @throws Exception
     */
    public String updateAccState(String account, AccountState state, boolean changePlatformState) throws Exception {
       return updateAccState(account,state,changePlatformState, RequestOptions.builder(AuthorityService.class).build());
    }

    /**
     * 运营方或平台方通过该方法进行DDC账户信息状态的更改。
     *
     * @param account DDC用户链账户地址
     * @param state   状态 ：Frozen - 冻结状态 ； Active - 活跃状态
     * @return 返回交易哈希
     * @throws Exception
     */
    public String updateAccState(String account, AccountState state, boolean changePlatformState,RequestOptions options) throws Exception {
        if (Strings.isEmpty(account)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_EMPTY);
        }

        if (!WalletUtils.isValidAddress(account)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }

        if (state == null) {
            throw new DDCException(ErrorMessage.ACCOUNT_STATUS_IS_EMPTY);
        }

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(account));
        arrayList.add(state.getStatus());
        arrayList.add(changePlatformState);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(options, arrayList, AuthorityFunctions.UPDATE_ACCOUNT_STATE);
        return (String) respJsonRpcBean.getResult();
    }


}
