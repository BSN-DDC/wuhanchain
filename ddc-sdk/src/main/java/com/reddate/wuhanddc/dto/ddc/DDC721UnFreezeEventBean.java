package com.reddate.wuhanddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

@Data
public class DDC721UnFreezeEventBean extends BaseEventBean {
	
	/** 签名者 */
    String sender;
    
    /** DDC ID */
    BigInteger ddcId;
}
