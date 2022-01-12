package com.reddate.ddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;
import java.util.ArrayList;

@Data
public class DDC1155TransferBatchEventBean extends BaseEventBean {
	
	/** 签名者 */
    String operator;
    
    /** 拥有账户地址 */
    String from;
    
    /** 接收账户地址 */
    String to;
    
    /** DDC集合 */
    ArrayList<BigInteger> ddcIds;
    
    /** 数量 */
    ArrayList<BigInteger> amounts;
}
