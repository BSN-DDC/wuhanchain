package com.reddate.wuhanddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

@Data
public class AddAccountEventBean extends BaseEventBean {
	
	/** 签名者 */
    private String caller;
    
    /** 链账户地址 */
    private String account;
    
    /** 账户DID */
    private String accountDID;
    
    /** 账户名称 */
    private String accountName;
    
    /** 账户角色 */
    private BigInteger accountRole;
    
    /** 账户上级管理者 */
    private String leaderDID;
    
    /** 平台管理账户状态 */
    private BigInteger platformState;
    
    /** 运营管理账户状态 */
    private BigInteger operatorState;
    
    /** 冗余字段 */
    private String field;

}
