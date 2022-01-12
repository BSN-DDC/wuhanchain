package com.reddate.ddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

@Data
public class UpdateAccountStateEventBean extends BaseEventBean {
	
	/** 链账户地址 */
    private String account;
    
    /** 平台管理账户状态 */
    private BigInteger platformState;
    
    /** 运营管理账户状态 */
    private BigInteger operatorState;

}
