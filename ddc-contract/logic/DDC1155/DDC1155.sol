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
     * @dev See {IDDC1155-mint}.
     */
    function safeMint(
        address to,
        uint256 amount,
        string memory _ddcURI,
        bytes memory data
    ) public override {
        _requireSenderHasFuncPermission();
        _requireAvailableDDCAccount(to);
        _requireOnePlatform(_msgSender(), to);
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
        _requireSenderHasFuncPermission();
        _requireAvailableDDCAccount(to);
        _requireOnePlatform(_msgSender(), to);
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
     * @dev See {IDDC1155-safeTransferFrom}.
     */
    function safeTransferFrom(
        address from,
        address to,
        uint256 ddcId,
        uint256 amount,
        bytes memory data
    ) public override {
        _requireSenderHasFuncPermission();
        _requireAvailableDDCAccount(from);
        _requireAvailableDDCAccount(to);
        _requireOnePlatformOrCrossPlatformApproval(from, to);
        _requireApprovedOrOwner(from, _msgSender());

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
        _requireSenderHasFuncPermission();
        _requireAvailableDDCAccount(from);
        _requireAvailableDDCAccount(to);
        _requireOnePlatformOrCrossPlatformApproval(from, to);
        _requireApprovedOrOwner(from, _msgSender());

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
        emit TransferSingle(_msgSender(), owner, address(0), ddcId, 0);
    }

    /**
     * @dev See {IDDC1155-burnBatch}.
     */
    function burnBatch(address owner, uint256[] memory ddcIds) public override {
        _requireSenderHasFuncPermission();
        _requireApprovedOrOwner(owner, _msgSender());
        uint256[] memory amounts = new uint256[](ddcIds.length);
        for (uint256 i = 0; i < ddcIds.length; i++) {
            _burnAndPay(owner, ddcIds[i]);
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
        _ddcIds[ddcId] = true;
        _lastDDCId = ddcId;
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
        _requireAdequateBalance(from, ddcId, amount);
        uint256 fromBalance = _balances[ddcId][from];
        unchecked {
            _balances[ddcId][from] = fromBalance - amount;
        }
        _balances[ddcId][to] += amount;
        _pay(ddcId);
    }

    /**
     * @dev check conditions & burn & pay
     */
    function _burnAndPay(address owner, uint256 ddcId) private {
        require(owner != address(0), "DDC1155:zero address");
        _requireExists(ddcId);
        _requireAdequateBalance(owner, ddcId);
        _balances[ddcId][owner] = 0;
        delete _blacklist[ddcId];
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
     * @dev Requires approved or onwer.
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
}
