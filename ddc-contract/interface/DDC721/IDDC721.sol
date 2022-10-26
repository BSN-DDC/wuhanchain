// SPDX-License-Identifier: BSN DDC

pragma solidity ^0.8.0;

import "../IERC165Upgradeable.sol";

/**
 * @dev DDC721 logic contract interface
 */
interface IDDC721 is IERC165Upgradeable {
    /**
     * @dev Emitted when `ddc` ddc is initialized.
     */
    event SetNameAndSymbol(string name, string symbol);

    /**
     * @dev Emitted when `ddcId` ddc is transferred from `from` to `to`.
     */
    event Transfer(
        address indexed from,
        address indexed to,
        uint256 indexed ddcId
    );

    /**
     * @dev Emitted when `ddcId` ddc is batch transferred from `from` to `to`.
     */
    event TransferBatch(
        address indexed operator,
        address indexed from,
        address indexed to,
        uint256[] ddcIds
    );

    /**
     * @dev Emitted when `owner` enables `approved` to manage the `ddcId` ddc.
     */
    event Approval(
        address indexed owner,
        address indexed approved,
        uint256 indexed ddcId
    );

    /**
     * @dev Emitted when `owner` enables or disables (`approved`) `operator` to manage all of its assets.
     */
    event ApprovalForAll(
        address indexed owner,
        address indexed operator,
        bool approved
    );

    /**
     * @dev Emitted when `owner` or (`approved`) `operator` set uri.
     */
    event SetURI(uint256 indexed ddcId, string ddcURI);

    /**
     * @dev Emitted when `sender` disables the ddc.
     */
    event EnterBlacklist(address indexed sender, uint256 ddcId);

    /**
     * @dev Emitted when `sender` enables the ddc.
     */
    event ExitBlacklist(address indexed sender, uint256 ddcId);

    /**
     * @dev Emitted when `ddcId` ddc is transferred from `from` to `to`.
     */
    event MetaTransfer(
        address indexed operator,
        address indexed from,
        address indexed to,
        uint256 ddcId
    );

