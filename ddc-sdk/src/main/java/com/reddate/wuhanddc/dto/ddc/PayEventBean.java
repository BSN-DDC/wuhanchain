package com.reddate.wuhanddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

@Data
public class PayEventBean extends BaseEventBean {

    /** 链账户地址 */
    String payer;

    /** 业务主合约 */
    String payee;

    /** 方法签名  */
    String sig;

    /** 业务费 */
    BigInteger amount;

    /** ddcId */
    BigInteger ddcId;

}
