package com.reddate.ddc.dto.config;

/**
 * @author wxq
 * @create 2021/12/25 12:29
 * @description DDCContract
 */

public class DDCContract {

    private String configType;
    private String contractAbi;
    private String contractBytecode;
    private String contractAddress;
    private String signUserAddress;

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getContractAbi() {
        return contractAbi;
    }

    public void setContractAbi(String contractAbi) {
        this.contractAbi = contractAbi;
    }

    public String getContractBytecode() {
        return contractBytecode;
    }

    public void setContractBytecode(String contractBytecode) {
        this.contractBytecode = contractBytecode;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getSignUserAddress() {
        return signUserAddress;
    }

    public void setSignUserAddress(String signUserAddress) {
        this.signUserAddress = signUserAddress;
    }


}
