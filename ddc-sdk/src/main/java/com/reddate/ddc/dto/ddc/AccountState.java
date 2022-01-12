package com.reddate.ddc.dto.ddc;

public enum AccountState {

	Frozen(0),
	Active(1);
	
	private Integer status;

	private AccountState(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}
	
	public static AccountState getByVal(Integer status) {
		for(AccountState tmp : values()) {
			if(status.equals(tmp.getStatus())) {
				return tmp;
			}
		}
		return null;
	}
	
}
