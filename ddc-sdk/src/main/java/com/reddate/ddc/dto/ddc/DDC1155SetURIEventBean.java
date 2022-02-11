package com.reddate.ddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author wxq
 * @create 2022/2/11 15:11
 * @description
 */
@Data
public class DDC1155SetURIEventBean extends BaseEventBean{
    private BigInteger ddcId;
    private String ddcURI;
}
