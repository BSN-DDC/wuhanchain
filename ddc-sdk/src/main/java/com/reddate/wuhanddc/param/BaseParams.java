package com.reddate.wuhanddc.param;

import com.reddate.wuhanddc.enums.DDCTypeEnum;
import lombok.Data;

/**
 * @author wxq
 * @create 2022/7/13 17:26
 * @description base params
 */
@Data
public class BaseParams {
    /**
     * Caller
     */
    String sender;
    /**
     * ddc type：721 or 1155
     */
    DDCTypeEnum ddcType;
}
