package com.reddate.ddc.constant;

public enum DDCContractType {
    DDC_721("721"),
    DDC_1155("1155"),
    DDC_AUTHORITY("Authority"),
    DDC_CHARGE("Charge");

    DDCContractType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

}
