// SPDX-License-Identifier:BSN TOKEN

pragma solidity ^0.8.0;

import "../../interface/CrossChainApplied/IEthCrossChainApplied.sol";
import "../ERCContract/ERC721.sol";
import "../ERCContract/ERC1155.sol";
import "../../utils/ZeroCopySource.sol";
import "../../utils/OwnableUpgradeable.sol";
import "../../proxy/utils/UUPSUpgradeable.sol";

contract EthCrossChainApplied is
    IEthCrossChainApplied,
    OwnableUpgradeable,
    UUPSUpgradeable
{
    // ERC721 proxy contract
    ERC721 private _erc721Proxy;

    // ERC1155 proxy contract
    ERC1155 private _erc1155Proxy;

    // Cross chain management contract address
    address _eccmpAddress;

    constructor() initializer {}

    function initialize() public initializer {
        __Ownable_init();
        __UUPSUpgradeable_init();
    }

    /**
     * @dev Function that should revert when `msg.sender` is not authorized to upgrade the contract. Called by
     * {upgradeTo} and {upgradeToAndCall}.
     *
     * Normally, this function will use an xref:access.adoc[access control] modifier such as {Ownable-onlyOwner}.
     */
    function _authorizeUpgrade(address newImplementation)
        internal
        override
        onlyOwner
    {}

    /**
     * @dev Sets erc721 proxy address.
     *
     * Requirements:
     * - sender must be the owner only.
     */
    function setERC721Address(address erc721Address) public override onlyOwner {
        _requireContract(erc721Address);
        _erc721Proxy = ERC721(erc721Address);
    }

    /**
     * @dev Sets erc1155 proxy address.
     *
     * Requirements:
     * - sender must be the owner only.
     */
    function setERC1155Address(address erc1155Address)
        public
        override
        onlyOwner
    {
        _requireContract(erc1155Address);
        _erc1155Proxy = ERC1155(erc1155Address);
    }

    /**
     * @dev Sets eccmp proxy address.
     *
     * Requirements:
     * - sender must be the owner only.
     */
    function setECCMPAddress(address eccmpAddress) public override onlyOwner {
        _requireContract(eccmpAddress);
        _eccmpAddress = eccmpAddress;
    }

    /**
     * @dev Initiation chain users call this method to generate cross-chain tokens.
     *
     *
     * Requirements:
     * -
     */
    function crossChainMint(
        bytes memory args,
        bytes memory fromContractAddr,
        uint64 fromChainId
    ) public override returns (bool) {
        // required is cross chain management of contract address
        require(
            _msgSender() == _eccmpAddress,
            "CrossChain:The sender must be a cross chain management contract"
        );

        // Get data transmitted across chains.
        TxArgs memory txArgs = _deserializeTxArgs(args);

        // Check the to address in the transmitted data, the length of the to address cannot be zero.
        require(txArgs.to.length != 0, "CrossChain:toAddress cannot be empty");

        // Determine whether the DDC type is 721 or 1155, and call the safe generation method.
        if (txArgs.ddcType == uint8(DDCType.ddc721)) {
            _erc721Proxy.safeMint(
                _bytesToAddress(txArgs.to),
                string(txArgs.tokenURI),
                txArgs.data
            );
        } else if (txArgs.ddcType == uint8(DDCType.ddc1155)) {
            _erc1155Proxy.safeMint(
                _bytesToAddress(txArgs.to),
                txArgs.amount,
                string(txArgs.tokenURI),
                txArgs.data
            );
        } else {
            revert("CrossChain:cross chain type error.");
        }

        // Call event
        emit CrossChainMint(
            tx.origin,
            txArgs.crossChainId,
            txArgs.ddcType,
            _bytesToAddress(txArgs.from),
            _bytesToAddress(txArgs.signer),
            _bytesToAddress(txArgs.to),
            txArgs.tokenId,
            txArgs.amount,
            txArgs.data,
            fromChainId,
            _bytesToAddress(fromContractAddr)
        );
        return true;
    }

    /**
     * @dev Decoding transmitted data.
     */
    function _deserializeTxArgs(bytes memory values)
        internal
        pure
        returns (TxArgs memory)
    {
        TxArgs memory args;
        uint256 off = 0;
        (args.crossChainId, off) = ZeroCopySource.NextUint256(values, off);
        (args.from, off) = ZeroCopySource.NextVarBytes(values, off);
        (args.ddcType, off) = ZeroCopySource.NextUint8(values, off);
        (args.signer, off) = ZeroCopySource.NextVarBytes(values, off);
        (args.to, off) = ZeroCopySource.NextVarBytes(values, off);
        (args.tokenId, off) = ZeroCopySource.NextUint256(values, off);
        (args.amount, off) = ZeroCopySource.NextUint256(values, off);
        (args.tokenURI, off) = ZeroCopySource.NextVarBytes(values, off);
        (args.data, off) = ZeroCopySource.NextVarBytes(values, off);
        return args;
    }

    // Byte-to-address
    function _bytesToAddress(bytes memory bys)
        internal
        pure
        returns (address addr)
    {
        assembly {
            addr := mload(add(bys, 0x14))
        }
    }

    /**
     * @dev Requires contract address on chain.
     *
     * Requirements:
     * - `account` must not be zero address.
     * - `account` must be a contract.
     */
    function _requireContract(address account) private view {
        require(account != address(0), "CrossChain:zero address");
    }
}
