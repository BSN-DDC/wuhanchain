// SPDX-License-Identifier: BSN DDC

pragma solidity ^0.8.0;

import "../IERC165Upgradeable.sol";

interface IDDC1155 is IERC165Upgradeable {
    /**
     * @dev Event notification for a single DDC transfer
     *
     * Requirements:
     * - ``
     * - ``
     * @param operator Operator's address
     * @param from Owner’s address
     * @param to Receiver's address
     * @param ddcId  DDC unique identifier
     * @param amount quantity
     *
     */
    event TransferSingle(
        address indexed operator,
        address indexed from,
        address indexed to,
        uint256 ddcId,
        uint256 amount
    );

    /**
     * @dev Event notification for bulk DDC transfer
     *
     * Requirements:
     * - ``
     * - ``
     * @param operator Operator's address
     * @param from Owner’s address
     * @param to Receiver's address
     * @param ddcIds ddc uniquely identifies the collection
     * @param amounts quantity collection
     *
     */
    event TransferBatch(
        address indexed operator,
        address indexed from,
        address indexed to,
        uint256[] ddcIds,
        uint256[] amounts
    );

    /**
     * @dev Emitted when `owner` or (`approved`) `operator` set uri.
     */
    event SetURI(uint256 indexed ddcId, string ddcURI);

    /**
     * @dev DDC freeze event notification
     *
     * Requirements:
     * - ``
     * - ``
     * @param sender Operator's address
     * @param ddcId  DDC unique identifier
     *
     */
    event EnterBlacklist(address indexed sender, uint256 ddcId);

    /**
     * @dev DDC unfreeze event notification
     *
     * Requirements:
     * - ``
     * - ``
     * @param sender Operator's address
     * @param ddcId  DDC unique identifier
     *
     */
    event ExitBlacklist(address indexed sender, uint256 ddcId);

    /**
     * @dev Authorize all DDCs under the owner to the authorizer account for event notification
     *
     * Requirements:
     * - ``
     * - ``
     * @param owner Owner's address
     * @param operator Operator's address
     * @param approved Authorize or deauthorize value
     *
     */
    event ApprovalForAll(
        address indexed owner,
        address indexed operator,
        bool approved
    );

    /**
     * @dev Represents the type of HashType.
     */
    enum HashType {
        safeMint,
        safeMintBatch,
        safeTransfer,
        safeTransferBatch,
        burn,
        burnBatch
    }

    /**
     * @dev Emitted when `ddcId` ddc is transferred from `from` to `to`.
     */
    event MetaTransferSingle(
        address indexed operator,
        address indexed from,
        address indexed to,
        uint256 ddcId,
        uint256 amount
    );

    event MetaTransferBatch(
        address indexed operator,
        address indexed from,
        address indexed to,
        uint256[] ddcIds,
        uint256[] amounts
    );

    /**
     * @dev The DDC cross-chain application contract calls this API to lock the DDC cross-chain.
     */
    event Locklist(address indexed operator, uint256 ddcId);

    /**
     * @dev DDC cross-chain application contract calls this API to unlock DDC cross-chain.
     */
    event UnLocklist(address indexed operator, uint256 ddcId);

    /**
     * @dev The owner information corresponding to the DDC is synchronized.
     */
    event SyncDDCOwners(
        address indexed operator,
        uint256[] ddcIds,
        address[][] owners
    );

    /**
     * @dev Emitted when the pause is triggered by `account`.
     */
    event Paused(address account);

    /**
     * @dev Emitted when the pause is lifted by `account`.
     */
    event Unpaused(address account);

    /**
     * @dev Sets charge proxy address.
     *
     * Requirements:
     * - sender must be the owner only.
     */
    function setChargeProxyAddress(address chargeProxyAddress) external;

    /**
     * @dev  Sets authority proxy address.
     *
     * Requirements:
     * - sender must be the owner only.
     */
    function setAuthorityProxyAddress(address authorityProxyAddress) external;

    /**
     * @dev  Sets meta type hash args.
     *
     * Requirements:
     * - sender must be the owner only.
     */
    function setMetaTypeHashArgs(HashType hashType, bytes32 hashValue) external;

    /**
     * @dev  Sets meta spearator arg.
     *
     * Requirements:
     * - sender must be the owner only.
     */
    function setMetaSeparatorArg(bytes32 separator) external;

