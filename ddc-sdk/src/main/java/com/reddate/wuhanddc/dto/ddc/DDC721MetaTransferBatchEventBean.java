package com.reddate.wuhanddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;
import java.util.ArrayList;

@Data
public class DDC721MetaTransferBatchEventBean extends BaseEventBean {

    /** 签名者 */
    String operator;

    /** 拥有账户地址 */
    String from;

    /** 接收账户地址 */
    String to;

    /** DDC ID */
    ArrayList<BigInteger> ddcIds;
}
