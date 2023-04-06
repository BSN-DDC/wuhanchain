package com.reddate.wuhanddc.param;

import com.reddate.wuhanddc.enums.CrossChainStateEnum;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class UpdateOpbCrossChainStatusParams {

    /**
     * Caller
     */
    String sender;
    /**
     *
     */
    BigInteger crossChainId;
    /**
     * State
     */
    CrossChainStateEnum state;

    /**
     * Modification status remarks.
     */
    String remark;

    private UpdateOpbCrossChainStatusParams(String sender,BigInteger crossChainId, CrossChainStateEnum state, String remark) {
        this.sender = sender;
        this.crossChainId = crossChainId;
        this.state = state;
        this.remark = remark;
    }

    public static UpdateOpbCrossChainStatusParams.Builder builder() {
        return new UpdateOpbCrossChainStatusParams.Builder();
    }

    public static class Builder {
        private String sender;
        private BigInteger crossChainId;
        private CrossChainStateEnum state;
        private String remark;

        public UpdateOpbCrossChainStatusParams build() {
            return new UpdateOpbCrossChainStatusParams(
                    this.sender,
                    this.crossChainId,
                    this.state,
                    this.remark
            );
        }

        public UpdateOpbCrossChainStatusParams.Builder setSender(String sender) {
            this.sender = sender;
            return this;
        }

        public UpdateOpbCrossChainStatusParams.Builder setCrossChainId(BigInteger crossChainId) {
            this.crossChainId = crossChainId;
            return this;
        }

        public UpdateOpbCrossChainStatusParams.Builder setState(CrossChainStateEnum state) {
            this.state = state;
            return this;
        }

        public UpdateOpbCrossChainStatusParams.Builder setRemark(String remark) {
            this.remark = remark;
            return this;
        }
    }


}
