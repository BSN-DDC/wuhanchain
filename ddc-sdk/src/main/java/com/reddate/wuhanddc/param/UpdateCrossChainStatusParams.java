package com.reddate.wuhanddc.param;

import com.reddate.wuhanddc.enums.CrossChainStateEnum;
import com.reddate.wuhanddc.enums.DDCTypeEnum;
import lombok.Getter;

import java.math.BigInteger;

/**
 * @author wxq
 * @create 2022/7/12 17:04
 * @description roll back transfer params
 */
@Getter
public class UpdateCrossChainStatusParams {
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

    private UpdateCrossChainStatusParams(String sender,BigInteger crossChainId, CrossChainStateEnum state, String remark) {
        this.sender = sender;
        this.crossChainId = crossChainId;
        this.state = state;
        this.remark = remark;
    }

    public static UpdateCrossChainStatusParams.Builder builder() {
        return new UpdateCrossChainStatusParams.Builder();
    }

    public static class Builder {
        private String sender;
        private BigInteger crossChainId;
        private CrossChainStateEnum state;
        private String remark;

        public UpdateCrossChainStatusParams build() {
            return new UpdateCrossChainStatusParams(
                    this.sender,
                    this.crossChainId,
                    this.state,
                    this.remark
            );
        }

        public Builder setSender(String sender) {
            this.sender = sender;
            return this;
        }

        public Builder setCrossChainId(BigInteger crossChainId) {
            this.crossChainId = crossChainId;
            return this;
        }

        public Builder setState(CrossChainStateEnum state) {
            this.state = state;
            return this;
        }

        public Builder setRemark(String remark) {
            this.remark = remark;
            return this;
        }
    }


}
