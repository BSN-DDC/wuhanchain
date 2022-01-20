package com.reddate.ddc.constant;

/**
 * ddc-1155 contract function
 */
public class DDC1155Functions {
    public static final String Mint = "safeMint";
    public static final String MINT_BATCH = "safeMintBatch";
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

    public static final String DDC_1155_TRANSFER_SINGLE_EVENT = "TransferSingle(address,address,address,uint256,uint256)";
    public static final String DDC_1155_TRANSFER_BATCH_EVENT = "TransferBatch(address,address,address,uint256[],uint256[])";
    public static final String DDC_1155_FREEZE_EVENT = "EnterBlacklist(address,uint256)";
    public static final String DDC_1155_UNFREEZE_EVENT = "ExitBlacklist(address,uint256)";

}
