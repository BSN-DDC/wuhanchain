package com.reddate.wuhanddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author skj
 * @create 2022/8/11 14:20
 * @description lock list event bean
 */
@Data
public class DDC721LockListEventBean extends BaseEventBean {
    String operator;
    String owner;
    BigInteger ddcId;
}
