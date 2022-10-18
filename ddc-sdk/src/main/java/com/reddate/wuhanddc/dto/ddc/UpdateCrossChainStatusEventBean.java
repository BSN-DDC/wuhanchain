package com.reddate.wuhanddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author wxq
 * @create 2022/7/12 17:36
 * @description cross chain transfer evnet
 */
@Data
public class UpdateCrossChainStatusEventBean extends BaseEventBean {
    String operator;
    BigInteger crossChainId;
    BigInteger state;
    String remark;
}
