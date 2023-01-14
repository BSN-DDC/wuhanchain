package com.reddate.wuhanddc.dto.ddc;

import lombok.Data;

@Data
public class CrossPlatFormApprovalEventBean extends BaseEventBean{
    /** 授权账户 */
    String from;

    /**接收账户*/
    String to;

    /**授权标识*/
    boolean	approved;
}
