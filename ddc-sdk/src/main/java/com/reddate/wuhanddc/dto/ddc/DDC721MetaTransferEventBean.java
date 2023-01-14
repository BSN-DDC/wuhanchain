package com.reddate.wuhanddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

@Data
public class DDC721MetaTransferEventBean extends BaseEventBean {

    /** 签名账户地址 */
    String operator;

	/** 拥有账户地址 */
    String from;
    
    /** 接收账户地址 */
    String to;
    
    /** DDC ID */
    BigInteger ddcId;
}
