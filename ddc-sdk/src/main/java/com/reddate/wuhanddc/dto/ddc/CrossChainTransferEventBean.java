package com.reddate.wuhanddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author wxq
 * @create 2022/7/12 17:35
 * @description cross chain transfer event
 */
@Data
public class CrossChainTransferEventBean extends BaseEventBean {
    String operator;
    BigInteger crossChainId;
    BigInteger ddcType;
    String signer;
    String to;
    BigInteger ddcId;
    String ddcURI;
    BigInteger amount;
    BigInteger fromChainID;
    BigInteger toChainID;
    String fromCCAddr;
    String toCCAddr;
    BigInteger crossChainFee;
}
