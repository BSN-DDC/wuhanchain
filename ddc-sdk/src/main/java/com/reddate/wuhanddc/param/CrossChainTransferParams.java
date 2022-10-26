package com.reddate.wuhanddc.param;

import com.reddate.wuhanddc.enums.DDCTypeEnum;
import lombok.Getter;

import java.math.BigInteger;

/**
 * @author wxq
 * @create 2022/7/12 14:48
 * @description cross chain transfer param
 */
@Getter
public class CrossChainTransferParams extends BaseParams{
    /**
     * Target chain signature account.
     */
    String signer;
    /**
     * Target chain recipient account.
     */
    String to;
    /**
     * DDC unique identification.
     */
    BigInteger ddcId;
    /**
     * Additional data.
     */
    byte[] data;
    /**
     * Target side chain Id.
     */
    BigInteger toChainID;
    /**
     * Target chain contract.
     */
    String toCCAddr;
    /**
     * Target chain function name.
     */
    String funcName;

    private CrossChainTransferParams(String sender, DDCTypeEnum ddcType, String signer, String to, BigInteger ddcId, byte[] data, BigInteger toChainID, String toCCAddr, String funcName) {
        this.sender = sender;
        this.ddcType = ddcType;
        this.signer = signer;
        this.to = to;
        this.ddcId = ddcId;
        this.data = data;
        this.toChainID = toChainID;
        this.toCCAddr = toCCAddr;
        this.funcName = funcName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String sender;
        private DDCTypeEnum ddcType;
        private String signer;
        private String to;
        private BigInteger ddcId;
        private byte[] data;
        private BigInteger toChainID;
        private String toCCAddr;
        private String funcName;

        public CrossChainTransferParams build() {
            return new CrossChainTransferParams(
                    this.sender,
                    this.ddcType,
                    this.signer,
                    this.to,
                    this.ddcId,
                    this.data,
                    this.toChainID,
                    this.toCCAddr,
                    this.funcName
            );
        }

        public Builder setSender(String sender) {
            this.sender = sender;
            return this;
        }

        public Builder setDDCType(DDCTypeEnum ddcType) {
            this.ddcType = ddcType;
            return this;
        }

        public Builder setSigner(String signer) {
            this.signer = signer;
            return this;
        }

        public Builder setTo(String to) {
            this.to = to;
            return this;
        }

        public Builder setDdcId(BigInteger ddcId) {
            this.ddcId = ddcId;
            return this;
        }

        public Builder setData(byte[] data) {
            this.data = data;
            return this;
        }

        public Builder setToChainID(BigInteger toChainID) {
            this.toChainID = toChainID;
            return this;
        }

        public Builder setToCCAddr(String toCCAddr) {
            this.toCCAddr = toCCAddr;
            return this;
        }

        public Builder setFuncName(String funcName) {
            this.funcName = funcName;
            return this;
        }
    }
}
