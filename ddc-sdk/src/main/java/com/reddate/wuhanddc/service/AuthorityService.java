package com.reddate.wuhanddc.service;

import com.google.common.collect.Lists;
import com.reddate.wuhanddc.constant.AuthorityFunctions;
import com.reddate.wuhanddc.constant.ErrorMessage;
import com.reddate.wuhanddc.dto.config.DDCContract;
import com.reddate.wuhanddc.dto.ddc.AccountInfo;
import com.reddate.wuhanddc.dto.ddc.AccountRole;
import com.reddate.wuhanddc.dto.ddc.AccountState;
import com.reddate.wuhanddc.dto.wuhanchain.RespJsonRpcBean;
import com.reddate.wuhanddc.exception.DDCException;
import com.reddate.wuhanddc.net.RequestOptions;
import org.fisco.bcos.web3j.abi.datatypes.Address;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.web3j.utils.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.reddate.wuhanddc.constant.ContractConfig.DDCContracts;
import static java.lang.String.valueOf;

/**
 * wuhanddc authority
 *
 * @author wxq
 */
public class AuthorityService extends BaseService {

    public static DDCContract authorityContract;

    public AuthorityService() {
        authorityContract = DDCContracts.stream().filter(t -> "authority".equals(t.getConfigType())).findFirst().orElse(null);
    }

    /**
     * 运营方可以通过调用该方法设置平台方添加链账户开关。
     *
     * @param sender The address the transaction is sent from.
     * @param isOpen state
     * @return 返回交易哈希
     * @throws Exception
     */
    public String setSwitcherStateOfPlatform(String sender, boolean isOpen) throws Exception {
        return setSwitcherStateOfPlatform(sender, isOpen, null);
    }

    public String setSwitcherStateOfPlatform(String sender, boolean isOpen, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(isOpen);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, AuthorityFunctions.SET_SWITCHER_STATE_OF_PLATFORM, authorityContract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 运营方可以通过调用该方法查询平台方添加链账户开关状态。
     *
     * @return 返回state信息
     * @throws Exception
     */
    public boolean switcherStateOfPlatform() throws Exception {
        return switcherStateOfPlatform(null);
    }

    public boolean switcherStateOfPlatform(RequestOptions options) throws Exception {
        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, null, AuthorityFunctions.SWITCHER_STATE_OF_PLATFORM, authorityContract);
        return (Boolean) inputAndOutputResult.getResult().get(0).getData();
    }

    /**
     * 平台方可以通过调用该方法直接对平台方或平台方的终端用户进行创建。
     *
     * @param sender      The address the transaction is sent from.
     * @param account     DDC链账户地址
     * @param accountName DDC账户对应的账户名称
     * @param accountDID  该普通账户对应的DID
     * @return 返回交易哈希
     * @throws Exception
     */

    public String addAccountByPlatform(String sender, String account, String accountName, String accountDID) throws Exception {
        return addAccountByPlatform(sender, account, accountName, accountDID, null);
    }

    public String addAccountByPlatform(String sender, String account, String accountName, String accountDID, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check account
        checkAccount(account);

        // check account name
        checkAccountName(accountName);

        if (Strings.isEmpty(accountDID)) {
            accountDID = "";
        }

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(account);
        arrayList.add(accountName);
        arrayList.add(accountDID);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, AuthorityFunctions.ADD_ACCOUNT_BY_PLATFORM, authorityContract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 平台方可以通过调用该方法进行DDC链账户信息的批量创建，上级角色可进行下级角色账户的操作，平台方通过该方法只能添加终端账户。
     *
     * @param sender   The address the transaction is sent from.
     * @param accounts DDC链账户
     * @return 返回交易哈希
     * @throws Exception
     */

    public String addBatchAccountByPlatform(String sender, List<AccountInfo> accounts) throws Exception {
        return addBatchAccountByPlatform(sender, accounts, null);
    }

    public String addBatchAccountByPlatform(String sender, List<AccountInfo> accounts, RequestOptions options) throws Exception {
        //check params length
        int len = accounts.size();
        checkLen(len);

        // check sender
        checkSender(sender);

        List<String> accountArray = Lists.newArrayList();
        List<String> accountNameArray = Lists.newArrayList();
        List<String> accountDIDArray = Lists.newArrayList();

        accounts.forEach(t -> {
            // check account
            checkAccount(t.getAccount());

            // check account name
            checkAccountName(t.getAccountName());

            if (Strings.isEmpty(t.getAccountDID())) {
                t.setAccountDID("");
            }
            accountArray.add(t.getAccount());
            accountNameArray.add(t.getAccountName());
            accountDIDArray.add(t.getAccountDID());
        });

        // input params
        ArrayList<Object> arrayList = Lists.newArrayList();
        arrayList.add(accountArray.stream().collect(Collectors.joining(",")));
        arrayList.add(accountNameArray.stream().collect(Collectors.joining(",")));
        arrayList.add(accountDIDArray.stream().collect(Collectors.joining(",")));

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, AuthorityFunctions.ADD_BATCH_ACCOUNT_BY_PLATFORM, authorityContract);
        return (String) respJsonRpcBean.getResult();
    }


