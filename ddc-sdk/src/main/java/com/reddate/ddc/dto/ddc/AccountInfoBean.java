package com.reddate.ddc.dto.ddc;


import java.math.BigInteger;

public class AccountInfoBean {
    String accountDID;
    String accountName;
    BigInteger accountRole;
    String leaderDID;
    BigInteger platformState;
    BigInteger operatorState;
    String field;

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

    public BigInteger getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(BigInteger accountRole) {
        this.accountRole = accountRole;
    }

    public String getLeaderDID() {
        return leaderDID;
    }

    public void setLeaderDID(String leaderDID) {
        this.leaderDID = leaderDID;
    }

    public BigInteger getPlatformState() {
        return platformState;
    }

    public void setPlatformState(BigInteger platformState) {
        this.platformState = platformState;
    }

    public BigInteger getOperatorState() {
        return operatorState;
    }

    public void setOperatorState(BigInteger operatorState) {
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
        return "AccountInfoBean{" +
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
