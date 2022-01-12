package com.reddate.ddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

@Data
public class PayEventBean extends BaseEventBean {
	
	/** 链账户地址 */
    String from;
    
    /** 业务主合约 */
    String ddcAddr;
    
    /** 方法签名  */
    String sig;
    
    /** 业务费 */
    BigInteger amount;
}
