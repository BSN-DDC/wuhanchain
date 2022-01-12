package com.reddate.ddc.dto.ddc;

/**
 * @author wxq
 * @create 2021/12/25 15:37
 * @description account
 */
public class Account {
    public String address;
    public String publicKey;
    public String privateKey;
    public String mnemonic;
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

}
