package com.reddate.ddc;

import com.reddate.ddc.constant.DDCContractType;
import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.dto.config.BasicConfiguration;
import com.reddate.ddc.dto.config.DDCContract;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.listener.SignEventListener;
import com.reddate.ddc.service.*;
import com.reddate.ddc.util.JsonFileUtil;
import org.web3j.utils.Strings;

import java.io.File;
import java.util.Objects;

/**
 * @author wxq
 * @create 2021/12/13 16:08
 * @description DdcClient
 */
public class DDCSdkClient {

    public static volatile DDCSdkClient ddcSdkClient = null;

    public AuthorityService authorityService;
    public ChargeService chargeService;
    public DDC1155Service ddc1155Service;
    public DDC721Service ddc721Service;
    public BlockEventService blockEventService;
    public AccountService accountService;
    public BaseService baseService;

    private void DDCSdkClient() {
    }

    private DDCSdkClient init(String filePath, SignEventListener signEventListener) {
        if (Strings.isEmpty(filePath)) {
            throw new DDCException(ErrorMessage.BASIC_CONFIGURATION_IS_EMPTY);
        }
        if (Objects.isNull(signEventListener)) {
            throw new DDCException(ErrorMessage.SIGN_EVENT_LISTENER_IS_EMPTY);
        }
        // loud configuration
        BasicConfiguration basicConfiguration = JsonFileUtil.readJsonFile(new File(filePath));
        if (Objects.isNull(basicConfiguration)) {
            throw new DDCException(ErrorMessage.BASIC_CONFIGURATION_READ_FAILED);
        }

        for (DDCContract contract : basicConfiguration.getContracts()) {
            if (contract.getConfigType().equalsIgnoreCase(DDCContractType.DDC_721.getType())) {
                ddc721Service = new DDC721Service(contract);
            }
            if (contract.getConfigType().equalsIgnoreCase(DDCContractType.DDC_1155.getType())) {
                ddc1155Service = new DDC1155Service(contract);
            }
            if (contract.getConfigType().equalsIgnoreCase(DDCContractType.DDC_CHARGE.getType())) {
                chargeService = new ChargeService(contract);
            }
            if (contract.getConfigType().equalsIgnoreCase(DDCContractType.DDC_AUTHORITY.getType())) {
                authorityService = new AuthorityService(contract);
            }
        }
        blockEventService = new BlockEventService(basicConfiguration);
        accountService = new AccountService();
        baseService = new BaseService();

        BaseService.signEventListener = signEventListener;
        BaseService.gatewayConfig.setGasLimit(basicConfiguration.getGasLimit());
        BaseService.gatewayConfig.setGasPrice(basicConfiguration.getGasPrice());
        BaseService.gatewayConfig.setGateWayUrl(basicConfiguration.getGateWayUrl());
        ddcSdkClient = this;
        return ddcSdkClient;
    }

    public DDCSdkClient instance(String filePath, SignEventListener signEventListener) {
        if (null == ddcSdkClient) {
            synchronized (DDCSdkClient.class) {
                if (null == ddcSdkClient) {
                    return init(filePath, signEventListener);
                }
            }
        }
        return ddcSdkClient;
    }
}
