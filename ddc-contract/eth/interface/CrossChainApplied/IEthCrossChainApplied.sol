// SPDX-License-Identifier:BSN TOKEN

pragma solidity ^0.8.0;

interface IEthCrossChainApplied {
    // Cross-chain information
    struct TxArgs {
        // DDC cross-chain ID.
        uint256 crossChainId;
        // Initiation chain account.
        bytes from;
        // DDC type, refer to the enumeration type DDCType for details.
        uint8 ddcType;
        // Target chain signer account.
        bytes signer;
        // Target chain recipient account.
        bytes to;
        // Token unique identifier.
        uint256 tokenId;
        // Token cross chain quantity.
        uint256 amount;
        // Token resource identifier.
        bytes tokenURI;
        // Additional data.
        bytes data;
    }

    /**
     * @dev DDC cross-chain generation event
     */
    event CrossChainMint(
        address indexed operator,
        uint256 crossChainId,
        uint8 ddcType,
        address fromOwner,
        address signer,
        address to,
        uint256 tokenId,
        uint256 amount,
        bytes data,
        uint64 fromChainId,
        address fromCCAddr
    );

    // @dev DDC type, 0 is DDC721, 1 is DDC1155.
    enum DDCType {
        ddc721,
        ddc1155
    }

    /**
     * @dev Sets erc721 proxy address.
     *
     * Requirements:
     * - sender must be the owner only.
     */
    function setERC721Address(address erc721Address) external;

    /**
     * @dev Sets erc1155 proxy address.
     *
     * Requirements:
     * - sender must be the owner only.
     */
    function setERC1155Address(address erc1155Address) external;

    /**
     * @dev Sets eccmp proxy address.
     *
     * Requirements:
     * - sender must be the owner only.
     */
    function setECCMPAddress(address eccmpAddress) external;

    /**
     * @dev Initiation chain users call this method to generate cross-chain tokens.
     *
     *
     * Requirements:
     * - sender must have call method permission.
     * @param args Cross-chain data for from chain assembly.
     * @param fromContractAddr Original chain contract address.
     * @param fromChainId The chainId corresponding to the from chain.
     * -
     */
    function crossChainMint(
        bytes memory args,
        bytes memory fromContractAddr,
        uint64 fromChainId
    ) external returns (bool);
}
