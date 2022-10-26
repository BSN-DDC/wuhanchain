package com.reddate.wuhanddc.enums;

/**
 * @author wxq
 * @create 2022/7/12 14:32
 * @description eec type
 */
public enum DDCTypeEnum {
    ERC721("0"),
    ERC1155("1");

    public String getType() {
        return type;
    }

    String type;

    DDCTypeEnum(final String type) {
        this.type = type;
    }
}
