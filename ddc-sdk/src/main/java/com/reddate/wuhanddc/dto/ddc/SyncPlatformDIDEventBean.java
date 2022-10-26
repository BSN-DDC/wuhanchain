package com.reddate.wuhanddc.dto.ddc;

import lombok.Data;

import java.util.ArrayList;

@Data
public class SyncPlatformDIDEventBean extends BaseEventBean{
    /**签名者*/
    String operator;

    /**平台方DID集合*/
    ArrayList<String> dids;

    @Override
    public String toString() {
        return "SyncPlatformDIDEventBean{" +
                "operator='" + operator + '\'' +
                ", dids=" + dids +
                "} " + super.toString();
    }
}
