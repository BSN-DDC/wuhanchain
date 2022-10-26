package com.reddate.wuhanddc.enums;

/**
 * @author wxq
 * @create 2022/7/18 16:13
 * @description cross chain state
 */
public enum CrossChainStateEnum {
    CROSS_CHAIN_PENDING(0),
    CROSS_CHAIN_SUCCESS(1),
    CROSS_CHAIN_FAILURE(2);

    public int getState() {
        return state;
    }
    int state;

    CrossChainStateEnum(final int type) {
        this.state = type;
    }
}
