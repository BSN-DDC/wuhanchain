package com.reddate.wuhanddc.dto.ddc;

import lombok.Data;

import java.util.ArrayList;
@Data
public class AddAccountBatchEventBean extends BaseEventBean{
    /**签名者*/
    String operator;

    /**链账户地址集合*/
    ArrayList<String> accounts;
}