    /**
     * @dev  Creates a new ddc for `to`.
     *
     * Requirements:
     * - sender's role is platform only.
     * - sender must have call method permission.
     * - `to` must have the `DDC` attribute
     * @param to Generate the recipient account address corresponding to the ddc
     * @param amount The amount corresponding to the generated ddc
     * @param _ddcURI URI corresponding to ddc
     * @param data Additional data with no specified format
     */
    function safeMint(
        address to,
        uint256 amount,
        string memory _ddcURI,
        bytes memory data
    ) external;

    /**
     * @dev  Creates a new ddc for `to`.
     *
     * Requirements:
     * - sender's role is crosschain only.
     * - sender must have call method permission.
     * - `to` must have the `DDC` attribute
     * @param to Generate the recipient account address corresponding to the ddc
     * @param amount The amount corresponding to the generated ddc
     * @param _ddcURI URI corresponding to ddc
     * @param data Additional data with no specified format
     */
    function crossSafeMint(
        address to,
        uint256 amount,
        string memory _ddcURI,
        bytes memory data
    ) external;

    /**
     * @dev  Creates ddc list for `to`.
     *
     * Requirements:
     * - ``
     * - ``
     * @param to Generate the recipient account address corresponding to the ddc
     * @param amounts Generate the quantity set corresponding to each ddc
     * @param ddcURIs Generate the uri set corresponding to each ddc
     * @param data Additional data with no specified format
     */
    function safeMintBatch(
        address to,
        uint256[] memory amounts,
        string[] memory ddcURIs,
        bytes memory data
    ) external;

    /**
     * @dev  Set the URI for ddcId when ddcURI is not set. (and available on the emitted {IDDC721-SetURI} event)
     *
     * Requirements:
     * - sender must have call method permission.
     * - The ddcURI is not initialized.
     */
    function setURI(
        address owner,
        uint256 ddcId,
        string memory ddcURI_
    ) external;

    /**
     * @dev Authorize all DDCs under the owner to the authorizer account
     *
     * Requirements:
     * - ``
     * - ``
     * @param operator authorizer address
     * @param approved authorization ID
     *
     */
    function setApprovalForAll(address operator, bool approved) external;

    /**
     * @dev Check whether all DDCs under the owner are authorized to the authorizer account
     *
     * Requirements:
     * - ``
     * - ``
     * @param owner  Owner account address
     * @param operator Authorized user
     *
     * @return
     */
    function isApprovedForAll(
        address owner,
        address operator
    ) external view returns (bool);

    /**
     * @notice getNonce for metatransfer
     *
     */
    function getNonce(address from) external view returns (uint256);

    /**
     * @dev Transfer a certain amount of a single DDC from the from account address to the to account address
     *
     * Requirements:
     * - ``
     * - ``
     * @param from Owner’s address
     * @param to Receiver's address
     * @param ddcId  DDC unique identifier
     * @param amount quantity
     * @param data additional data
     *
     */
    function safeTransferFrom(
        address from,
        address to,
        uint256 ddcId,
        uint256 amount,
        bytes memory data
    ) external;

    /**
     * @dev Transfer the DDC list from the from account address to the to account address according to a certain number of sets
     *
     * Requirements:
     * - ``
     * - ``
     * @param from Owner’s address
     * @param to Receiver's address
     * @param ddcIds ddc uniquely identifies the collection
     * @param amounts quantity collection
     * @param data additional data
     *
     */
    function safeBatchTransferFrom(
        address from,
        address to,
        uint256[] memory ddcIds,
        uint256[] memory amounts,
        bytes memory data
    ) external;

    /**
     * @dev Freeze a ddc
     *
     * Requirements:
     * - ``
     * - ``
     * @param ddcId  DDC unique identifier
     *
     */
    function freeze(uint256 ddcId) external;

    /**
     * @dev Unfreeze a ddc
     *
     * Requirements:
     * - ``
     * - ``
     * @param ddcId  DDC unique identifier
     *
     */
    function unFreeze(uint256 ddcId) external;

    /**
     * @dev Destroy a single DDC owned by the owner
     *
     * Requirements:
     * - ``
     * - ``
     * @param owner  Owner account address
     * @param ddcId  DDC unique identifier
     *
     */
    function burn(address owner, uint256 ddcId) external;

    /**
     * @dev Destroy the DDC list owned by the owner in batches
     *
     * Requirements:
     * - ``
     * - ``
     * @param owner  Owner account address
     * @param ddcIds  DDC unique identifiers
     *
     */
    function burnBatch(address owner, uint256[] memory ddcIds) external;

    /**
     * @dev Query the number of Owner's ownership of a single DDC
     *
     * Requirements:
     * - ``
     * - ``
     * @param owner  Owner account address
     * @param ddcId  DDC unique identifier
     *
     * @return Returns the number of ddc owned by the owner
     */
    function balanceOf(
        address owner,
        uint256 ddcId
    ) external view returns (uint256);

