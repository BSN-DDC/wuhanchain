package com.reddate.wuhanddc.constant;

/**
 * wuhanddc-1155 contract function
 */
public class DDC1155Functions {
    public static final String SAFE_MINT = "safeMint";
    public static final String SAFE_MINT_BATCH = "safeMintBatch";
    public static final String SET_APPROVAL_FOR_ALL = "setApprovalForAll";
    public static final String IS_APPROVED_FOR_ALL = "isApprovedForAll";
    public static final String SAFE_TRANSFER_FROM = "safeTransferFrom";
    public static final String SAFE_BATCH_TRANSFER_FROM = "safeBatchTransferFrom";
    public static final String FREEZE = "freeze";
    public static final String UNFREEZE = "unFreeze";
    public static final String BURN = "burn";
    public static final String BURN_BATCH = "burnBatch";
    public static final String BALANCE_OF = "balanceOf";
    public static final String BALANCE_OF_BATCH = "balanceOfBatch";
    public static final String DDC_URI = "ddcURI";
    public static final String SET_URI = "setURI";
    public static final String GET_LATEST_DDC_ID = "getLatestDDCId";
    public static final String META_SAFE_MINT = "metaSafeMint";
    public static final String META_SAFE_MINT_BATCH = "metaSafeMintBatch";
    public static final String META_SAFE_TRANSFER_FROM = "metaSafeTransferFrom";
    public static final String META_SAFE_BATCH_TRANSFER_FROM = "metaSafeBatchTransferFrom";
    public static final String META_BURN = "metaBurn";
    public static final String META_BURN_BATCH = "metaBurnBatch";
    public static final String GET_NONCE = "getNonce";
    public static final String SYNC_DDC_OWNERS = "syncDDCOwners";

    public static final String TRANSFER_SINGLE_EVENT = "TransferSingle(address,address,address,uint256,uint256)";
    public static final String TRANSFER_BATCH_EVENT = "TransferBatch(address,address,address,uint256[],uint256[])";
    public static final String FREEZE_EVENT = "EnterBlacklist(address,uint256)";
    public static final String UNFREEZE_EVENT = "ExitBlacklist(address,uint256)";
    public static final String SET_URI_EVENT = "SetURI(uint256,string)";
    public static final String LOCK_LIST_EVENT = "Locklist(address,uint256)";
    public static final String UNLOCK_LIST_EVENT = "UnLocklist(address,uint256)";

}
