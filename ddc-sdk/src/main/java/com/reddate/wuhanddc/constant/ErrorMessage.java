package com.reddate.wuhanddc.constant;

/**
 * error enum
 */
public enum ErrorMessage {
    // Param check  1
    // Chain error  2
    // other 9

    UNKNOWN_ERROR(9999, "unknown error"),
    CUSTOM_ERROR(1000, "%s"),
    ILLEGAL_PARAMETER(1001, "Illegal %s parameter"),
    NOT_STANDARD_ADDRESS_FORMAT(1002, "%s is not a standard address format"),
    LESS_THAN_ZERO(1003, "%s is less than 0"),
    IS_NULL(1004, "%s is null"),
    GET_FAILED(1005, "%s failed"),
    IS_EMPTY(1006, "%s is empty"),
    REQUEST_FAILED(2001, "profile initialization failed"),
    GET_TX_POOL_INSPECT_ERROR(2002, "failed to get txpool_inspect"),
    ETH_PROXY_ERROR(403, "both rpc err and result are null");

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
