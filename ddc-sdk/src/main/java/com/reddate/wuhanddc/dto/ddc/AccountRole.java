package com.reddate.wuhanddc.dto.ddc;

public enum AccountRole {

	Operator(0),
	PlatformManager(1),
	Consumer(2),
	CrossChain(3);
	
	private Integer role;

	private AccountRole(Integer role) {
		this.role = role;
	}

	public Integer getRole() {
		return role;
	}
	
	public static AccountRole getByVal(Integer val) {
		for(AccountRole tmp : values()) {
			if(val.equals(tmp.getRole())) {
				return tmp;
			}
		}
		return null;
	}

}
