package com.reddate.ddc.constant;

import com.reddate.ddc.dto.ddc.*;

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
        eventBeanMap.put(AuthorityFunctions.ADD_ACCOUNT_EVENT, AddAccountEventBean.class);
        eventBeanMap.put(AuthorityFunctions.UPDATE_ACCOUNT_STATE_EVENT, UpdateAccountStateEventBean.class);

        eventBeanMap.put(ChargeFunctions.RECHARGE_EVENT, ReChargeEventBean.class);
        eventBeanMap.put(ChargeFunctions.PAY_EVENT, PayEventBean.class);
        eventBeanMap.put(ChargeFunctions.SET_FEE_EVENT, SetFeeEventBean.class);
        eventBeanMap.put(ChargeFunctions.DELETE_FEE_EVENT, DeleteFeeEventBean.class);
        eventBeanMap.put(ChargeFunctions.DELETE_DDC_EVENT, DeleteDDCEventBean.class);

        eventBeanMap.put(DDC721Functions.DDC_721_TRANSFER_EVENT, DDC721TransferEventBean.class);
        eventBeanMap.put(DDC721Functions.DDC_721_FREEZE_EVENT, DDC721FreezeEventBean.class);
        eventBeanMap.put(DDC721Functions.DDC_721_UNFREEZE_EVENT, DDC721UnFreezeEventBean.class);
        eventBeanMap.put(DDC721Functions.DDC721SetURIEvent, DDC721SetURIEventBean.class);

        eventBeanMap.put(DDC1155Functions.DDC_1155_TRANSFER_SINGLE_EVENT, DDC1155TransferSingleEventBean.class);
        eventBeanMap.put(DDC1155Functions.DDC_1155_TRANSFER_BATCH_EVENT, DDC1155TransferBatchEventBean.class);
        eventBeanMap.put(DDC1155Functions.DDC_1155_FREEZE_EVENT, DDC1155FreezeEventBean.class);
        eventBeanMap.put(DDC1155Functions.DDC_1155_UNFREEZE_EVENT, DDC1155UnFreezeEventBean.class);
        eventBeanMap.put(DDC1155Functions.DDC1155SetURIEvent, DDC1155SetURIEventBean.class);

    }
}
