package com.reddate.ddc.dto.ddc;

import lombok.Data;

@Data
public class DeleteFeeEventBean extends BaseEventBean {
	
	/** 业务主合约 */
    String ddcAddr;
    
    /** 方法签名 */
    String sig;
}
