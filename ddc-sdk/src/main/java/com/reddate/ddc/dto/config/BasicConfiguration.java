package com.reddate.ddc.dto.config;

import java.util.List;

/**
 * @author wxq
 * @create 2021/12/11 15:30
 * @description Contract, gateway, etc. configuration
 */
public class BasicConfiguration extends Gateway {

    private List<DDCContract> contracts;

    public void setContracts(List<DDCContract> contracts) {
        this.contracts = contracts;
    }

    public List<DDCContract> getContracts() {
        return contracts;
    }

}
