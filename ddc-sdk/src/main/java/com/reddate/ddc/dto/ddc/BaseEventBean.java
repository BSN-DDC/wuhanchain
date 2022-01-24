package com.reddate.ddc.dto.ddc;

import lombok.Data;

@Data
public class BaseEventBean {
    private String timestamp;
    private String blockNumber;
    private String blockHash;
    private Object transactionInfoBean;
}