    /**
     * @dev Query the number of Owner used by each DDC in the DDC list
     *
     * Requirements:
     * - ``
     * - ``
     * @param owners  Owner account addresses
     * @param ddcIds  DDC unique identifiers
     *
     * @return Returns the number of ddc owned by the owners
     */
    function balanceOfBatch(
        address[] memory owners,
        uint256[] memory ddcIds
    ) external view returns (uint256[] memory);

    /**
     * @dev Query the URI corresponding to the DDC according to the DDCID
     *
     * Requirements:
     * - ``
     * - ``
     * @param ddcId  DDC unique identifier
     *
     * @return
     */
    function ddcURI(uint256 ddcId) external view returns (string memory);

    /**
     * @dev Returns the last ddcID.
     *
     * Requirements:
     */
    function getLatestDDCId() external view returns (uint256);

    /**
     * @dev  Creates a new ddc for `to`.
     *
     * Requirements:
     * - sender's role is platform only.
     * - sender must have call method permission.
     * - `to` must have the `DDC` attribute
     * @param to Generate the recipient account address corresponding to the ddc
     * @param amount The amount corresponding to the generated ddc
     * @param _ddcURI URI corresponding to ddc
     * @param data Additional data with no specified format
     */
    function metaSafeMint(
        address to,
        uint256 amount,
        string memory _ddcURI,
        bytes memory data,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) external;

    /**
     * @dev  Creates ddc list for `to`.
     *
     * Requirements:
     * - ``
     * - ``
     * @param to Generate the recipient account address corresponding to the ddc
     * @param amounts Generate the quantity set corresponding to each ddc
     * @param ddcURIs Generate the uri set corresponding to each ddc
     * @param data Additional data with no specified format
     */
    function metaSafeMintBatch(
        address to,
        uint256[] memory amounts,
        string[] memory ddcURIs,
        bytes memory data,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) external;

    /**
     * @dev Transfer a certain amount of a single DDC from the from account address to the to account address
     *
     * Requirements:
     * - ``
     * - ``
     * @param from Owner’s address
     * @param to Receiver's address
     * @param ddcId  DDC unique identifier
     * @param amount quantity
     * @param data additional data
     *
     */
    function metaSafeTransferFrom(
        address from,
        address to,
        uint256 ddcId,
        uint256 amount,
        bytes memory data,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) external;

    /**
     * @dev Transfer the DDC list from the from account address to the to account address according to a certain number of sets
     *
     * Requirements:
     * - ``
     * - ``
     * @param from Owner’s address
     * @param to Receiver's address
     * @param ddcIds ddc uniquely identifies the collection
     * @param amounts quantity collection
     * @param data additional data
     *
     */
    function metaSafeBatchTransferFrom(
        address from,
        address to,
        uint256[] memory ddcIds,
        uint256[] memory amounts,
        bytes memory data,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) external;

    /**
     * @dev meta tranfer burns a ddc.
     *
     * Requirements:
     * - sender must own `ddcId` or be an approved operator.
     */
    function metaBurn(
        address owner,
        uint256 ddcId,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) external;

    /**
     * @dev Destroy the DDC list owned by the owner in batches
     *
     * Requirements:
     * - ``
     * - ``
     * @param owner  Owner account address
     * @param ddcIds  DDC unique identifiers
     *
     */
    function metaBurnBatch(
        address owner,
        uint256[] memory ddcIds,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) external;

    /**
     * @dev Perform DDC cross-chain lock.
     *
     * Requirements:
     * - ``
     * - ``
     * @param ddcId  DDC unique identifier
     *
     */
    function lock(uint256 ddcId) external;

    /**
     * @dev Perform DDC cross-chain unlocking.
     *
     * Requirements:
     * - ``
     * - ``
     * @param ddcId  DDC unique identifier
     *
     */
    function unlock(uint256 ddcId) external;

    /**
     * @dev Returns the owner of the `ddcId` ddc.
     *
     * Requirements:
     * - ``
     * - ``
     * @param ddcId  DDC unique identifier
     *
     */
    function ownerOf(uint256 ddcId) external view returns (address[] memory);

    /**
     * @dev Synchronize the owner list information corresponding to the old DDC by calling this method.
     *
     * Requirements:
     * - ``
     * - ``
     * @param ddcIds  DDC unique identifiers
     * @param owners  Owner account address
     *
     */
    function syncDDCOwners(
        uint256[] memory ddcIds,
        address[][] memory owners
    ) external;
}
