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

    // EIP712
    mapping(address => uint256) private _nonces;
    bytes32 public DOMAIN_SEPARATOR;

    // Mapping from ddc ID to locklist status
    mapping(uint256 => mapping(address => bool)) private _locklist;

    // Mapping hashtype to type hash
    mapping(HashType => bytes32) private typeHashs;

    bool private _paused;

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
    function _authorizeUpgrade(
        address newImplementation
    ) internal override onlyOwner {}

    /**
     * @dev See {IERC165-supportsInterface}.
     */
    function supportsInterface(
        bytes4 interfaceId
    )
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
    function setNameAndSymbol(
        string memory name_,
        string memory symbol_
    ) public override onlyOwner {
        _name = name_;
        _symbol = symbol_;
        emit SetNameAndSymbol(_name, _symbol);
    }

    /**
     * @dev See {IDDC721-setChargeProxyAddress}.
     */
    function setChargeProxyAddress(
        address chargeProxyAddress
    ) public override onlyOwner {
        _requireContract(chargeProxyAddress);
        _chargeProxy = ICharge(chargeProxyAddress);
    }

    /**
     * @dev See {IDDC721-setAuthorityProxyAddress}.
     */
    function setAuthorityProxyAddress(
        address authorityProxyAddress
    ) public override onlyOwner {
        _requireContract(authorityProxyAddress);
        _authorityProxy = IAuthority(authorityProxyAddress);
    }

    /**
     * @dev See {IDDC721-setMetaTypeHashArgs}.
     */
    function setMetaTypeHashArgs(
        HashType hashType,
        bytes32 hashValue
    ) public override onlyOwner {
        require(hashValue.length > 0, "DDC721: `hashValue` cannot be empty");
        typeHashs[hashType] = hashValue;
    }

    /**
     * @dev See {IDDC721-setMetaSeparatorArg}.
     */
    function setMetaSeparatorArg(bytes32 separator) public override onlyOwner {
        require(separator.length != 0, "DDC721: `separator` cannot be empty");
        DOMAIN_SEPARATOR = separator;
    }

    /**
     * @dev See {IDDC721-mint}.
     */
    function mint(address to, string memory ddcURI_) public override {
        _whenNotPaused(_msgSender());
        uint256 ddcId = _mint(to, ddcURI_);
        emit Transfer(address(0), to, ddcId);
    }

    /**
     * @dev See {IDDC721-mintBatch}.
     */
    function mintBatch(address to, string[] memory ddcURIs) public override {
        _whenNotPaused(_msgSender());
        uint256[] memory ddcIds = _mintBatch(to, ddcURIs);
        emit TransferBatch(_msgSender(), address(0), to, ddcIds);
    }

    /**
     * @dev See {IDDC721-mint}.
     */
    function safeMint(
        address to,
        string memory _ddcURI,
        bytes memory _data
    ) public override {
        _whenNotPaused(_msgSender());
        uint256 ddcId = _mint(to, _ddcURI);
        emit Transfer(address(0), to, ddcId);
        require(
            _checkOnERC721Received(address(0), to, ddcId, _data),
            "DDC721:transfer to non ERC721Receiver implementer"
        );
    }

    /**
     * @dev See {IDDC721-crossSafeMint}.
     */
    function crossSafeMint(
        address to,
        string memory _ddcURI,
        bytes memory _data
    ) public override {
        uint256 ddcId = _mint(to, _ddcURI);
        emit Transfer(address(0), to, ddcId);
        require(
            _checkOnERC721Received(address(0), to, ddcId, _data),
            "DDC721:transfer to non ERC721Receiver implementer"
        );
    }

    /**
     * @dev See {IDDC721-safeMintBatch}.
     */
    function safeMintBatch(
        address to,
        string[] memory ddcURIs,
        bytes memory data
    ) public override {
        _whenNotPaused(_msgSender());
        uint256[] memory ddcIds = _mintBatch(to, ddcURIs);
        emit TransferBatch(_msgSender(), address(0), to, ddcIds);
        require(
            _checkOnERC721BatchReceived(address(0), to, ddcIds, data),
            "DDC721:transfer to non ERC721Receiver implementer"
        );
    }

    /**
     * @dev See {IDDC721-setURI}.
     */
    function setURI(uint256 ddcId, string memory ddcURI_) public override {
        _requireSenderHasFuncPermission();
        _requireAvailableDDC(ddcId);
        _requireUnLockDDC(ddcId);
        _requireApprovedOrOwner(_msgSender(), ddcId);
        _whenNotPaused(_msgSender());
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
        _requireUnLockDDC(ddcId);
        _requireOnePlatform(_msgSender(), to);
        _whenNotPaused(_msgSender());
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
    function setApprovalForAll(
        address operator,
        bool approved
    ) public override {
        _requireSenderHasFuncPermission();
        _requireAvailableDDCAccount(operator);
        _requireOnePlatform(_msgSender(), operator);
        _whenNotPaused(_msgSender());
        require(operator != _msgSender(), "DDC721:approve to caller");
        _operatorApprovals[_msgSender()][operator] = approved;
        emit ApprovalForAll(_msgSender(), operator, approved);
    }

    /**
     * @dev See {IDDC721-isApprovedForAll}.
     */
    function isApprovedForAll(
        address owner,
        address operator
    ) public view override returns (bool) {
        require(
            owner != address(0) && operator != address(0),
            "DDC721:zero address"
        );
        return _operatorApprovals[owner][operator];
    }

    /**
     * @dev See {IDDC721-getNonce}.
     */
    function getNonce(address from) public view override returns (uint256) {
        return _nonces[from];
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
        _whenNotPaused(_msgSender());
        _requireTransferFromCheck(from, to, ddcId, _msgSender());
        _transfer(from, to, ddcId);
        emit Transfer(from, to, ddcId);
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
        _whenNotPaused(_msgSender());
        _requireTransferFromCheck(from, to, ddcId, _msgSender());
        _transfer(from, to, ddcId);
        emit Transfer(from, to, ddcId);
    }

    /**
     * @dev See {IDDC721-freeze}.
     */
    function freeze(uint256 ddcId) public override {
        _whenNotPaused(_msgSender());
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
        _whenNotPaused(_msgSender());
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
        _whenNotPaused(_msgSender());
        _requireBurnCheck(ddcId, _msgSender());
        address owner = DDC721.ownerOf(ddcId);
        _burn(owner, ddcId);
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
        return _name;
    }

    /**
     * @dev See {IDDC721Metadata-symbol}.
     */
    function symbol() public view override returns (string memory) {
        return _symbol;
    }

    /**
     * @dev See {IDDC721Metadata-ddcURI}.
     */
    function ddcURI(
        uint256 ddcId
    ) public view override returns (string memory) {
        _requireExists(ddcId);
        return _ddcURIs[ddcId];
    }

    /**
     * @dev See {IDDC721-metaMint}.
     */
    function metaMint(
        address to,
        string memory ddcURI_,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) external override {
        bytes32 msgHash = keccak256(
            abi.encode(typeHashs[HashType.mint], to, ddcURI_, nonce, deadline)
        );
        address signer = _getSignerAccount(sign, msgHash);

        require(to == signer, "DDC721: invalid signature");

        // check permit signature
        _requireValidSignature(signer, nonce, deadline);
        _whenNotPaused(signer);

        uint256 ddcId = _mint(to, ddcURI_);

        emit MetaTransfer(_msgSender(), address(0), to, ddcId);
    }

    /**
     * @dev See {IDDC721-metaSafeMint}.
     */
    function metaSafeMint(
        address to,
        string memory ddcURI_,
        bytes memory data_,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) external override {
        bytes32 msgHash = keccak256(
            abi.encode(
                typeHashs[HashType.safeMint],
                to,
                ddcURI_,
                nonce,
                deadline
            )
        );
        address signer = _getSignerAccount(sign, msgHash);

        require(to == signer, "DDC721: invalid signature");

        // check permit signature
        _requireValidSignature(signer, nonce, deadline);
        _whenNotPaused(signer);
        uint256 ddcId = _mint(to, ddcURI_);
        require(
            _checkOnERC721Received(address(0), to, ddcId, data_),
            "DDC721:transfer to non ERC721Receiver implementer"
        );
        emit MetaTransfer(_msgSender(), address(0), to, ddcId);
    }

    /**
     * @dev See {IDDC721-metaMintBatch}.
     */
    function metaMintBatch(
        address to,
        string[] memory ddcURIs,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) external override {
        bytes32 msgHash = keccak256(
            abi.encode(
                typeHashs[HashType.mintBatch],
                to,
                ddcURIs,
                nonce,
                deadline
            )
        );
        address signer = _getSignerAccount(sign, msgHash);
        require(to == signer, "DDC721: invalid signature");
        // check permit signature
        _requireValidSignature(signer, nonce, deadline);
        _whenNotPaused(signer);
        uint256[] memory ddcIds = _mintBatch(to, ddcURIs);
        emit MetaTransferBatch(_msgSender(), address(0), to, ddcIds);
    }

    /**
     * @dev See {IDDC721-metaSafeMintBatch}.
     */
    function metaSafeMintBatch(
        address to,
        string[] memory ddcURIs,
        bytes memory data,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) external override {
        bytes32 msgHash = keccak256(
            abi.encode(
                typeHashs[HashType.safeMintBatch],
                to,
                ddcURIs,
                nonce,
                deadline
            )
        );
        address signer = _getSignerAccount(sign, msgHash);
        require(to == signer, "DDC721: invalid signature");
        // check permit signature
        _requireValidSignature(signer, nonce, deadline);
        _whenNotPaused(signer);
        uint256[] memory ddcIds = _mintBatch(to, ddcURIs);
        require(
            _checkOnERC721BatchReceived(address(0), to, ddcIds, data),
            "DDC721:transfer to non ERC721Receiver implementer"
        );
        emit MetaTransferBatch(_msgSender(), address(0), to, ddcIds);
    }

    /**
     * @dev See {IDDC721-metaTransferFrom}.
     */
    function metaTransferFrom(
        address from,
        address to,
        uint256 ddcId,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) public override {
        bytes32 msgHash = keccak256(
            abi.encode(
                typeHashs[HashType.transfer],
                from,
                to,
                ddcId,
                nonce,
                deadline
            )
        );
        address signer = _getSignerAccount(sign, msgHash);
        _whenNotPaused(signer);
        _requireSignatureAccountIsApprovedOrOwner(signer, ddcId);
        // check permit signature
        _requireValidSignature(signer, nonce, deadline);
        _requireTransferFromCheck(from, to, ddcId, signer);
        _transfer(from, to, ddcId);
        emit MetaTransfer(_msgSender(), from, to, ddcId);
    }

    /**
     * @dev See {IDDC721-metaSafeTransferFrom}.
     */
    function metaSafeTransferFrom(
        address from,
        address to,
        uint256 ddcId,
        bytes memory data,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) external override {
        bytes32 msgHash = keccak256(
            abi.encode(
                typeHashs[HashType.safeTransfer],
                from,
                to,
                ddcId,
                nonce,
                deadline
            )
        );
        address signer = _getSignerAccount(sign, msgHash);
        _whenNotPaused(signer);
        _requireSignatureAccountIsApprovedOrOwner(signer, ddcId);
        // check permit signature
        _requireValidSignature(signer, nonce, deadline);
        _requireTransferFromCheck(from, to, ddcId, signer);
        _transfer(from, to, ddcId);
        require(
            _checkOnERC721Received(from, to, ddcId, data),
            "DDC721:transfer to non ERC721Receiver implementer"
        );
        emit MetaTransfer(_msgSender(), from, to, ddcId);
    }

    /**
     * @dev See {IDDC721-metaBurn}.
     */
    function metaBurn(
        uint256 ddcId,
        uint256 nonce,
        uint256 deadline,
        bytes memory sign
    ) external override {
        bytes32 msgHash = keccak256(
            abi.encode(typeHashs[HashType.burn], ddcId, nonce, deadline)
        );
        address signer = _getSignerAccount(sign, msgHash);
        _requireSignatureAccountIsApprovedOrOwner(signer, ddcId);
        // check permit signature
        _requireValidSignature(signer, nonce, deadline);
        require(
            !_blacklist[ddcId],
            "DDC721:have ddcid frozen and cannot be deleted"
        );
        _requireBurnCheck(ddcId, signer);
        _whenNotPaused(signer);
        address owner = DDC721.ownerOf(ddcId);
        _burn(owner, ddcId);
        emit MetaTransfer(_msgSender(), owner, address(0), ddcId);
    }

    /**
     * @dev See {IDDC721-getLatestDDCId}.
     */
    function getLatestDDCId() public view override returns (uint256) {
        return _lastDDCId;
    }

    /**
     * @dev See {IDDC721Metadata-lock}.
     */
    function lock(uint256 ddcId) public override {
        _whenNotPaused(_msgSender());
        _requireSenderHasFuncPermission();
        _requireAvailableDDC(ddcId);
        _requireUnLockDDC(ddcId);
        address owner = ownerOf(ddcId);
        _locklist[ddcId][owner] = true;
        emit Locklist(_msgSender(), ddcId);
    }

    /**
     * @dev See {IDDC721Metadata-unlock}.
     */
    function unlock(uint256 ddcId) public override {
        _whenNotPaused(_msgSender());
        _requireSenderHasFuncPermission();
        _requireAvailableDDC(ddcId);
        _requireLockDDC(ddcId);
        address owner = ownerOf(ddcId);
        _locklist[ddcId][owner] = false;
        emit UnLocklist(_msgSender(), ddcId);
    }

    /**
     * @dev Returns true if the contract is paused, and false otherwise.
     */
    function paused() public view returns (bool) {
        return _paused;
    }

    /**
     * @dev Triggers stopped state.
     *
     * Requirements:
     *
     * - The contract must not be paused.
     */
    function pause() public onlyOwner {
        _paused = true;
        emit Paused(_msgSender());
    }

    /**
     * @dev Returns to normal state.
     *
     * Requirements:
     *
     * - The contract must be paused.
     */
    function unpause() public onlyOwner {
        _paused = false;
        emit Unpaused(_msgSender());
    }

    /**
     * @dev Creates a ddc for `to`.
     */
    function _mint(
        address to,
        string memory _ddcURI
    ) private returns (uint256 ddcId) {
        _requireSenderHasFuncPermission();
        _requireAvailableDDCAccount(to);
        //_requireOnePlatform(_msgSender(), to);
        ddcId = _lastDDCId + 1;
        _mintAndPay(to, ddcId, _ddcURI);
    }

    /**
     * @dev Creates multiple ddcs for `to`.
     */
    function _mintBatch(
        address to,
        string[] memory ddcURIs
    ) private returns (uint256[] memory ddcIds) {
        _requireSenderHasFuncPermission();
        _requireAvailableDDCAccount(to);
        //_requireOnePlatform(_msgSender(), to);
        ddcIds = new uint256[](ddcURIs.length);
        for (uint256 i = 0; i < ddcURIs.length; i++) {
            uint256 ddcId = _lastDDCId + 1;
            ddcIds[i] = ddcId;
            _mintAndPay(to, ddcId, ddcURIs[i]);
        }
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
    }

    /**
     * @dev Transfers `ddcId` from `from` to `to`.
     *
     * Requirements:
     * - `to` cannot be the zero address.
     * - `ddcId` ddc must be owned by `from`.
     *
     */
    function _transfer(address from, address to, uint256 ddcId) private {
        require(
            DDC721.ownerOf(ddcId) == from,
            "DDC721:transfer of ddc that is not own"
        );
        delete _ddcApprovals[ddcId];
        _balances[from] -= 1;
        _balances[to] += 1;
        _owners[ddcId] = to;
        _pay(ddcId);
    }

    /**
     * @dev Burn DDC.
     */
    function _burn(address owner, uint256 ddcId) private {
        delete _ddcApprovals[ddcId];
        delete _ddcURIs[ddcId];
        _balances[owner] -= 1;
        delete _owners[ddcId];
        _pay(ddcId);
    }

    /**
     * @dev pay business fee
     */
    function _pay(uint256 ddcId) private {
        _chargeProxy.pay(_msgSender(), msg.sig, ddcId);
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
        uint256 ddcId,
        address sender
    ) private {
        _requireSenderHasFuncPermission();
        _requireAvailableDDCAccount(from);
        _requireAvailableDDCAccount(to);
        _requireAvailableDDC(ddcId);
        _requireUnLockDDC(ddcId);
        //_requireOnePlatform(_msgSender(), from);
        //_requireOnePlatformOrCrossPlatformApproval(from, to);
        _requireApprovedOrOwner(sender, ddcId);
    }

    /**
     * @dev Requires check before DDC before burn.
     *
     * Requirements:
     * - `sender` must have function permission.
     * - `ddcId` must exists.
     */
    function _requireBurnCheck(uint256 ddcId, address sender) private {
        if (_blacklist[ddcId] == true) {
            require(
                _authorityProxy.checkAvailableAndRole(
                    sender,
                    IAuthority.Role.Operator
                ),
                "DDC721:not a operator role or disabled"
            );
        } else {
            _requireApprovedOrOwner(sender, ddcId);
        }
        _requireSenderHasFuncPermission();
        _requireExists(ddcId);
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
     * @dev Internal function to invoke {IERC721Receiver-onERC721BatchReceived} on a target address.
     * The call is not executed if the target address is not a contract.
     *
     * @param from address representing the previous owner of the given ddc ID
     * @param to target address that will receive the ddcs
     * @param ddcIds uint256 array IDs of each ddc to be transferred
     * @param _data bytes optional data to send along with the call
     * @return bool whether the call correctly returned the expected magic value
     */
    function _checkOnERC721BatchReceived(
        address from,
        address to,
        uint256[] memory ddcIds,
        bytes memory _data
    ) private returns (bool) {
        if (to.isContract()) {
            try
                IERC721Receiver(to).onERC721BatchReceived(
                    _msgSender(),
                    from,
                    ddcIds,
                    _data
                )
            returns (bytes4 retval) {
                return retval == IERC721Receiver.onERC721BatchReceived.selector;
            } catch (bytes memory reason) {
                if (reason.length == 0) {
                    revert(
                        "DDC721:transfer to non onERC721BatchReceived implementer"
                    );
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
    function _isOnePlatform(
        address from,
        address to
    ) private view returns (bool) {
        return _authorityProxy.onePlatformCheck(from, to);
    }

    /**
     * @dev check whether the two meet cross-platform approval requirement.
     */
    function _isCrossPlatformApproval(
        address from,
        address to
    ) private view returns (bool) {
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
     * @dev Requires a unlock ddc.
     *
     * Requirements:
     * - ddc must be exist.
     * - ddc must not be in the locklist.
     */
    function _requireUnLockDDC(uint256 ddcId) private view {
        _requireExists(ddcId);
        address owner = ownerOf(ddcId);
        require(!_locklist[ddcId][owner], "DDC721:locked ddc");
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
        address owner = ownerOf(ddcId);
        require(_locklist[ddcId][owner], "DDC721:non-locked ddc");
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
    function _requireApprovedOrOwner(
        address spender,
        uint256 ddcId
    ) private view {
        address owner = DDC721.ownerOf(ddcId);
        require(
            spender == owner ||
                DDC721.getApproved(ddcId) == spender ||
                DDC721.isApprovedForAll(owner, spender),
            "DDC721:not owner nor approved"
        );
    }

    /**
     * @dev Check if the signer account is approved or owner.
     *
     * Requirements:
     * - `spender` is owner or approved.
     */
    function _requireSignatureAccountIsApprovedOrOwner(
        address spender,
        uint256 ddcId
    ) private view {
        address owner = DDC721.ownerOf(ddcId);
        require(
            spender == owner ||
                DDC721.getApproved(ddcId) == spender ||
                DDC721.isApprovedForAll(owner, spender),
            "DDC721: invalid signature"
        );
    }

    /**
     * @dev Get signer account.
     */
    function _getSignerAccount(
        bytes memory sig,
        bytes32 msgHash
    ) private view returns (address) {
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
        //_requireOnePlatform(_msgSender(), signer);
        require(
            deadline == 0 || block.timestamp <= deadline,
            "DDC721: expired signature"
        );
        _nonces[signer]++;
        require(nonce == _nonces[signer], "DDC721:invalid nonce");
    }

    /**
     * @dev recovers an address of the signer.
     */
    function _recoverSigner(
        bytes32 message,
        bytes memory sig
    ) private pure returns (address) {
        require(sig.length == 65, "DDC721:invalid signature length");
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
     * @dev Modifier to make a function callable only when the contract is not paused.
     *
     * Requirements:
     *
     * - The contract must not be paused.
     */
    function _whenNotPaused(address sender) private view {
        IAuthority.AccountInfo memory senderAcc;
        (
            senderAcc.accountDID,
            ,
            senderAcc.accountRole,
            senderAcc.leaderDID,
            senderAcc.platformState,
            senderAcc.operatorState,

        ) = _authorityProxy.getAccount(sender);
        require(
            !((senderAcc.accountRole == IAuthority.Role.PlatformManager ||
                senderAcc.accountRole == IAuthority.Role.Consumer) && paused()),
            "DDC721: paused"
        );
    }
}
