package com.reddate.wuhanddc.dto.ddc;

import lombok.Data;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReChargeBatchEventBean extends BaseEventBean {
	
	/** 原链账户地址 */
    String from;
    
    /** 目标链账户地址 */
    ArrayList<String> toList;
    
    /** 业务费 */
    ArrayList<BigInteger> amounts;
}
