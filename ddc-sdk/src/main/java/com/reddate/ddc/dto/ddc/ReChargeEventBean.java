package com.reddate.ddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

@Data
public class ReChargeEventBean extends BaseEventBean {
	
	/** 原链账户地址 */
    String from;
    
    /** 目标链账户地址 */
    String to;
    
    /** 业务费 */
    BigInteger value;
}
