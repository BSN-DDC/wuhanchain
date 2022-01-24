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
    public final static HashMap<String, Class> eventBeanMapNew = new HashMap<>();

    static {

        // event entity binding
        eventBeanMapNew.put(AuthorityFunctions.ADD_ACCOUNT_EVENT, AddAccountEventBean.class);
        eventBeanMapNew.put(AuthorityFunctions.UPDATE_ACCOUNT_STATE_EVENT, UpdateAccountStateEventBean.class);

        eventBeanMapNew.put(ChargeFunctions.RECHARGE_EVENT, ReChargeEventBean.class);
        eventBeanMapNew.put(ChargeFunctions.PAY_EVENT, PayEventBean.class);
        eventBeanMapNew.put(ChargeFunctions.SET_FEE_EVENT, SetFeeEventBean.class);
        eventBeanMapNew.put(ChargeFunctions.DELETE_FEE_EVENT, DeleteFeeEventBean.class);
        eventBeanMapNew.put(ChargeFunctions.DELETE_DDC_EVENT, DeleteDDCEventBean.class);

        eventBeanMapNew.put(DDC721Functions.DDC_721_TRANSFER_EVENT, DDC721TransferEventBean.class);
        eventBeanMapNew.put(DDC721Functions.DDC_721_FREEZE_EVENT, DDC721FreezeEventBean.class);
        eventBeanMapNew.put(DDC721Functions.DDC_721_UNFREEZE_EVENT, DDC721UnFreezeEventBean.class);

        eventBeanMapNew.put(DDC1155Functions.DDC_1155_TRANSFER_SINGLE_EVENT, DDC1155TransferSingleEventBean.class);
        eventBeanMapNew.put(DDC1155Functions.DDC_1155_TRANSFER_BATCH_EVENT, DDC1155TransferBatchEventBean.class);
        eventBeanMapNew.put(DDC1155Functions.DDC_1155_FREEZE_EVENT, DDC1155FreezeEventBean.class);
        eventBeanMapNew.put(DDC1155Functions.DDC_1155_UNFREEZE_EVENT, DDC1155UnFreezeEventBean.class);

    }
}
