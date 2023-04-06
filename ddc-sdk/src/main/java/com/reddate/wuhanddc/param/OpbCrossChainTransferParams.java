package com.reddate.wuhanddc.param;

import com.reddate.wuhanddc.enums.DDCTypeEnum;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class OpbCrossChainTransferParams extends BaseParams{

    /**
     * DDC unique identification.
     */
    BigInteger ddcId;

    /**
     * Whether locked.
     */
    Boolean isLock;

    /**
     * Target side chain Id.
     */
    BigInteger toChainID;

    /**
     * Target chain recipient account.
     */
    String to;

    /**
     * Additional data.
     */
    byte[] data;

    private OpbCrossChainTransferParams(String sender, DDCTypeEnum ddcType, String to, Boolean isLock, BigInteger ddcId, byte[] data, BigInteger toChainID) {
        this.sender = sender;
        this.ddcType = ddcType;
        this.to = to;
        this.isLock = isLock;
        this.ddcId = ddcId;
        this.data = data;
        this.toChainID = toChainID;
    }

    public static OpbCrossChainTransferParams.Builder builder() {
        return new OpbCrossChainTransferParams.Builder();
    }

    public static class Builder {
        private String sender;
        private DDCTypeEnum ddcType;
        private String to;
        private Boolean isLock;
        private BigInteger ddcId;
        private byte[] data;
        private BigInteger toChainID;

        public OpbCrossChainTransferParams build() {
            return new OpbCrossChainTransferParams(
                    this.sender,
                    this.ddcType,
                    this.to,
                    this.isLock,
                    this.ddcId,
                    this.data,
                    this.toChainID
            );
        }

        public OpbCrossChainTransferParams.Builder setSender(String sender) {
            this.sender = sender;
            return this;
        }

        public OpbCrossChainTransferParams.Builder setDDCType(DDCTypeEnum ddcType) {
            this.ddcType = ddcType;
            return this;
        }


        public OpbCrossChainTransferParams.Builder setTo(String to) {
            this.to = to;
            return this;
        }

        public OpbCrossChainTransferParams.Builder setIsLock(Boolean isLock) {
            this.isLock = isLock;
            return this;
        }

        public OpbCrossChainTransferParams.Builder setDdcId(BigInteger ddcId) {
            this.ddcId = ddcId;
            return this;
        }

        public OpbCrossChainTransferParams.Builder setData(byte[] data) {
            this.data = data;
            return this;
        }

        public OpbCrossChainTransferParams.Builder setToChainID(BigInteger toChainID) {
            this.toChainID = toChainID;
            return this;
        }

    }
}
