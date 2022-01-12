package com.reddate.ddc.constant;

/**
 * Authorization contract method
 */
public class AuthorityFunctions {
    public static final String ADD_ACCOUNT = "addAccount";

    public static final String ADD_CONSUMER_BY_OPERATOR = "addConsumerByOperator";

    public static final String DEL_ACCOUNT = "DelAccount";

    public static final String GET_ACCOUNT = "getAccount";

    public static final String UPDATE_ACCOUNT_STATE = "updateAccountState";

    public static final String GET_FUNCTION = "getFunction";

    public static final String DEL_FUNCTION = "delFunction";

    public static final String ADD_FUNCTION = "addFunction";


    public static final String ADD_ACCOUNT_EVENT = "AddAccount(address,address,string,string,uint8,string,uint8,uint8,string)";
    public static final String DEL_ACCOUNT_EVENT = "DelAccount(address)";
    public static final String UPDATE_ACCOUNT_EVENT = "UpdateAccount(address,string,string,uint8,string,uint8,uint8,string)";
    public static final String UPDATE_ACCOUNT_STATE_EVENT = "UpdateAccountState(address,uint8,uint8)";
}
