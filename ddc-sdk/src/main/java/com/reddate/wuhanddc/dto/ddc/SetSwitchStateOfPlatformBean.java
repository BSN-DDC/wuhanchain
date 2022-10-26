package com.reddate.wuhanddc.dto.ddc;

import lombok.Data;

@Data
public class SetSwitchStateOfPlatformBean extends BaseEventBean {
    /** 签名者 */
    String operator;

    /** isOpen */
    boolean isOpen;
}
