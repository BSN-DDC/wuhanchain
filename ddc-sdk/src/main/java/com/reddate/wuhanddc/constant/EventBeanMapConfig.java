package com.reddate.wuhanddc.constant;

import com.reddate.wuhanddc.dto.ddc.*;

import java.util.HashMap;

/**
 * @author skj
 * @create 2022/8/30 16:21
 * @description eventBean config
 */
public class EventBeanMapConfig {
    /**
     * eventBeanMap
     */
    public final static HashMap<String, Class> eventBeanMap = new HashMap<>();

    public static void setEventBeanMap(String authorityAddress, String chargeAddress, String ddc721Address, String ddc1155Address, String crossChainAddress) {
        // event entity binding
        eventBeanMap.put(authorityAddress + AuthorityFunctions.ADD_ACCOUNT_EVENT, AddAccountEventBean.class);
        eventBeanMap.put(authorityAddress + AuthorityFunctions.UPDATE_ACCOUNT_STATE_EVENT, UpdateAccountStateEventBean.class);

        eventBeanMap.put(authorityAddress + AuthorityFunctions.SET_SWITCHER_STATE_OF_PLATFORM_EVENT, SetSwitchStateOfPlatformBean.class);
        eventBeanMap.put(authorityAddress + AuthorityFunctions.ADD_BATCH_ACCOUNT_EVENT, AddAccountBatchEventBean.class);
        eventBeanMap.put(authorityAddress + AuthorityFunctions.CROSS_PLATFORM_APPROVAL_EVENT, CrossPlatFormApprovalEventBean.class);
        eventBeanMap.put(authorityAddress + AuthorityFunctions.SYNC_PLATFORM_DID_EVENT, SyncPlatformDIDEventBean.class);

        eventBeanMap.put(chargeAddress + ChargeFunctions.RECHARGE_EVENT, ReChargeEventBean.class);
        eventBeanMap.put(chargeAddress + ChargeFunctions.RECHARGE_BATCH_EVENT, ReChargeBatchEventBean.class);
        eventBeanMap.put(chargeAddress + ChargeFunctions.PAY_EVENT, PayEventBean.class);
        eventBeanMap.put(chargeAddress + ChargeFunctions.SET_FEE_EVENT, SetFeeEventBean.class);
        eventBeanMap.put(chargeAddress + ChargeFunctions.DELETE_FEE_EVENT, DeleteFeeEventBean.class);
        eventBeanMap.put(chargeAddress + ChargeFunctions.DELETE_DDC_EVENT, DeleteDDCEventBean.class);

        eventBeanMap.put(ddc721Address + DDC721Functions.TRANSFER_EVENT, DDC721TransferEventBean.class);
        eventBeanMap.put(ddc721Address + DDC721Functions.FREEZE_EVENT, DDC721FreezeEventBean.class);
        eventBeanMap.put(ddc721Address + DDC721Functions.UNFREEZE_EVENT, DDC721UnFreezeEventBean.class);
        eventBeanMap.put(ddc721Address + DDC721Functions.SET_URI_EVENT, DDC721SetURIEventBean.class);
        eventBeanMap.put(ddc721Address + DDC721Functions.LOCK_LIST_EVENT, DDC721LockListEventBean.class);
        eventBeanMap.put(ddc721Address + DDC721Functions.UNLOCK_LIST_EVENT, DDC721UnlockListEventBean.class);
        eventBeanMap.put(ddc721Address + DDC721Functions.META_TRANSFER_EVENT, DDC721MetaTransferEventBean.class);

        eventBeanMap.put(ddc1155Address + DDC1155Functions.TRANSFER_SINGLE_EVENT, DDC1155TransferSingleEventBean.class);
        eventBeanMap.put(ddc1155Address + DDC1155Functions.TRANSFER_BATCH_EVENT, DDC1155TransferBatchEventBean.class);
        eventBeanMap.put(ddc1155Address + DDC1155Functions.FREEZE_EVENT, DDC1155FreezeEventBean.class);
        eventBeanMap.put(ddc1155Address + DDC1155Functions.UNFREEZE_EVENT, DDC1155UnFreezeEventBean.class);
        eventBeanMap.put(ddc1155Address + DDC1155Functions.SET_URI_EVENT, DDC1155SetURIEventBean.class);
        eventBeanMap.put(ddc1155Address + DDC1155Functions.LOCK_LIST_EVENT, DDC1155LockListEventBean.class);
        eventBeanMap.put(ddc1155Address + DDC1155Functions.UNLOCK_LIST_EVENT, DDC1155UnlockListEventBean.class);
        eventBeanMap.put(ddc1155Address + DDC1155Functions.META_TRANSFER_SINGLE_EVENT, DDC1155MetaTransferSingleEventBean.class);

        eventBeanMap.put(crossChainAddress + DDCCrossChainFunctions.CROSS_CHAIN_TRANSFER_EVENT, CrossChainTransferEventBean.class);
        eventBeanMap.put(crossChainAddress + DDCCrossChainFunctions.UPDATE_CROSS_CHAIN_STATUS_EVENT, UpdateCrossChainStatusEventBean.class);
    }
}
