package com.reddate.ddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

@Data
public class DDC721TransferEventBean extends BaseEventBean {
	
	/** 拥有账户地址 */
    String from;
    
    /** 接收账户地址 */
    String to;
    
    /** DDC ID */
    BigInteger ddcId;
}
