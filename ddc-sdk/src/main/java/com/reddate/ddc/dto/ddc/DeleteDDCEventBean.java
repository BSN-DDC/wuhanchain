package com.reddate.ddc.dto.ddc;

import lombok.Data;

@Data
public class DeleteDDCEventBean extends BaseEventBean {
	
	/** 业务主合约 */
    String ddcAddr;
}
