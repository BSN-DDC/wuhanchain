package com.reddate.ddc.dto.ddc;

public class AccountInfo {

	/** DDC用户链账户地址  */
	private String accountDID;

	/** DDC账户对应的账户名称  */
	private String accountName;

	/** 账户角色
	 * 0.运营方
	 * 1.平台
	 * 2.终端用户
	 * */
	private AccountRole accountRole;

	/** 账户上级管理者  */
	private String leaderDID;

	/** 平台管理账户状态  */
	private AccountState platformState;

	/** 运营管理账户状态 */
	private AccountState operatorState;

	/** 冗余字段 */
	private String field;

	public String getAccountDID() {
		return accountDID;
	}

	public void setAccountDID(String accountDID) {
		this.accountDID = accountDID;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public AccountRole getAccountRole() {
		return accountRole;
	}

	public void setAccountRole(AccountRole accountRole) {
		this.accountRole = accountRole;
	}

	public String getLeaderDID() {
		return leaderDID;
	}

	public void setLeaderDID(String leaderDID) {
		this.leaderDID = leaderDID;
	}

	public AccountState getPlatformState() {
		return platformState;
	}

	public void setPlatformState(AccountState platformState) {
		this.platformState = platformState;
	}

	public AccountState getOperatorState() {
		return operatorState;
	}

	public void setOperatorState(AccountState operatorState) {
		this.operatorState = operatorState;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	@Override
	public String toString() {
		return "AccountInfo{" +
				"accountDID='" + accountDID + '\'' +
				", accountName='" + accountName + '\'' +
				", accountRole=" + accountRole +
				", leaderDID='" + leaderDID + '\'' +
				", platformState=" + platformState +
				", operatorState=" + operatorState +
				", field='" + field + '\'' +
				'}';
	}
	
	
}
