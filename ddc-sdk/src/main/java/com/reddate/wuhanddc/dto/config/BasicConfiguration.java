package com.reddate.wuhanddc.dto.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wxq
 * @create 2021/12/11 15:30
 * @description Contract, gateway, etc. configuration
 */
public class BasicConfiguration extends Gateway {

    private List<DDCContract> contracts = new ArrayList<>();

    public void setContracts(DDCContract contracts) {
        this.contracts.add(contracts);
    }

    public List<DDCContract> getContracts() {
        return contracts;
    }

}
