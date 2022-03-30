package com.reddate.wuhanddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author wxq
 * @create 2022/2/11 15:12
 * @description
 */
@Data
public class DDC721SetURIEventBean extends BaseEventBean{
    private BigInteger ddcId;
    private String ddcURI;
}