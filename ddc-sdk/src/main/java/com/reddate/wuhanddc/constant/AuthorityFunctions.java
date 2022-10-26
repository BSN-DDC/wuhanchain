package com.reddate.wuhanddc.constant;

/**
 * Authorization contract method
 */
public class AuthorityFunctions {
    public static final String SET_SWITCHER_STATE_OF_PLATFORM = "setSwitcherStateOfPlatform";

    public static final String SWITCHER_STATE_OF_PLATFORM = "switcherStateOfPlatform";

    public static final String ADD_ACCOUNT_BY_PLATFORM = "addAccountByPlatform";
    public static final String ADD_BATCH_ACCOUNT_BY_PLATFORM = "addBatchAccountByPlatform";

    public static final String ADD_ACCOUNT_BY_OPERATOR = "addAccountByOperator";
    public static final String ADD_BATCH_ACCOUNT_BY_OPERATOR = "addBatchAccountByOperator";

    public static final String ADD_ACCOUNT = "addAccount";

    public static final String DEL_ACCOUNT = "DelAccount";

    public static final String GET_ACCOUNT = "getAccount";

    public static final String UPDATE_ACCOUNT_STATE = "updateAccountState";

    public static final String GET_FUNCTION = "getFunction";

    public static final String DEL_FUNCTION = "delFunction";

    public static final String ADD_FUNCTION = "addFunction";
    public static final String CROSS_PLATFORM_APPROVAL = "crossPlatformApproval";
    public static final String SYNC_PLATFORM_DID = "syncPlatformDID";
    public static final String SET_SWITCHER_STATE_OF_BATCH = "setSwitcherStateOfBatch";

    public static final String ADD_ACCOUNT_EVENT = "AddAccount(address,address)";
    public static final String DEL_ACCOUNT_EVENT = "DelAccount(address)";
    public static final String UPDATE_ACCOUNT_STATE_EVENT = "UpdateAccountState(address,uint8,uint8)";

    public static final String SET_SWITCHER_STATE_OF_PLATFORM_EVENT = "SetSwitcherStateOfPlatform(address,bool)";
    public static final String ADD_BATCH_ACCOUNT_EVENT = "AddBatchAccount(address,address[])";
    public static final String CROSS_PLATFORM_APPROVAL_EVENT = "CrossPlatformApproval(address,address,bool)";
    public static final String SYNC_PLATFORM_DID_EVENT = "SyncPlatformDID(address,string[])";
}
