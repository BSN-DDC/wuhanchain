package com.reddate.ddc.constant;

/**
 * error enum
 */
public enum ErrorMessage {
    // Param check  1
    // Chain error  2
    // other 9

    UNKNOWN_ERROR(9999, "unknown error"),
    ACCOUNT_NAME_IS_EMPTY(1001, "accountName is empty"),
    ACCOUNT_IS_EMPTY(1002, "account is empty"),
    ACCOUNT_STATUS_IS_EMPTY(1003, "account status is empty"),
    ACCOUNT_LEADER_DID_IS_EMPTY(1004, "leader DID is empty"),
    ACCOUNT_IS_NOT_ADDRESS_FORMAT(1005, "account is not a standard address format"),
    AMOUNT_IS_EMPTY(1006, "amount is empty"),
    TO_ACCOUNT_IS_EMPTY(1007, "to account is empty"),
    TO_ACCOUNT_IS_NOT_ADDRESS_FORMAT(1008, "to account is not a standard address format"),
    FROM_ACCOUNT_IS_EMPTY(1009, "from account is empty"),
    FROM_ACCOUNT_IS_NOT_ADDRESS_FORMAT(1010, "from account is not a standard address format"),
    ACC_ADDR_IS_EMPTY(1011, "accAddr is empty"),
    ACC_ADDR_IS_NOT_ADDRESS_FORMAT(1012, "accAddr is not a standard address format"),
    DDC_ADDR_IS_EMPTY(1013, "ddcAddr is empty"),
    DDC_ADDR_IS_NOT_ADDRESS_FORMAT(1014, "ddcAddr is not a standard address format"),
    AMOUNT_LT_ZERO(1015, "amount is less than 0"),
    DDC_ID_LT_ZERO(1016, "ddcId is less than 0"),
    DDC_ID_LT_EMPTY(1016, "ddcId is empty"),
    DDC_URI_IS_EMPTY(1017, "ddcURI cannot be null, but can be an empty string"),
    CONTRACT_INFO_IS_EMPTY(1018, "contract info is empty"),
    CONTRACT_BYTECODE_IS_EMPTY(1019, "contract Bytecode is empty"),
    CONTRACT_ADDRESS_IS_EMPTY(1020, "contractAddress is empty"),
    NONCE_GET_FAILED(1021, "nonce get failed"),
    GAS_PRICE_GET_FAILED(1022, "gasPrice get failed"),
    GAS_LIMIT_GET_FAILED(1023, "gasLimit get failed"),
    EMPTY_GATEWAY_URL_SPECIFIED(1024, "empty gateWayUrl specified"),
    SIG_IS_EMPTY(1025, "sig is empty"),
    SIG_IS_NOT_4BYTE_HASH(1026, "sig is not 4 byte hash"),
    REQUEST_OPTIONS_INIT_FAILED(1027, "requestOptions init failed"),
    BASIC_CONFIGURATION_IS_EMPTY(1028, "basicConfiguration is empty"),
    BASIC_CONFIGURATION_READ_FAILED(1029, "basicConfiguration read failed"),
    SIGN_EVENT_LISTENER_IS_EMPTY(1030, "not register sign event listener"),
    SIGN_EVENT_LISTENER_RESPONSE_FAILED(1031, "signEventListener response failed"),
    INPUT_AND_OUTPUT_RESULT_IS_EMPTY(1032, "InputAndOutputResult is empty"),
    GET_BLOCK_BY_NUMBER_ERROR(1033, "getBlockByNumber error"),
    GET_TRANSACTION_RECEIPT_ERROR(1034, "transactionReceipt is empty"),
    FAILED_TO_CREATE_ACCOUNT(1035, "Failed to create account"),
    TRANSACTION_RESULT_IS_EMPTY(1036, "transaction result is empty"),
    RESULT_FORMAT_CONVERSION_FAILED(1037, "result format conversion failed"),
    SIGN_USER_ADDRESS_IS_EMPTY(1038, "signUserAddress is empty"),
    TRANSACTION_FAILED(1039, "transaction failed"),
    GAS_PRICE_DEFINITION_ERROR(1040, "gasPrice definition error"),
    GAS_LIMIT_DEFINITION_ERROR(1041, "gasLimit definition error"),
    REQUEST_OPTIONS_BUILDER_FAILED(1042, "requestOptions builder failed"),
    SENDER_IS_EMPTY(1043, "sender is empty"),
    SENDER_IS_NOT_A_STANDARD_ADDRESS_format(1044, "accAddr is empty"),
    OWNER_IS_EMPTY(1045, "owner is empty"),
    OWNER_IS_NOT_ADDRESS_FORMAT(1046, "owner is not a standard address format"),
    OPERATOR_IS_EMPTY(1047, "operator is empty"),
    OPERATOR_IS_NOT_ADDRESS_FORMAT(1048, "operator is not a standard address format"),
    REQUEST_FAILED(2001, "profile initialization failed"),
    ETH_PROXY_ERROR(403, "both rpc err and result are null"),
    ;

    private Integer code;

    private String message;

    private ErrorMessage(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    public static String getMessage(Integer code) {
        for (ErrorMessage error : ErrorMessage.values()) {
            if (error.code.equals(code)) {
                return error.message;
            }
        }
        return null;
    }

    public static String getMessage(ErrorMessage errorMessage) {
        for (ErrorMessage error : ErrorMessage.values()) {
            if (error.code.equals(errorMessage.code)) {
                return error.message;
            }
        }
        return null;
    }
}
