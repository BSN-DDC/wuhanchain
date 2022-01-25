// SPDX-License-Identifier:BSN DDC

pragma solidity ^0.8.0;

import "../../interface/DDC721/IDDC721.sol";
import "../../interface/DDC721/IERC721Receiver.sol";
import "../../interface/Charge/ICharge.sol";
import "../../interface/Authority/IAuthority.sol";
import "../../utils/AddressUpgradeable.sol";
import "../../utils/OwnableUpgradeable.sol";
import "../../proxy/utils/UUPSUpgradeable.sol";
import "../ERC165Upgradeable.sol";

/**
 * @title DDC721
 * @author Aaron zhang
 * @dev DDC721 contract
 */
contract DDC721 is
    OwnableUpgradeable,
    UUPSUpgradeable,
    ERC165Upgradeable,
    IDDC721
{
    using AddressUpgradeable for address;

    // ddc name
    string private _name;
    // ddc symbol
    string private _symbol;

    // Mapping from ddc ID to owner address
    mapping(uint256 => address) private _owners;

    // Mapping owner address to ddc count
    mapping(address => uint256) private _balances;

    // Mapping from ddc ID to approved address
    mapping(uint256 => address) private _ddcApprovals;

    // Mapping from owner to operator approvals
    mapping(address => mapping(address => bool)) private _operatorApprovals;

    // Mapping from ddc id to uri
    mapping(uint256 => string) private _ddcURIs;

    // Mapping from ddc ID to blacklist status
    mapping(uint256 => bool) _blacklist;

    // Records the last ddcid
    // Note: no need to change when ddc was burned.
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
     * @dev See {IERC165-supportsInterface}.
     */
    function supportsInterface(bytes4 interfaceId)
        public
        view
        virtual
        override(ERC165Upgradeable, IERC165Upgradeable)
        returns (bool)
    {
        return
            interfaceId == type(IDDC721).interfaceId ||
            super.supportsInterface(interfaceId);
    }

    /**
     * @dev See {IDDC721-setNameAndSymbol}.
     */
    function setNameAndSymbol(string memory name_, string memory symbol_)
        public
        override
        onlyOwner
    {
        _name = name_;
        _symbol = symbol_;
        emit SetNameAndSymbol(_name, _symbol);
    }

    /**
     * @dev See {IDDC721-setChargeProxyAddress}.
     */
    function setChargeProxyAddress(address chargeProxyAddress)
        public
        override
        onlyOwner
    {
        _requireContract(chargeProxyAddress);
        _chargeProxy = ICharge(chargeProxyAddress);
    }

    /**
     * @dev See {IDDC721-setAuthorityProxyAddress}.
     */
    function setAuthorityProxyAddress(address authorityProxyAddress)
        public
        override
        onlyOwner
    {
        _requireContract(authorityProxyAddress);
        _authorityProxy = IAuthority(authorityProxyAddress);
    }

    /**
     * @dev See {IDDC721-mint}.
     */
    function mint(address to, string memory ddcURI_) public override {
        _requireSenderHasFuncPermission();
        _requireAvailableDDCAccount(to);
        _requireOnePlatform(_msgSender(), to);
        uint256 ddcId = _lastDDCId + 1;
        _mintAndPay(to, ddcId, ddcURI_);
    }

    /**
     * @dev See {IDDC721-mint}.
     */
    function safeMint(
        address to,
        string memory _ddcURI,
        bytes memory _data
    ) public override {
        _requireSenderHasFuncPermission();
        _requireAvailableDDCAccount(to);
        _requireOnePlatform(_msgSender(), to);
        uint256 ddcId = _lastDDCId + 1;
        _mintAndPay(to, ddcId, _ddcURI);
        require(
            _checkOnERC721Received(address(0), to, ddcId, _data),
            "DDC721:transfer to non ERC721Receiver implementer"
        );
    }

    /**
     * @dev See {IDDC721-setURI}.
     */
    function setURI(uint256 ddcId, string memory ddcURI_) public override {
        _requireSenderHasFuncPermission();
        _requireAvailableDDC(ddcId);
        _requireApprovedOrOwner(_msgSender(), ddcId);
        require(bytes(ddcURI_).length != 0, "DDC721:Can not be empty");
        require(
            bytes(_ddcURIs[ddcId]).length == 0,
            "DDC721:already initialized"
        );

        _ddcURIs[ddcId] = ddcURI_;
        emit SetURI(ddcId, ddcURI_);
    }

    /**
     * @dev See {IDDC721-approve}.
     */
    function approve(address to, uint256 ddcId) public override {
        _requireSenderHasFuncPermission();
        _requireAvailableDDCAccount(to);
        _requireAvailableDDC(ddcId);
        _requireOnePlatform(_msgSender(), to);
        address owner = DDC721.ownerOf(ddcId);
        require(to != owner, "DDC721:approval to current owner");
        require(
            _msgSender() == owner ||
                DDC721.isApprovedForAll(owner, _msgSender()),
            "DDC721:approve caller is not owner nor approved for all"
        );
        _ddcApprovals[ddcId] = to;
        emit Approval(owner, to, ddcId);
    }

    /**
     * @dev See {IDDC721-getApproved}.
     */
    function getApproved(uint256 ddcId) public view override returns (address) {
        _requireExists(ddcId);
        return _ddcApprovals[ddcId];
    }

    /**
     * @dev See {IDDC721-setApprovalForAll}.
     */
    function setApprovalForAll(address operator, bool approved)
        public
        override
    {
        _requireSenderHasFuncPermission();
        _requireAvailableDDCAccount(operator);
        _requireOnePlatform(_msgSender(), operator);
        require(operator != _msgSender(), "DDC721:approve to caller");
        _operatorApprovals[_msgSender()][operator] = approved;
        emit ApprovalForAll(_msgSender(), operator, approved);
    }

    /**
     * @dev See {IDDC721-isApprovedForAll}.
     */
    function isApprovedForAll(address owner, address operator)
        public
        view
        override
        returns (bool)
    {
        require(
            owner != address(0) && operator != address(0),
            "DDC721:zero address"
        );
        return _operatorApprovals[owner][operator];
    }

    /**
     * @dev See {IDDC721-safeTransferFrom}.
     */
    function safeTransferFrom(
        address from,
        address to,
        uint256 ddcId,
        bytes memory data
    ) public override {
        _requireSenderHasFuncPermission();
        _requireAvailableDDCAccount(from);
        _requireAvailableDDCAccount(to);
        _requireAvailableDDC(ddcId);
        _requireOnePlatformOrCrossPlatformApproval(from, to);
        _requireApprovedOrOwner(_msgSender(), ddcId);
        _transfer(from, to, ddcId);
        require(
            _checkOnERC721Received(from, to, ddcId, data),
            "DDC721:transfer to non ERC721Receiver implementer"
        );
    }

    /**
     * @dev See {IDDC721-transferFrom}.
     */
    function transferFrom(
        address from,
        address to,
        uint256 ddcId
    ) public override {
        _requireSenderHasFuncPermission();
        _requireAvailableDDCAccount(from);
        _requireAvailableDDCAccount(to);
        _requireAvailableDDC(ddcId);
        _requireOnePlatformOrCrossPlatformApproval(from, to);
        _requireApprovedOrOwner(_msgSender(), ddcId);
        _transfer(from, to, ddcId);
    }

    /**
     * @dev See {IDDC721-freeze}.
     */
    function freeze(uint256 ddcId) public override {
        _requireSenderHasFuncPermission();
        _requireOperator();
        _requireAvailableDDC(ddcId);
        _blacklist[ddcId] = true;
        emit EnterBlacklist(_msgSender(), ddcId);
    }

    /**
     * @dev See {IDDC721-unFreeze}.
     */
    function unFreeze(uint256 ddcId) public override {
        _requireSenderHasFuncPermission();
        _requireOperator();
        _requireDisabledDDC(ddcId);
        _blacklist[ddcId] = false;
        emit ExitBlacklist(_msgSender(), ddcId);
    }

    /**
     * @dev See {IDDC721-burn}.
     */
    function burn(uint256 ddcId) public override {
        _requireSenderHasFuncPermission();
        _requireExists(ddcId);
        _requireApprovedOrOwner(_msgSender(), ddcId);
        address owner = DDC721.ownerOf(ddcId);
        // Clear approvals
        _clearApprovals(ddcId);
        // _ddc721Data.burn(owner, ddcId);
        _balances[owner] -= 1;
        delete _owners[ddcId];
        _pay(ddcId);
        emit Transfer(owner, address(0), ddcId);
    }

    /**
     * @dev See {IDDC721-balanceOf}.
     */
    function balanceOf(address owner) public view override returns (uint256) {
        require(
            owner != address(0),
            "DDC721:balance query for the zero address"
        );
        // return _ddc721Data.balanceOf(owner);
        return _balances[owner];
    }

    /**
     * @dev See {IDDC721-ownerOf}.
     */
    function ownerOf(uint256 ddcId) public view override returns (address) {
        address owner = _owners[ddcId];
        require(owner != address(0), "DDC721:owner query for nonexistent ddc");
        return owner;
    }

    /**
     * @dev See {IDDC721Metadata-name}.
     */
    function name() public view override returns (string memory) {
        // return _ddc721Data.name();
        return _name;
    }

    /**
     * @dev See {IDDC721Metadata-symbol}.
     */
    function symbol() public view override returns (string memory) {
        // return _ddc721Data.symbol();
        return _symbol;
    }

    /**
     * @dev See {IDDC721Metadata-ddcURI}.
     */
    function ddcURI(uint256 ddcId)
        public
        view
        override
        returns (string memory)
    {
        _requireExists(ddcId);
        // return _ddc721Data.ddcURI(ddcId);
        return _ddcURIs[ddcId];
    }

    /**
     * @dev check conditions of mint & mint & pay
     */
    function _mintAndPay(
        address to,
        uint256 ddcId,
        string memory ddcURI_
    ) private {
        require(_owners[ddcId] == address(0), "DDC721:already minted");
        _balances[to] += 1;
        _owners[ddcId] = to;
        _ddcURIs[ddcId] = ddcURI_;
        _lastDDCId = ddcId;
        _pay(ddcId);
        emit Transfer(address(0), to, ddcId);
    }

    /**
     * @dev Transfers `ddcId` from `from` to `to`.
     *  As opposed to {transferFrom}, this imposes no restrictions on msg.sender.
     *
     * Requirements:
     * - `to` cannot be the zero address.
     * - `ddcId` ddc must be owned by `from`.
     *
     * Emits a {Transfer} event.
     */
    function _transfer(
        address from,
        address to,
        uint256 ddcId
    ) private {
        require(
            DDC721.ownerOf(ddcId) == from,
            "DDC721:transfer of ddc that is not own"
        );
        // Clear approvals from the previous owner
        _clearApprovals(ddcId);
        _balances[from] -= 1;
        _balances[to] += 1;
        _owners[ddcId] = to;
        _pay(ddcId);
        emit Transfer(from, to, ddcId);
    }

    /**
     * @dev Clear approvals from the previous owner
     */
    function _clearApprovals(uint256 ddcId) private {
        _ddcApprovals[ddcId] = address(0);
    }

    /**
     * @dev pay business fee
     */
    function _pay(uint256 ddcId) private {
        _chargeProxy.pay(_msgSender(), msg.sig, ddcId);
    }

    /**
     * @dev Internal function to invoke {IERC721Receiver-onERC721Received} on a target address.
     * The call is not executed if the target address is not a contract.
     *
     * @param from address representing the previous owner of the given ddc ID
     * @param to target address that will receive the ddcs
     * @param ddcId uint256 ID of the ddc to be transferred
     * @param _data bytes optional data to send along with the call
     * @return bool whether the call correctly returned the expected magic value
     */
    function _checkOnERC721Received(
        address from,
        address to,
        uint256 ddcId,
        bytes memory _data
    ) private returns (bool) {
        if (to.isContract()) {
            try
                IERC721Receiver(to).onERC721Received(
                    _msgSender(),
                    from,
                    ddcId,
                    _data
                )
            returns (bytes4 retval) {
                return retval == IERC721Receiver.onERC721Received.selector;
            } catch (bytes memory reason) {
                if (reason.length == 0) {
                    revert("DDC721:transfer to non ERC721Receiver implementer");
                } else {
                    assembly {
                        revert(add(32, reason), mload(reason))
                    }
                }
            }
        } else {
            return true;
        }
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
        require(account != address(0), "DDC721:zero address");
        require(account.isContract(), "DDC721:not a contract");
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
            "DDC721:not a operator role or disabled"
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
            "DDC721:no permission"
        );
    }

    /**
     * @dev Requires a available account.
     *
     * Requirements:
     * - `sender` must be a available `ddc` account.
     */
    function _requireAvailableDDCAccount(address account) private view {
        require(account != address(0), "DDC721:zero address");
        require(
            _authorityProxy.accountAvailable(account),
            "DDC721:not a available account"
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
        require(!_blacklist[ddcId], "DDC721:disabled ddc");
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
        require(_blacklist[ddcId], "DDC721:non-disabled ddc");
    }

    /**
     * @dev Requires `from` and `to`  belong to the same platform.
     */
    function _requireOnePlatform(address from, address to) private view {
        require(_isOnePlatform(from, to), "DDC721:only on the same platform");
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
            "DDC721:Only one platform or cross-platform approval"
        );
    }

    /**
     * @dev Requires `ddcId` exists.
     */
    function _requireExists(uint256 ddcId) private view {
        require(_owners[ddcId] != address(0), "DDC721:nonexistent ddc");
    }

    /**
     * @dev Requires `spender` is allowed to manage `ddcId`.
     *
     * Requirements:
     * - `ddcId` must exists.
     * - `spender` is owner or approved.
     */
    function _requireApprovedOrOwner(address spender, uint256 ddcId)
        private
        view
    {
        address owner = DDC721.ownerOf(ddcId);
        require(
            spender == owner ||
                DDC721.getApproved(ddcId) == spender ||
                DDC721.isApprovedForAll(owner, spender),
            "DDC721:not owner nor approved"
        );
    }
}
