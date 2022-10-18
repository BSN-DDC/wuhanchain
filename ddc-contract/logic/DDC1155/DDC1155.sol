// SPDX-License-Identifier: BSN DDC

pragma solidity ^0.8.0;

import "../ERC165Upgradeable.sol";
import "../../interface/DDC1155/IDDC1155.sol";
import "../../interface/DDC1155/IERC1155Receiver.sol";
import "../../interface/Charge/ICharge.sol";
import "../../interface/Authority/IAuthority.sol";
import "../../utils/AddressUpgradeable.sol";
import "../../utils/OwnableUpgradeable.sol";
import "../../proxy/utils/UUPSUpgradeable.sol";

/**
 * @title DDC1155
 * @author AndyCao
 * @dev DDC1155 contract - Logical contract
 */
contract DDC1155 is
    UUPSUpgradeable,
    OwnableUpgradeable,
    ERC165Upgradeable,
    IDDC1155
{
    using AddressUpgradeable for address;

    // Mapping from ddc ID to account balances
    mapping(uint256 => mapping(address => uint256)) private _balances;

    // Mapping from account to operator approvals
    mapping(address => mapping(address => bool)) private _operatorApprovals;

    // Mapping from ddc id to uri
    mapping(uint256 => string) private _ddcURIs;

    // Mapping from ddc id list
    mapping(uint256 => bool) _ddcIds;

    // Mapping from ddc ID to blacklist status
    mapping(uint256 => bool) _blacklist;

    // last ddc id
    uint256 private _lastDDCId;

    // Charge proxy contract
    ICharge private _chargeProxy;

    // Authority proxy contract
    IAuthority private _authorityProxy;

    // EIP712
    mapping(address => uint256) private _nonces;
    bytes32 public DOMAIN_SEPARATOR;

    // Mapping from ddc ID to locklist status
    mapping(uint256 => bool) private _locklist;

    // Mapping hashtype to type hash
    mapping(HashType => bytes32) private typeHashs;

    // Mapping from ddc ID to type ddc owner
    mapping(uint256 => address[]) private _ddcOwners;

    // Mapping from ddc ID to type ddc owner index
    mapping(uint256 => mapping(address => uint256)) private _ddcOwnerIndexs;

    // Mapping from ddc ID to ddc amounts
    mapping(uint256 => uint256) _ddcAmts;

    constructor() initializer {}

    function initialize() public initializer {
        __ERC165_init();
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
     * @dev See {IDDC1155-setChargeProxyAddress}.
     */
    function setChargeProxyAddress(address chargeProxyAddress)
        external
        override
        onlyOwner
    {
        _requireContract(chargeProxyAddress);
        _chargeProxy = ICharge(chargeProxyAddress);
    }

    /**
     * @dev See {IDDC1155-setAuthorityProxyAddress}.
     */
    function setAuthorityProxyAddress(address authorityProxyAddress)
        external
        override
        onlyOwner
    {
        _requireContract(authorityProxyAddress);
        _authorityProxy = IAuthority(authorityProxyAddress);
    }

    /**
     * @dev See {IERC165-supportsInterface}.
     */
    function supportsInterface(bytes4 interfaceId)
        public
        view
        override(ERC165Upgradeable, IERC165Upgradeable)
        returns (bool)
    {
        return
            interfaceId == type(IDDC1155).interfaceId ||
            super.supportsInterface(interfaceId);
    }

    /**
     * @dev See {IDDC1155-setMetaTypeHashArgs}.
     */
    function setMetaTypeHashArgs(HashType hashType, bytes32 hashValue)
        public
        override
        onlyOwner
    {
        require(hashValue.length > 0, "DDC1155: `hashValue` cannot be empty");
        typeHashs[hashType] = hashValue;
    }

    /**
     * @dev See {IDDC1155-setMetaSeparatorArg}.
     */
    function setMetaSeparatorArg(bytes32 separator) public override onlyOwner {
        require(separator.length != 0, "DDC1155: `separator` cannot be empty");
        DOMAIN_SEPARATOR = separator;
    }

    /**
     * @dev See {IDDC1155-mint}.
     */
    function safeMint(
        address to,
        uint256 amount,
        string memory _ddcURI,
        bytes memory data
    ) public override {
        _requireMintCheck(to);
        // generated ddc id
        uint256 ddcId = _lastDDCId + 1;
        _mintAndPay(to, ddcId, amount, _ddcURI);
        emit TransferSingle(_msgSender(), address(0), to, ddcId, amount);
        // acceptance of safe transfer
        _doSafeTransferAcceptanceCheck(
            _msgSender(),
            address(0),
            to,
            ddcId,
            amount,
            data
        );
    }

    /**
     * @dev See {IDDC1155-mainBatch}.
     */
    function safeMintBatch(
        address to,
        uint256[] memory amounts,
        string[] memory ddcURIs,
        bytes memory data
    ) public override {
        _requireMintCheck(to);
        require(
            amounts.length != 0 && ddcURIs.length != 0,
            "DDC1155:amounts and ddcURIs length must be greater than 0"
        );
        require(amounts.length == ddcURIs.length, "DDC1155:length mismatch");
        uint256 ddcID = _lastDDCId;
        uint256[] memory ddcIds = new uint256[](amounts.length);
        for (uint256 i = 0; i < amounts.length; i++) {
            ddcID += 1;
            ddcIds[i] = ddcID;
            _mintAndPay(to, ddcIds[i], amounts[i], ddcURIs[i]);
        }
        emit TransferBatch(_msgSender(), address(0), to, ddcIds, amounts);
        _doSafeBatchTransferAcceptanceCheck(
            _msgSender(),
            address(0),
            to,
            ddcIds,
            amounts,
            data
        );
    }

    /**
     * @dev See {IDDC1155-setURI}.
     */
    function setURI(
        address owner,
        uint256 ddcId,
        string memory ddcURI_
    ) public override {
        _requireSenderHasFuncPermission();
        _requireAvailableDDC(ddcId);
        _requireUnLockDDC(ddcId);
        _requireApprovedOrOwner(owner, _msgSender());
        require(bytes(ddcURI_).length != 0, "DDC1155:Can not be empty");
        require(
            bytes(_ddcURIs[ddcId]).length == 0,
            "DDC1155:already initialized"
        );

        _ddcURIs[ddcId] = ddcURI_;
        emit SetURI(ddcId, ddcURI_);
    }

    /**
     * @dev See {IDDC1155-setApprovalForAll}.
     */
    function setApprovalForAll(address operator, bool approved)
        public
        override
    {
        _requireSenderHasFuncPermission();
        _requireAvailableDDCAccount(operator);
        _requireOnePlatform(_msgSender(), operator);
        require(_msgSender() != operator, "DDC1155:setting approval for self");
        _operatorApprovals[_msgSender()][operator] = approved;
        emit ApprovalForAll(_msgSender(), operator, approved);
    }

    /**
     * @dev See {IDDC1155-isApprovedForAll}.
     */
    function isApprovedForAll(address owner, address operator)
        public
        view
        override
        returns (bool)
    {
        require(
            owner != address(0) && operator != address(0),
            "DDC1155:zero address"
        );
        return _operatorApprovals[owner][operator];
    }

    /**
     * @dev See {IDDC1155-getNonce}.
     */
    function getNonce(address from) public view override returns (uint256) {
        return _nonces[from];
    }

    /**
     * @dev See {IDDC1155-safeTransferFrom}.
     */
    function safeTransferFrom(
        address from,
        address to,
        uint256 ddcId,
        uint256 amount,
        bytes memory data
    ) public override {
        _requireTransferFromCheck(from, to, _msgSender());
        _transferAndPay(from, to, ddcId, amount);
        emit TransferSingle(_msgSender(), from, to, ddcId, amount);
        _doSafeTransferAcceptanceCheck(
            _msgSender(),
            from,
            to,
            ddcId,
            amount,
            data
        );
    }

    /**
     * @dev See {IDDC1155-safeBatchTransferFrom}.
     */
    function safeBatchTransferFrom(
        address from,
        address to,
        uint256[] memory ddcIds,
        uint256[] memory amounts,
        bytes memory data
    ) public override {
        _requireTransferFromCheck(from, to, _msgSender());
        require(
            amounts.length != 0 && ddcIds.length != 0,
            "DDC1155:amounts and ddcIds length must be greater than 0"
        );
        require(ddcIds.length == amounts.length, "DDC1155:length mismatch");
        for (uint256 i = 0; i < ddcIds.length; ++i) {
            _transferAndPay(from, to, ddcIds[i], amounts[i]);
        }
        emit TransferBatch(_msgSender(), from, to, ddcIds, amounts);
        _doSafeBatchTransferAcceptanceCheck(
            _msgSender(),
            from,
            to,
            ddcIds,
            amounts,
            data
        );
    }

    /**
     * @dev See {IDDC1155-freeze}.
     */
    function freeze(uint256 ddcId) public override {
        _requireSenderHasFuncPermission();
        _requireOperator();
        _requireAvailableDDC(ddcId);

        _blacklist[ddcId] = true;
        emit EnterBlacklist(_msgSender(), ddcId);
    }

    /**
     * @dev See {IDDC1155-unFreeze}.
     */
    function unFreeze(uint256 ddcId) public override {
        _requireSenderHasFuncPermission();
        _requireOperator();
        _requireDisabledDDC(ddcId);

        _blacklist[ddcId] = false;
        emit ExitBlacklist(_msgSender(), ddcId);
    }

    /**
     * @dev See {IDDC1155-burn}.
     */
    function burn(address owner, uint256 ddcId) public override {
        _requireSenderHasFuncPermission();
        _requireApprovedOrOwner(owner, _msgSender());
        _burnAndPay(owner, ddcId);
        emit TransferSingle(
            _msgSender(),
            owner,
            address(0),
            ddcId,
            DDC1155.balanceOf(owner, ddcId)
        );
    }

    /**
     * @dev See {IDDC1155-burnBatch}.
     */
    function burnBatch(address owner, uint256[] memory ddcIds) public override {
        _requireSenderHasFuncPermission();
        _requireApprovedOrOwner(owner, _msgSender());
        require(
            ddcIds.length != 0,
            "DDC1155:ddcIds length must be greater than 0"
        );
        uint256[] memory amounts = new uint256[](ddcIds.length);
        for (uint256 i = 0; i < ddcIds.length; i++) {
            _burnAndPay(owner, ddcIds[i]);
            amounts[i] = DDC1155.balanceOf(owner, ddcIds[i]);
        }
        emit TransferBatch(_msgSender(), owner, address(0), ddcIds, amounts);
    }

    /**
     * @dev See {IDDC1155-balanceOf}.
     */
    function balanceOf(address owner, uint256 ddcId)
        public
        view
        override
        returns (uint256 balance)
    {
        require(owner != address(0), "DDC1155:zero address");
        return _balances[ddcId][owner];
    }

    /**
     * @dev See {IDDC1155-balanceOfBatch}.
     */
    function balanceOfBatch(address[] memory owners, uint256[] memory ddcIds)
        public
        view
        override
        returns (uint256[] memory)
    {
        require(owners.length == ddcIds.length, "DDC1155:length mismatch");
        uint256[] memory batchBalances = new uint256[](owners.length);
        for (uint256 i = 0; i < ddcIds.length; i++) {
            batchBalances[i] = DDC1155.balanceOf(owners[i], ddcIds[i]);
        }
        return batchBalances;
    }

    /**
     * @dev See {IDDC1155-ddcURI}.
     */
    function ddcURI(uint256 ddcId)
        public
        view
        override
        returns (string memory)
    {
        _requireExists(ddcId);
        return _ddcURIs[ddcId];
    }

    /**
     * @dev See {IDDC1155-getLatestDDCId}.
     */
    function getLatestDDCId() public view override returns (uint256) {
        return _lastDDCId;
    }

    /**
     * @dev See {IDDC1155-metaSafeMint}.
     */
    function metaSafeMint(
        address to,
        uint256 amount,
        string memory _ddcURI,
        bytes memory data,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) public override {
        bytes32 msgHash = keccak256(
            abi.encode(
                typeHashs[HashType.safeMint],
                amount,
                to,
                _ddcURI,
                nonce,
                deadline
            )
        );
        address signer = _getSignerAccount(sign, msgHash);
        require(to == signer, "DDC1155: invalid signature");
        // check permit signature
        _requireValidSignature(signer, nonce, deadline);
        _requireMintCheck(to);
        // generated ddc id
        uint256 ddcId = _lastDDCId + 1;
        _mintAndPay(to, ddcId, amount, _ddcURI);
        emit MetaTransferSingle(_msgSender(), address(0), to, ddcId, amount);
        // acceptance of safe transfer
        _doSafeTransferAcceptanceCheck(
            _msgSender(),
            address(0),
            to,
            ddcId,
            amount,
            data
        );
    }

    /**
     * @dev See {IDDC1155-metaSafeMintBatch}.
     */
    function metaSafeMintBatch(
        address to,
        uint256[] memory amounts,
        string[] memory ddcURIs,
        bytes memory data,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) public override {
        bytes32 msgHash = keccak256(
            abi.encode(
                typeHashs[HashType.safeMintBatch],
                to,
                amounts,
                ddcURIs,
                nonce,
                deadline
            )
        );
        address signer = _getSignerAccount(sign, msgHash);
        require(to == signer, "DDC1155: invalid signature");
        // check permit signature
        _requireValidSignature(signer, nonce, deadline);
        _requireMintCheck(to);
        require(
            amounts.length != 0 && ddcURIs.length != 0,
            "DDC1155:amounts and ddcURIs length must be greater than 0"
        );
        require(ddcURIs.length == amounts.length, "DDC1155:length mismatch");
        uint256 ddcID = _lastDDCId;
        uint256[] memory ddcIds = new uint256[](amounts.length);
        for (uint256 i = 0; i < amounts.length; i++) {
            ddcID += 1;
            ddcIds[i] = ddcID;
            _mintAndPay(to, ddcIds[i], amounts[i], ddcURIs[i]);
        }
        emit MetaTransferBatch(_msgSender(), address(0), to, ddcIds, amounts);
        _doSafeBatchTransferAcceptanceCheck(
            _msgSender(),
            address(0),
            to,
            ddcIds,
            amounts,
            data
        );
    }

    /**
     * @dev See {IDDC1155-metaSafeTransferFrom}.
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
    ) public override {
        bytes32 msgHash = keccak256(
            abi.encode(
                typeHashs[HashType.safeTransfer],
                from,
                to,
                ddcId,
                amount,
                nonce,
                deadline
            )
        );
        address signer = _getSignerAccount(sign, msgHash);
        _requireSignatureAccountIsApprovedOrOwner(from, signer);
        // check permit signature
        _requireValidSignature(signer, nonce, deadline);
        _requireTransferFromCheck(from, to, signer);
        _transferAndPay(from, to, ddcId, amount);
        emit MetaTransferSingle(_msgSender(), from, to, ddcId, amount);
        _doSafeTransferAcceptanceCheck(
            _msgSender(),
            from,
            to,
            ddcId,
            amount,
            data
        );
    }

    /**
     * @dev See {IDDC1155-metaSafeBatchTransferFrom}.
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
    ) public override {
        bytes32 msgHash = keccak256(
            abi.encode(
                typeHashs[HashType.safeTransferBatch],
                from,
                to,
                ddcIds,
                amounts,
                nonce,
                deadline
            )
        );
        address signer = _getSignerAccount(sign, msgHash);
        _requireSignatureAccountIsApprovedOrOwner(from, signer);
        // check permit signature
        _requireValidSignature(signer, nonce, deadline);
        _requireTransferFromCheck(from, to, signer);
        require(
            amounts.length != 0 && ddcIds.length != 0,
            "DDC1155:amounts and ddcIds length must be greater than 0"
        );
        require(ddcIds.length == amounts.length, "DDC1155:length mismatch");
        for (uint256 i = 0; i < ddcIds.length; ++i) {
            _transferAndPay(from, to, ddcIds[i], amounts[i]);
        }
        emit MetaTransferBatch(_msgSender(), from, to, ddcIds, amounts);
        _doSafeBatchTransferAcceptanceCheck(
            _msgSender(),
            from,
            to,
            ddcIds,
            amounts,
            data
        );
    }

    /**
     * @dev See {IDDC1155-metaBurn}.
     */
    function metaBurn(
        address owner,
        uint256 ddcId,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) public override {
        bytes32 msgHash = keccak256(
            abi.encode(typeHashs[HashType.burn], owner, ddcId, nonce, deadline)
        );
        address signer = _getSignerAccount(sign, msgHash);
        _requireSignatureAccountIsApprovedOrOwner(owner, signer);
        // check permit signature
        _requireValidSignature(signer, nonce, deadline);
        _requireSenderHasFuncPermission();
        _requireOnePlatform(_msgSender(), owner);
        _requireApprovedOrOwner(owner, signer);
        _burnAndPay(owner, ddcId);
        emit MetaTransferSingle(
            _msgSender(),
            owner,
            address(0),
            ddcId,
            DDC1155.balanceOf(owner, ddcId)
        );
    }

    /**
     * @dev See {IDDC1155-metaBurnBatch}.
     */
    function metaBurnBatch(
        address owner,
        uint256[] memory ddcIds,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) public override {
        bytes32 msgHash = keccak256(
            abi.encode(
                typeHashs[HashType.burnBatch],
                owner,
                ddcIds,
                nonce,
                deadline
            )
        );
        address signer = _getSignerAccount(sign, msgHash);
        _requireSignatureAccountIsApprovedOrOwner(owner, signer);
        // check permit signature
        _requireValidSignature(signer, nonce, deadline);
        _requireSenderHasFuncPermission();
        _requireOnePlatform(_msgSender(), owner);
        _requireApprovedOrOwner(owner, signer);
        require(
            ddcIds.length != 0,
            "DDC1155:ddcIds length must be greater than 0"
        );
        uint256[] memory amounts = new uint256[](ddcIds.length);
        for (uint256 i = 0; i < ddcIds.length; i++) {
            _burnAndPay(owner, ddcIds[i]);
            amounts[i] = DDC1155.balanceOf(owner, ddcIds[i]);
        }
        emit MetaTransferBatch(
            _msgSender(),
            owner,
            address(0),
            ddcIds,
            amounts
        );
    }

    /**
     * @dev See {IDDC1155-lock}.
     */
    function lock(uint256 ddcId) public override {
        _requireSenderHasFuncPermission();
        _requireAvailableDDC(ddcId);
        _requireUnLockDDC(ddcId);
        _requireCheckDDCOwners(ddcId);
        _locklist[ddcId] = true;
        emit Locklist(_msgSender(), ddcId);
    }

    /**
     * @dev See {IDDC1155-unlock}.
     */
    function unlock(uint256 ddcId) public override {
        _requireSenderHasFuncPermission();
        _requireAvailableDDC(ddcId);
        _requireLockDDC(ddcId);
        _locklist[ddcId] = false;
        emit UnLocklist(_msgSender(), ddcId);
    }

    /**
     * @dev See {IDDC1155-syncDDCOwners}.
     */
    function syncDDCOwners(uint256[] memory ddcIds, address[][] memory owners)
        public
        override
    {
        _requireSenderHasFuncPermission();
        _requireAvailableDDCAccount(_msgSender());
        _requireOperator();
        for (uint256 i = 0; i < ddcIds.length; i++) {
            _requireAvailableDDC(ddcIds[i]);
            _requireOwnerAddressEqual(ddcIds[i], owners[i]);
            for (uint256 j = 0; j < owners[i].length; j++) {
                _requireAdequateBalance(owners[i][j], ddcIds[i]);
                _addDDCOwner(owners[i][j], ddcIds[i]);
            }
        }
        emit SyncDDCOwners(_msgSender(), ddcIds, owners);
    }

    /**
     * @dev See {IDDC1155-ownerOf}.
     */
    function ownerOf(uint256 ddcId)
        public
        view
        override
        returns (address[] memory)
    {
        return _ddcOwners[ddcId];
    }

    /**
     * @dev check conditions & mint & pay
     */
    function _mintAndPay(
        address to,
        uint256 ddcId,
        uint256 amount,
        string memory _ddcURI
    ) private {
        _requireMintConditions(ddcId, amount);
        _balances[ddcId][to] += amount;
        _ddcURIs[ddcId] = _ddcURI;
        _ddcAmts[ddcId] = amount;
        _ddcIds[ddcId] = true;
        _lastDDCId = ddcId;
        _addDDCOwner(to, ddcId);
        _pay(ddcId);
    }

    /**
     * @dev check conditions of transfer & transfer & pay
     */
    function _transferAndPay(
        address from,
        address to,
        uint256 ddcId,
        uint256 amount
    ) private {
        _requireAvailableDDC(ddcId);
        _requireUnLockDDC(ddcId);
        _requireAdequateBalance(from, ddcId, amount);
        uint256 fromBalance = _balances[ddcId][from];
        unchecked {
            _balances[ddcId][from] = fromBalance - amount;
        }
        _balances[ddcId][to] += amount;
        _requireDDCOwner(from, to, ddcId);
        _pay(ddcId);
    }

    /**
     * @dev check conditions & burn & pay
     */
    function _burnAndPay(address owner, uint256 ddcId) private {
        require(owner != address(0), "DDC1155:zero address");
        _requireExists(ddcId);
        _requireAdequateBalance(owner, ddcId);
        uint256 amount = DDC1155.balanceOf(owner, ddcId);
        if (_ddcAmts[ddcId] > 0) {
            _ddcAmts[ddcId] -= amount;
        }
        _balances[ddcId][owner] = 0;
        delete _blacklist[ddcId];
        if (_ddcAmts[ddcId] == 0) {
            delete _ddcURIs[ddcId];
            _ddcIds[ddcId] = false;
        }
        _delDDCOwner(owner, ddcId);
        _pay(ddcId);
    }

    /**
     * @dev pay business fee
     */
    function _pay(uint256 ddcId) private {
        _chargeProxy.pay(_msgSender(), msg.sig, ddcId);
    }

    /**
     * @dev  check acceptance for SafeTransfer
     */
    function _doSafeTransferAcceptanceCheck(
        address operator,
        address from,
        address to,
        uint256 ddcId,
        uint256 amount,
        bytes memory data
    ) private {
        if (to.isContract()) {
            try
                IERC1155Receiver(to).onERC1155Received(
                    operator,
                    from,
                    ddcId,
                    amount,
                    data
                )
            returns (bytes4 response) {
                if (response != IERC1155Receiver.onERC1155Received.selector) {
                    revert("DDC1155:ERC1155Receiver rejected");
                }
            } catch Error(string memory reason) {
                revert(reason);
            } catch {
                revert("DDC1155:transfer to non ERC1155Receiver implementer");
            }
        }
    }

    /**
     * @dev  check acceptance for SafeBatchTransfer
     */
    function _doSafeBatchTransferAcceptanceCheck(
        address operator,
        address from,
        address to,
        uint256[] memory ddcIds,
        uint256[] memory amounts,
        bytes memory data
    ) private {
        if (to.isContract()) {
            try
                IERC1155Receiver(to).onERC1155BatchReceived(
                    operator,
                    from,
                    ddcIds,
                    amounts,
                    data
                )
            returns (bytes4 response) {
                if (
                    response != IERC1155Receiver.onERC1155BatchReceived.selector
                ) {
                    revert("DDC1155:ERC1155Receiver rejected");
                }
            } catch Error(string memory reason) {
                revert(reason);
            } catch {
                revert("DDC1155:transfer to non ERC1155Receiver implementer");
            }
        }
    }

    /**
     * @dev get a singleton array
     */
    function _asSingletonArray(uint256 element)
        private
        pure
        returns (uint256[] memory)
    {
        uint256[] memory array = new uint256[](1);
        array[0] = element;

        return array;
    }

    /**
     * @dev check whether the two belong to the same platform.
     */
    function _isOnePlatform(address from, address to)
        private
        view
        returns (bool)
    {
        return _authorityProxy.onePlatformCheck(from, to);
    }

    /**
     * @dev check whether the two meet cross-platform approval requirement.
     */
    function _isCrossPlatformApproval(address from, address to)
        private
        view
        returns (bool)
    {
        return _authorityProxy.crossPlatformCheck(from, to);
    }

    /**
     * @dev Requires contract address on chain.
     *
     * Requirements:
     * - `account` must not be zero address.
     * - `account` must be a contract.
     */
    function _requireContract(address account) private view {
        require(account != address(0), "DDC1155:zero address");
        require(account.isContract(), "DDC1155:not a contract");
    }

    /**
     * @dev Requires a operator role.
     *
     * Requirements:
     * - `sender` must be a available `ddc` account.
     * - `sender` must be a `Operator` role.
     */
    function _requireOperator() private view {
        require(
            _authorityProxy.checkAvailableAndRole(
                _msgSender(),
                IAuthority.Role.Operator
            ),
            "DDC1155:not a operator role or disabled"
        );
    }

    /**
     * @dev Requires check before mint DDC.
     *
     * Requirements:
     * - `to` must be a available `ddc` account.
     * - `sender` must have function permission.
     * - `sender` and `to`  belong to the same platform.
     */
    function _requireMintCheck(address to) private {
        _requireSenderHasFuncPermission();
        _requireAvailableDDCAccount(to);
        _requireOnePlatform(_msgSender(), to);
    }

    /**
     * @dev Requires check before DDC before transfer.
     *
     * Requirements:
     * - `from` must be a available `ddc` account.
     * - `to` must be a available `ddc` account.
     * - `sender` must have function permission.
     * - `from` and `to`  belong to the same platform.
     */
    function _requireTransferFromCheck(
        address from,
        address to,
        address sender
    ) private {
        _requireSenderHasFuncPermission();
        _requireAvailableDDCAccount(from);
        _requireAvailableDDCAccount(to);
        _requireOnePlatform(_msgSender(), from);
        _requireOnePlatformOrCrossPlatformApproval(from, to);
        _requireApprovedOrOwner(from, sender);
    }

    /**
     * @dev Requires function permissions.
     *
     * Requirements:
     * - `sender` must be a available `ddc` account.
     * - `sender` must have function permission.
     */
    function _requireSenderHasFuncPermission() private {
        require(
            _authorityProxy.hasFunctionPermission(
                _msgSender(),
                address(this),
                msg.sig
            ),
            "DDC1155:no permission"
        );
    }

    /**
     * @dev Requires a available account.
     *
     * Requirements:
     * - `sender` must be a available `ddc` account.
     */
    function _requireAvailableDDCAccount(address account) private view {
        require(account != address(0), "DDC1155:zero address");
        require(
            _authorityProxy.accountAvailable(account),
            "DDC1155:not a available account"
        );
    }

    /**
     * @dev Requires a available ddc.
     *
     * Requirements:
     * - ddc must be exist.
     * - ddc must not be in the blacklist.
     */
    function _requireAvailableDDC(uint256 ddcId) private view {
        _requireExists(ddcId);
        require(!_blacklist[ddcId], "DDC1155:disabled ddc");
    }

    /**
     * @dev Requires a disabled ddc.
     *
     * Requirements:
     * - ddc must be exist.
     * - ddc must be in the blacklist.
     */
    function _requireDisabledDDC(uint256 ddcId) private view {
        _requireExists(ddcId);
        require(_blacklist[ddcId], "DDC1155:non-disabled ddc");
    }

    /**
     * @dev Requires a unlock ddc.
     *
     * Requirements:
     * - ddc must be exist.
     * - ddc must not be in the locklist.
     */
    function _requireUnLockDDC(uint256 ddcId) private view {
        _requireExists(ddcId);
        require(!_locklist[ddcId], "DDC1155:locked ddc");
    }

    /**
     * @dev Requires a lcok ddc.
     *
     * Requirements:
     * - ddc must be exist.
     * - ddc must be in the locklist.
     */
    function _requireLockDDC(uint256 ddcId) private view {
        _requireExists(ddcId);
        require(_locklist[ddcId], "DDC1155:non-locked ddc");
    }

    /**
     * @dev Requires `from` and `to`  belong to the same platform.
     */
    function _requireOnePlatform(address from, address to) private view {
        require(_isOnePlatform(from, to), "DDC1155:only on the same platform");
    }

    /**
     * @dev Requires `from` and `to` must belong to the same platform or meet cross-platform approval requirement.
     */
    function _requireOnePlatformOrCrossPlatformApproval(
        address from,
        address to
    ) private view {
        require(
            _isOnePlatform(from, to) || _isCrossPlatformApproval(from, to),
            "DDC1155:Only one platform or cross-platform approval"
        );
    }

    /**
     * @dev Requires `ddcId` does not exist, amount greater than zero.
     */
    function _requireMintConditions(uint256 ddcId, uint256 amount)
        private
        view
    {
        require(amount > 0, "DDC1155:invalid amount");
        require(!_ddcIds[ddcId], "DDC1155:already minted");
    }

    /**
     * @dev Requires `ddcId` exists.
     */
    function _requireExists(uint256 ddcId) private view {
        require(_ddcIds[ddcId], "DDC1155:nonexistent ddc");
    }

    /**
     * @dev Requires adequate balance.
     */
    function _requireAdequateBalance(address owner, uint256 ddcId)
        private
        view
    {
        require(
            DDC1155.balanceOf(owner, ddcId) > 0,
            "DDC1155:insufficient balance"
        );
    }

    /**
     * @dev Requires adequate balance.
     */
    function _requireAdequateBalance(
        address owner,
        uint256 ddcId,
        uint256 amount
    ) private view {
        require(amount > 0, "DDC1155:invalid amount");
        require(
            DDC1155.balanceOf(owner, ddcId) >= amount,
            "DDC1155:insufficient balance"
        );
    }

    /**
     * @dev Operate on the number of DDC owners.
     */
    function _requireCheckDDCOwners(uint256 ddcId) private {
        uint256 ownerAmount = _getDDCOwnersLength(ddcId);
        require(ownerAmount == 1, "DDC can only have one owner");
    }

    /**
     * @dev Get the number of ddc Owners.
     */
    function _getDDCOwnersLength(uint256 ddcId) private view returns (uint256) {
        return _ddcOwners[ddcId].length;
    }

    /**
     * @dev Operate on the number of DDC owners.
     */
    function _requireDDCOwner(
        address from,
        address to,
        uint256 ddcId
    ) private {
        uint256 fromAmount = DDC1155.balanceOf(from, ddcId);
        if (fromAmount == 0) {
            _delDDCOwner(from, ddcId);
            _addDDCOwner(to, ddcId);
        } else if (from != to) {
            _addDDCOwner(to, ddcId);
        }
    }

    /**
     * @dev Add owner.
     */
    function _addDDCOwner(address owner, uint256 ddcId) private {
        // get the key value index value
        uint256 index = _ddcOwnerIndexs[ddcId][owner];
        if (index == 0) {
            // add to the proposal storage list
            _ddcOwners[ddcId].push(owner);
            // record the index value corresponding to the key value
            _ddcOwnerIndexs[ddcId][owner] = _ddcOwners[ddcId].length;
        } else {
            //Update proposal information
            _ddcOwners[ddcId][index - 1] = owner;
        }
    }

    /**
     * @dev Delete owner.
     */
    function _delDDCOwner(address owner, uint256 ddcId) private {
        uint256 index = _getOwnerIndex(owner, ddcId);
        if (index > 0) {
            _ddcOwners[ddcId][index - 1] = _ddcOwners[ddcId][
                _ddcOwners[ddcId].length - 1
            ];
            _ddcOwnerIndexs[ddcId][
                _ddcOwners[ddcId][_ddcOwners[ddcId].length - 1]
            ] = _ddcOwnerIndexs[ddcId][owner];
            delete _ddcOwnerIndexs[ddcId][owner];
            _ddcOwners[ddcId].pop();
        }
    }

    /**
     * @dev Get owner index.
     */
    function _getOwnerIndex(address owner, uint256 ddcId)
        private
        view
        returns (uint256)
    {
        return _ddcOwnerIndexs[ddcId][owner];
    }

    /**
     * @dev Requires approved or owner.
     *
     * Requirements:
     * - `spender` is owner or approved.
     */
    function _requireApprovedOrOwner(address owner, address spender)
        private
        view
    {
        require(
            DDC1155.isApprovedForAll(owner, spender) || spender == owner,
            "DDC1155:not owner nor approved"
        );
    }

    /**
     * @dev Check if the signer account is approved or owner.
     *
     * Requirements:
     * - `spender` is owner or approved.
     */
    function _requireSignatureAccountIsApprovedOrOwner(
        address owner,
        address spender
    ) private view {
        require(
            DDC1155.isApprovedForAll(owner, spender) || spender == owner,
            "DDC1155: invalid signature"
        );
    }

    /**
     * @dev Get signer account.
     */
    function _getSignerAccount(bytes memory sig, bytes32 msgHash)
        private
        view
        returns (address)
    {
        bytes32 digest = keccak256(
            abi.encodePacked("\x19\x01", DOMAIN_SEPARATOR, msgHash)
        );
        address signer = _recoverSigner(digest, sig);
        return signer;
    }

    /**
     * @dev Requires a permit signature.
     */
    function _requireValidSignature(
        address signer,
        uint256 nonce,
        uint256 deadline
    ) private {
        _requireAvailableDDCAccount(signer);
        _requireOnePlatform(_msgSender(), signer);
        require(
            deadline == 0 || block.timestamp <= deadline,
            "DDC1155: expired signature"
        );
        _nonces[signer]++;
        require(nonce == _nonces[signer], "DDC1155:invalid nonce");
    }

    /**
     * @dev recovers an address of the signer.that the owner address exists
     */
    function _recoverSigner(bytes32 message, bytes memory sig)
        private
        pure
        returns (address)
    {
        require(sig.length == 65, "DDC1155:invalid signature length");
        uint8 v;
        bytes32 r;
        bytes32 s;
        assembly {
            // first 32 bytes, after the length prefix.
            r := mload(add(sig, 32))
            // second 32 bytes.
            s := mload(add(sig, 64))
            // final byte (first byte of the next 32 bytes).
            v := byte(0, mload(add(sig, 96)))
        }
        return ecrecover(message, v, r, s);
    }

    /**
     * @dev Check if the owner address exists.
     */
    function _requireOwnerAddressEqual(uint256 ddcId, address[] memory owner)
        private
        view
    {
        address[] memory ownerIs = DDC1155.ownerOf(ddcId);
        for (uint256 i = 0; i < ownerIs.length; i++)
            for (uint256 j = 0; j < owner.length; j++) {
                require(
                    ownerIs[i] == owner[j],
                    "DDC1155:The owner already exists"
                );
            }
    }
}
