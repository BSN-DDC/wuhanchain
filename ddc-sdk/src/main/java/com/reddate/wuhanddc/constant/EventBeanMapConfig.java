package com.reddate.wuhanddc.constant;

import com.reddate.wuhanddc.dto.ddc.*;

import java.util.HashMap;

/**
 * @author wxq
 * @create 2022/1/24 16:21
 * @description eventBean config
 */
public class EventBeanMapConfig {
    /**
     * eventBeanMap
     */
    public final static HashMap<String, Class> eventBeanMap = new HashMap<>();

    static {

        // event entity binding
        eventBeanMap.put(ContractConfig.AUTHORITY_ADDRESS + AuthorityFunctions.ADD_ACCOUNT_EVENT, AddAccountEventBean.class);
        eventBeanMap.put(ContractConfig.AUTHORITY_ADDRESS + AuthorityFunctions.UPDATE_ACCOUNT_STATE_EVENT, UpdateAccountStateEventBean.class);

        eventBeanMap.put(ContractConfig.CHARGE_ADDRESS + ChargeFunctions.RECHARGE_EVENT, ReChargeEventBean.class);
        eventBeanMap.put(ContractConfig.CHARGE_ADDRESS + ChargeFunctions.PAY_EVENT, PayEventBean.class);
        eventBeanMap.put(ContractConfig.CHARGE_ADDRESS + ChargeFunctions.SET_FEE_EVENT, SetFeeEventBean.class);
        eventBeanMap.put(ContractConfig.CHARGE_ADDRESS + ChargeFunctions.DELETE_FEE_EVENT, DeleteFeeEventBean.class);
        eventBeanMap.put(ContractConfig.CHARGE_ADDRESS + ChargeFunctions.DELETE_DDC_EVENT, DeleteDDCEventBean.class);

        eventBeanMap.put(ContractConfig.DDC_721_ADDRESS + DDC721Functions.DDC_721_TRANSFER_EVENT, DDC721TransferEventBean.class);
        eventBeanMap.put(ContractConfig.DDC_721_ADDRESS + DDC721Functions.DDC_721_FREEZE_EVENT, DDC721FreezeEventBean.class);
        eventBeanMap.put(ContractConfig.DDC_721_ADDRESS + DDC721Functions.DDC_721_UNFREEZE_EVENT, DDC721UnFreezeEventBean.class);
        eventBeanMap.put(ContractConfig.DDC_721_ADDRESS + DDC721Functions.DDC_721_SET_URI_EVENT, DDC721SetURIEventBean.class);

        eventBeanMap.put(ContractConfig.DDC_1155_ADDRESS + DDC1155Functions.DDC_1155_TRANSFER_SINGLE_EVENT, DDC1155TransferSingleEventBean.class);
        eventBeanMap.put(ContractConfig.DDC_1155_ADDRESS + DDC1155Functions.DDC_1155_TRANSFER_BATCH_EVENT, DDC1155TransferBatchEventBean.class);
        eventBeanMap.put(ContractConfig.DDC_1155_ADDRESS + DDC1155Functions.DDC_1155_FREEZE_EVENT, DDC1155FreezeEventBean.class);
        eventBeanMap.put(ContractConfig.DDC_1155_ADDRESS + DDC1155Functions.DDC_1155_UNFREEZE_EVENT, DDC1155UnFreezeEventBean.class);
        eventBeanMap.put(ContractConfig.DDC_1155_ADDRESS + DDC1155Functions.DDC_1155_SET_URI_EVENT, DDC1155SetURIEventBean.class);

    }
}
