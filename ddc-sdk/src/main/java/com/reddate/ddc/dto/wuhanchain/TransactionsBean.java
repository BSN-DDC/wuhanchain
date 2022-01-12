package com.reddate.ddc.dto.wuhanchain;

/**
 * @author wxq
 * @create 2021/12/23 17:39
 * @description  eth Transactions
 */
public class TransactionsBean {

    private String blockHash;
    private String blockNumber;
    private String from;
    private String gas;
    private String gasPrice;
    private String hash;
    private String input;
    private String nonce;
    private String to;
    private String transactionIndex;
    private String value;
    private String type;
    private String v;
    private String r;
    private String s;

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public String getGas() {
        return gas;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getInput() {
        return input;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getNonce() {
        return nonce;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTo() {
        return to;
    }

    public void setTransactionIndex(String transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getTransactionIndex() {
        return transactionIndex;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getV() {
        return v;
    }

    public void setR(String r) {
        this.r = r;
    }

    public String getR() {
        return r;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getS() {
        return s;
    }

}