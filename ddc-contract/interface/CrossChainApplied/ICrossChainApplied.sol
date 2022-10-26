// SPDX-License-Identifier:BSN DDC

pragma solidity ^0.8.0;

interface ICrossChainApplied {
    // @dev Cross-chain information
    struct CrossChainInfo {
        // @dev DDC cross-chain ID.
        uint256 crossChainId;
        // @dev Used to distinguish the DDC type corresponding to DDC cross-chain: 0:721 1:1155
        uint8 ddcType;
        // @dev The caller corresponding to the initiation of DDC cross-chain transfer
        address sender;
        // @dev The corresponding DDC owner when DDC cross-chain transfer is initiated
        address owner;
        // @dev DDC unique identifier.
        uint256 ddcId;
        // @dev DDC cross-chain business fee.
        uint256 fee;
        // @dev DDC cross-chain status 0:cross chain 1:successlly 2:failed
        uint8 state;
        // @dev Remark
        string remark;
    }

    // @dev DDC cross chain business data.
    struct DDCCrossChainData {
        // @dev DDC cross-chain ID.
        uint256 crossChainId;
        // @dev Initiation chain account.
        bytes from;
        // @dev DDC type, refer to the enumeration type DDCType for details.
        uint8 ddcType;
        // @dev Target chain signer account.
        bytes signer;
        // @dev Target chain recipient account.
        bytes to;
        // @dev DDC unique identifier.
        uint256 ddcId;
        // @dev DDC resource identifier.
        bytes ddcURI;
        // @dev Additional data.
        bytes data;
        // @dev DDC cross-chain quantity.
        uint256 amount;
    }

    // @dev temporary data
    struct tempArgs {
        // @dev Amount of DDCs.
        uint256 amount;
        // @dev DDC uri.
        string ddcURI;
        // @dev DDC owner.
        address fromOwner;
    }

    // @dev cross-chain status
    enum State {
        CrossChain,
        CrossChainSuccess,
        CrossChainFailure
    }

    // @dev DDC type, 0 is DDC721, 1 is DDC1155.
    enum DDCType {
        ddc721,
        ddc1155
    }

    /**
     * @dev Basic data settings event
     */
    event SetBaseData(
        address operator,
        address eccmpAddress,
        uint64 fromChainID
    );

    /**
     * @dev DDC cross-chain flow event
     */
    event CrossChainTransfer(
        address indexed operator,
        uint256 crossChainId,
        DDCType ddcType,
        address signer,
        address to,
        uint256 ddcId,
        string ddcURI,
        uint256 amount,
        uint64 fromChainID,
        uint256 toChainID,
        address fromCCAddr,
        address toCCAddr,
        uint256 crossChainFee
    );

    /**
     * @dev DDC cross-chain rollback event
     */
    event UpdateCrossChainStatus(
        address indexed operator,
        uint256 ddcId,
        State state,
        string remark
    );

    /**
     * @dev DDC cross-chain generation event
     */
    event CrossChainMint(
        address indexed operator,
        uint256 crossChainId,
        DDCType ddcType,
        address signer,
        address to,
        uint256 ddcId,
        string ddcURI,
        uint256 amount,
        uint64 fromChainID,
        uint256 toChainID,
        address fromCCAddr,
        address toCCAddr
    );

    /**
     * @dev Basic data settings
     *
     * Requirements:
     * - ``
     * - ``
     * @param eccmpAddress Cross-chain management agent contract address
     * @param fromChainID Starting chain ID
     *
     */
    function setBaseData(address eccmpAddress, uint64 fromChainID) external;

    /**
     * @dev  send a ddc to destination chain from original chain on the platform of bsn.
     *  need to change owner of the ddc to `lock account`, which used to manage the ddc.
     */
    function crossChainTransfer(
        DDCType ddcType,
        address signer,
        address to,
        uint256 ddcId,
        bytes memory data,
        uint64 toChainID,
        address toCCAddr,
        string memory funcName
    ) external;

    /**
     * @dev  rollback the original cross chain transaction.
     *  need to change owner of the ddc to the original account from the `lock account`.
     *
     * Requirements:
     * - the role must be `operator`
     */
    function updateCrossChainStatus(
        uint256 crossChainID,
        State state,
        string memory remark
    ) external;

    /**
     * @dev Initiation chain users call this method to generate cross-chain tokens.
     *
     *
     * Requirements:
     * - sender must have call method permission.
     * @param ccData Cross-chain data for from chain assembly.
     * @param fromCCAddr Original chain contract address.
     * @param fromChainID The chainId corresponding to the from chain.
     * -
     */
    function crossChainMint(
        bytes memory ccData,
        bytes memory fromCCAddr,
        uint64 fromChainID
    ) external;
}
