package com.reddate.wuhanddc.dto.ddc;

import lombok.Data;
import org.web3j.protocol.core.methods.response.EthBlock;

@Data
public class BaseEventBean {
    private String timestamp;
    private String blockNumber;
    private String blockHash;
    private EthBlock.TransactionObject transactionInfoBean;
}
