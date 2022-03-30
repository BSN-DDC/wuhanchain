package com.reddate.wuhanddc.dto.config;

import java.math.BigInteger;

/**
 * @author wxq
 * @create 2021/12/25 12:28
 * @description gateway
 */
public class Gateway {

    private String gateWayUrl;
    private BigInteger gasPrice;
    private BigInteger gasLimit;
    public String getGateWayUrl() {
        return gateWayUrl;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
    }

    public void setGateWayUrl(String gateWayUrl) {
        this.gateWayUrl = gateWayUrl;
    }


}
