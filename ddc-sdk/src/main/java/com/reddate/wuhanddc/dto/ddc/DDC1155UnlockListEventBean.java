package com.reddate.wuhanddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author skj
 * @create 2022/8/11 14:20
 * @description lock list event bean
 */
@Data
public class DDC1155UnlockListEventBean extends BaseEventBean {
    String operator;
    BigInteger ddcId;
}