    /**
     * @dev Emitted when `ddcId` ddc is transferred from `from` to `to`.
     */
    event MetaTransferBatch(
        address indexed operator,
        address indexed from,
        address indexed to,
        uint256[] ddcIds
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
     * @dev Represents the type of HashType.
     */
    enum HashType {
        mint,
        safeMint,
        mintBatch,
        safeMintBatch,
        transfer,
        safeTransfer,
        burn
    }

    /**
     * @dev  Initializes a name and symbol for the ddc.
     *
     * Requirements:
     * - sender must be the owner only.
     */
    function setNameAndSymbol(string memory name_, string memory symbol_)
        external;

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
     * @dev  Creates a new ddc for `to`. If ddcId is zero, Its ddc ID will be automatically
     *       assigned (and available on the emitted {IDDC721-Transfer} event)
     *
     * Requirements:
     * - sender must have call method permission.
     * - `to` must have the `DDC` attribute
     */
    function mint(address to, string memory ddcURI_) external;

    /**
     * @dev  Creates multipe new ddcs for `to`.The ddc ID will be automatically
     *       assigned (and available on the emitted {IDDC721-TransferBatch} event)
     *
     * Requirements:
     * - sender must have call method permission.
     * - `to` must have the `DDC` attribute
     * - `sender` and `to` must belong to the same platform.
     */
    function mintBatch(address to, string[] memory ddcURIs) external;

    /**
     * @dev  Creates a new ddc for `to`. If ddcId is zero, Its ddc ID will be automatically
     *       assigned (and available on the emitted {IDDC721-Transfer} event)
     *
     * Requirements:
     * - sender must have call method permission.
     * - `to` must have the `DDC` attribute
     * - If `to` refers to a smart contract, it must implement {IERC721Receiver-onDDC721Received}, which is called upon a safe transfer.
     */
    function safeMint(
        address to,
        string memory _ddcURI,
        bytes memory _data
    ) external;

    /**
     * @dev  Creates multipe new ddcs for `to`. The ddc ID will be automatically
     *       assigned (and available on the emitted {IDDC721-TransferBatch} event)
     *
     * Requirements:
     * - sender must have call method permission.
     * - `to` must have the `DDC` attribute
     * - `sender` and `to` must belong to the same platform.
     * - If `to` refers to a smart contract, it must implement {IERC721Receiver-onERC721Received}, which is called upon a safe transfer.
     */
    function safeMintBatch(
        address to,
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
    function setURI(uint256 ddcId, string memory ddcURI_) external;

    /**
     * @dev Gives permission to `to` to transfer `ddcId` ddc to another account.
     * Requirements:
     * - sender must have call method permission.
     * - `to` must have the `DDC` attribute
     * - sender must be the owner only.
     */
    function approve(address to, uint256 ddcId) external;

    /**
     * @dev Returns the account approved for `ddcId` ddc.
     *
     * Requirements:
     *
     */
    function getApproved(uint256 ddcId)
        external
        view
        returns (address operator);

    /**
     * @dev Approve or remove `operator` as an operator for the caller.
     * Operators can call {transferFrom} or {safeTransferFrom} for any ddc owned by the caller.
     *
     * Requirements:
     *
     * - The `operator` cannot be the caller.
     *
     * Emits an {ApprovalForAll} event.
     */
    function setApprovalForAll(address operator, bool approved) external;

    /**
     * @dev Returns if the `operator` is allowed to manage all of the assets of `owner`.
     *
     * See {setApprovalForAll}
     */
    function isApprovedForAll(address owner, address operator)
        external
        view
        returns (bool);

    /**
     * @notice getNonce for metatransfer
     *
     */
    function getNonce(address from) external view returns (uint256);

    /**
     * @dev Safely transfers `ddcId` ddc from `from` to `to`, checking first that contract recipients
     * are aware of the DDC721 protocol to prevent ddcs from being forever locked.
     *
     * Requirements:
     * - sender must have call method permission.
     * - `from `&`to` are must be a available `ddc` account.
     * - `ddc` must be available.
     * - sender & from & to are must be belong to the same platform;
     * - sender must be the owner or approved.
     *
     * transfer:
     * - `from` cannot be the zero address.
     * - `to` cannot be the zero address.
     * - `ddcId` ddc must exist and be owned by `from`.
     * - If the caller is not `from`, it must be have been allowed to move this ddc by either {approve} or {setApprovalForAll}.
     * - If `to` refers to a smart contract, it must implement {IDDC721Receiver-onDDC721Received}, which is called upon a safe transfer.
     *
     * Emits a {Transfer} event.
     */
    function safeTransferFrom(
        address from,
        address to,
        uint256 ddcId,
        bytes memory data
    ) external;

    /**
     * @dev Transfers `ddcId` ddc from `from` to `to`.
     *
     * Requirements:
     * - sender must be the owner or approved.
     *
     * Emits a {Transfer} event.
     */
    function transferFrom(
        address from,
        address to,
        uint256 ddcId
    ) external;

    /**
     * @dev  Freezes a ddc. If the ddc has freezed, it cann't do any actions.
     *
     * Requirements:
     * - sender's role is operator only.
     * - ``
     */
    function freeze(uint256 ddcId) external;

    /**
     * @dev  Unfreezes a ddc. If the ddc has unfreezed, it can do any actions.
     *
     * Requirements:
     * - sender's role is operator only.
     * - ``
     */
    function unFreeze(uint256 ddcId) external;

    /**
     * @dev Burns a ddc.
     *
     * Requirements:
     * - sender must own `ddcId` or be an approved operator.
     */
    function burn(uint256 ddcId) external;

    /**
     * @dev Returns the number of ddcs in ``owner``'s account.
     *
     * Requirements:
     *
     */
    function balanceOf(address owner) external view returns (uint256 balance);

    /**
     * @dev Returns the owner of the `ddcId` ddc.
     *
     * Requirements:
     */
    function ownerOf(uint256 ddcId) external view returns (address owner);

    /**
     * @dev Returns the ddc collection name.
     * Requirements:
     */
    function name() external view returns (string memory);

    /**
     * @dev Returns the ddc collection symbol.
     * Requirements:
     */
    function symbol() external view returns (string memory);

    /**
     * @dev Returns the Uniform Resource Identifier (URI) for `ddcId` ddc.
     * Requirements:
     */
    function ddcURI(uint256 ddcId) external view returns (string memory);

    /**
     * @dev Returns the last ddcID.
     *
     * Requirements:
     */
    function getLatestDDCId() external view returns (uint256);

    /**
     * @dev  Creates a new ddc for `to`. If ddcId is zero, Its ddc ID will be automatically
     *       assigned (and available on the emitted {IDDC721-Transfer} event)
     *
     * Requirements:
     * - sender must have call method permission.
     * - `to` must have the `DDC` attribute
     */
    function metaMint(
        address to,
        string memory ddcURI_,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) external;

    /**
     * @dev  Creates a new ddc for `to`. If ddcId is zero, Its ddc ID will be automatically
     *       assigned (and available on the emitted {IDDC721-metaSafeMint} event)
     *
     * Requirements:
     * - sender must have call method permission.
     * - `to` must have the `DDC` attribute
     */
    function metaSafeMint(
        address to,
        string memory ddcURI_,
        bytes memory data,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) external;

    /**
     * @dev  Creates multipe new ddcs for `to`. The ddc ID will be automatically
     *       assigned (and available on the emitted {IDDC721-MetaTransferBatch} event)
     *
     * Requirements:
     * - sender must have call method permission.
     * - `to` must have the `DDC` attribute
     * - `sender` and `to` must belong to the same platform.
     * - If `to` refers to a smart contract, it must implement {IERC721Receiver-onERC721Received}, which is called upon a safe transfer.
     */
    function metaMintBatch(
        address to,
        string[] memory ddcURIs,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) external;

    /**
     * @dev  Creates multipe new ddcs for `to`. The ddc ID will be automatically
     *       assigned (and available on the emitted {IDDC721-MetaTransferBatch} event)
     *
     * Requirements:
     * - sender must have call method permission.
     * - `to` must have the `DDC` attribute
     * - `sender` and `to` must belong to the same platform.
     * - If `to` refers to a smart contract, it must implement {IERC721Receiver-onERC721Received}, which is called upon a safe transfer.
     */
    function metaSafeMintBatch(
        address to,
        string[] memory ddcURIs,
        bytes memory data,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) external;

    /**
     * @dev Verify a signed transfer permit and execute if valid.
     *
     * Requirements:
     * - sender must have call method permission.
     * - `from `&`to` are must be a available `ddc` account.
     * - `ddc` must be available.
     * - sender & from & to are must be belong to the same platform;
     *
     * Emits a {MetaTransfer} event.
     */
    function metaTransferFrom(
        address from,
        address to,
        uint256 ddcId,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) external;

    /**
     * @dev Verify a signed transfer permit and execute if valid.
     *
     * Requirements:
     * - sender must have call method permission.
     * - `from `&`to` are must be a available `ddc` account.
     * - `ddc` must be available.
     * - sender & from & to are must be belong to the same platform;
     *
     * Emits a {MetaTransfer} event.
     */
    function metaSafeTransferFrom(
        address from,
        address to,
        uint256 ddcId,
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
        uint256 ddcId,
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
}