    /**
     * 运营方可以通过调用该方法直接对平台方或平台方的终端用户进行创建。
     *
     * @param sender    The address the transaction is sent from.
     * @param account   DDC链账户地址
     * @param accName   DDC账户对应的账户名称
     * @param accDID    DDC账户对应的DID信息
     * @param leaderDID 该普通账户对应的上级账户的DID
     * @return 返回交易哈希
     * @throws Exception
     */
    public String addAccountByOperator(String sender, String account, String accName, String accDID, String leaderDID) throws Exception {
        return addAccountByOperator(sender, account, accName, accDID, leaderDID, null);
    }

    /**
     * 平台方可以通过调用该方法进行DDC账户信息的创建，上级角色可进行下级角色账户的操作，平台方通过该方法只能添加终端账户。
     *
     * @param sender    The address the transaction is sent from.
     * @param account   DDC链账户地址
     * @param accName   DDC账户对应的账户名称
     * @param accDID    DDC账户对应的DID信息
     * @param leaderDID 该普通账户对应的上级账户的DID
     * @return 返回交易哈希
     * @throws Exception
     */
    public String addAccountByOperator(String sender, String account, String accName, String accDID, String leaderDID, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check account
        checkAccount(account);

        // check account name
        checkAccountName(accName);

        if (Strings.isEmpty(leaderDID)) {
            leaderDID = "";
        }

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(account);
        arrayList.add(accName);
        arrayList.add(accDID);
        arrayList.add(leaderDID);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, AuthorityFunctions.ADD_ACCOUNT_BY_OPERATOR, authorityContract);
        return (String) respJsonRpcBean.getResult();
    }


    /**
     * 运营方可以通过调用该方法直接对平台方或平台方的终端用户进行批量创建。
     *
     * @param sender   The address the transaction is sent from.
     * @param accounts DDC链账户
     * @return 返回交易哈希
     * @throws Exception
     */
    public String addBatchAccountByOperator(String sender, List<AccountInfo> accounts) throws Exception {
        return addBatchAccountByOperator(sender, accounts, null);
    }

    public String addBatchAccountByOperator(String sender, List<AccountInfo> accounts, RequestOptions options) throws Exception {
        //check params length
        int len = accounts.size();
        checkLen(len);

        // check sender
        checkSender(sender);

        List<String> accountArray = Lists.newArrayList();
        List<String> accountNameArray = Lists.newArrayList();
        List<String> accountDIDArray = Lists.newArrayList();
        List<String> accountLeaderDIDArray = Lists.newArrayList();

        accounts.forEach(t -> {
            // check account
            checkAccount(t.getAccount());
            // check account name
            checkAccountName(t.getAccountName());
            if (Strings.isEmpty(t.getLeaderDID())) {
                t.setLeaderDID("");
            }
            accountArray.add(t.getAccount());
            accountNameArray.add(t.getAccountName());
            accountDIDArray.add(t.getAccountDID());
            accountLeaderDIDArray.add(t.getLeaderDID());
        });

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(accountArray.stream().collect(Collectors.joining(",")));
        arrayList.add(accountNameArray.stream().collect(Collectors.joining(",")));
        arrayList.add(accountDIDArray.stream().collect(Collectors.joining(",")));
        arrayList.add(accountLeaderDIDArray.stream().collect(Collectors.joining(",")));

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, AuthorityFunctions.ADD_BATCH_ACCOUNT_BY_OPERATOR, authorityContract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 删除账户
     *
     * @param sender  The address the transaction is sent from.
     * @param account DDC链账户地址
     * @return 返回交易哈希
     * @throws Exception
     */
    public String delAccount(String sender, String account) throws Exception {
        return delAccount(sender, account, null);
    }

    /**
     * 删除账户
     *
     * @param sender  The address the transaction is sent from.
     * @param account
     * @param options
     * @return
     * @throws Exception
     */
    public String delAccount(String sender, String account, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check account
        checkAccount(account);

        // input parameter
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(account));

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, AuthorityFunctions.DEL_ACCOUNT, authorityContract);
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
        return getAccount(account, null);
    }

    /**
     * 运营方或平台方通过该方法进行DDC账户信息的查询，上级角色可进行下级角色账户的操作。
     *
     * @param account DDC用户链账户地址
     * @return 返回DDC账户信息
     * @throws Exception
     */
    public AccountInfo getAccount(String account, RequestOptions options) throws Exception {
        // check account
        checkAccount(account);

        // input parameter
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(account);

        // send call tran and decode output
        InputAndOutputResult inputAndOutputResult = sendCallTransactionAndDecodeOutput(options, arrayList, AuthorityFunctions.GET_ACCOUNT, authorityContract);

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
     * @param sender  The address the transaction is sent from.
     * @param account DDC用户链账户地址
     * @param state   状态 ：Frozen - 冻结状态 ； Active - 活跃状态
     * @return 返回交易哈希
     * @throws Exception
     */
    public String updateAccState(String sender, String account, AccountState state, boolean changePlatformState) throws Exception {
        return updateAccState(sender, account, state, changePlatformState, null);
    }

    /**
     * 运营方或平台方通过该方法进行DDC账户信息状态的更改。
     *
     * @param sender  The address the transaction is sent from.
     * @param account DDC用户链账户地址
     * @param state   状态 ：Frozen - 冻结状态 ； Active - 活跃状态
     * @return 返回交易哈希
     * @throws Exception
     */
    public String updateAccState(String sender, String account, AccountState state, boolean changePlatformState, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check account
        checkAccount(account);

        if (Objects.isNull(state)) {
            throw new DDCException(ErrorMessage.IS_EMPTY, "account status");
        }

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(account));
        arrayList.add(state.getStatus());
        arrayList.add(changePlatformState);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, AuthorityFunctions.UPDATE_ACCOUNT_STATE, authorityContract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 跨平台授权链账户转移DDC
     *
     * @param sender
     * @param from
     * @param to
     * @param approved
     * @return
     * @throws Exception
     */
    public String crossPlatformApproval(String sender, String from, String to, boolean approved) throws Exception {
        return crossPlatformApproval(sender, from, to, approved, null);
    }

    /**
     * 跨平台授权链账户转移DDC
     *
     * @param sender
     * @param from
     * @param to
     * @param approved
     * @return
     * @throws Exception
     */
    public String crossPlatformApproval(String sender, String from, String to, boolean approved, RequestOptions options) throws Exception {
        // check sender
        checkSender(sender);

        // check from
        checkFrom(from);

        // check to
        checkTo(to);

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);
        arrayList.add(to);
        arrayList.add(approved);

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, AuthorityFunctions.CROSS_PLATFORM_APPROVAL, authorityContract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 运营方通过调用该接口将旧平台方链账户所对应的DID同步到链上。
     *
     * @param sender
     * @param dids   did list
     * @return
     * @throws Exception
     */
    public String syncPlatformDID(String sender, List<String> dids) throws Exception {
        return syncPlatformDID(sender, dids, null);
    }

    public String syncPlatformDID(String sender, List<String> dids, RequestOptions options) throws Exception {
        //check params length
        int len = dids.size();
        checkLen(len);

        // check sender
        checkSender(sender);

        List<String> didsArray = new ArrayList<>();
        // check did
        dids.forEach(t -> {
            checkDID(t);
            didsArray.add(t);
        });

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(didsArray.stream().collect(Collectors.joining(",")));

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, AuthorityFunctions.SYNC_PLATFORM_DID, authorityContract);
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
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(sender, options, arrayList, AuthorityFunctions.SET_SWITCHER_STATE_OF_BATCH, authorityContract);
        return (String) respJsonRpcBean.getResult();
    }
}
