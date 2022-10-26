pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC721/IERC721.sol";
import "@openzeppelin/contracts/token/ERC721/IERC721Receiver.sol";
import "@openzeppelin/contracts/token/ERC721/extensions/IERC721Metadata.sol";
import "@openzeppelin/contracts/utils/introspection/ERC165.sol";
import "@openzeppelin/contracts/utils/Address.sol";
import "../../utils/OwnableUpgradeable.sol";
import "../../proxy/utils/UUPSUpgradeable.sol";

/**
 * @dev Implementation of https://eips.ethereum.org/EIPS/eip-721[ERC721] Non-Fungible Token Standard, including
 * the Metadata extension, but not including the Enumerable extension, which is available separately as
 * {ERC721Enumerable}.
 */
contract ERC721 is
    IERC721,
    IERC721Metadata,
    ERC165,
    OwnableUpgradeable,
    UUPSUpgradeable
{
    using Address for address;

    // Contract token name
    string private _name;

    // Contract token symbol
    string private _symbol;

    // The last generated token ID
    uint256 private _lastTokenId;

    // Mapping from token ID to token name
    mapping(uint256 => string) private _tokenNames;

    // Mapping from token ID to token symbol
    mapping(uint256 => string) private _tokenSymbols;

    // Mapping from token ID to token uri
    mapping(uint256 => string) private _tokenURIs;

    // Mapping from token ID to owner address
    mapping(uint256 => address) private _owners;

    // Mapping owner address to token count
    mapping(address => uint256) private _balances;

    // Mapping from token ID to approved address
    mapping(uint256 => address) private _tokenApprovals;

    // Mapping from owner to operator approvals
    mapping(address => mapping(address => bool)) private _operatorApprovals;

    // Cross chain application contract address
    address _eccapAddress;

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
     * @dev  Initializes a name and symbol for the token.
     *
     * Requirements:
     * - sender must be the owner only.
     */
    function setNameAndSymbol(string memory name_, string memory symbol_)
        public
        onlyOwner
    {
        _name = name_;
        _symbol = symbol_;
    }

    /**
     * @dev Sets eccap proxy address.
     *
     * Requirements:
     * - sender must be the owner only.
     */
    function setECCAPAddress(address eccapAddress) public onlyOwner {
        require(eccapAddress != address(0), "ERC721:zero address");
        _eccapAddress = eccapAddress;
    }

    /**
     * @dev See {IERC165-supportsInterface}.
     */
    function supportsInterface(bytes4 interfaceId)
        public
        view
        virtual
        override(ERC165, IERC165)
        returns (bool)
    {
        return
            interfaceId == type(IERC721).interfaceId ||
            interfaceId == type(IERC721Metadata).interfaceId ||
            super.supportsInterface(interfaceId);
    }

    /**
     * @dev Mints `tokenURI` and transfers it to `to`.
     *
     * Requirements:
     *
     * - `to` cannot be the zero address.
     *
     * Emits a {Transfer} event.
     */
    function mint(address to, string memory tokenURI) public {
        _requireCrossChainAppliedContract();
        uint256 tokenId = _lastTokenId + 1;
        _mint(to, tokenId, tokenURI);
    }

    /**
     * @dev Safely mints `tokenURI` and transfers it to `to`.
     *
     * Requirements:
     *
     * - `to` cannot be the zero address.
     * - If `to` refers to a smart contract, it must implement {IERC721Receiver-onERC721Received}, which is called upon a safe transfer.
     *
     * Emits a {Transfer} event.
     */
    function safeMint(
        address to,
        string memory tokenURI,
        bytes memory data
    ) public {
        _requireCrossChainAppliedContract();
        uint256 tokenId = _lastTokenId + 1;
        _mint(to, tokenId, tokenURI);
        require(
            _checkOnERC721Received(address(0), to, tokenId, data),
            "ERC721:transfer to non ERC721Receiver implementer"
        );
    }

    /**
     * @dev See {IERC721-safeTransferFrom}.
     */
    function safeTransferFrom(
        address from,
        address to,
        uint256 tokenId,
        bytes memory data
    ) public override {
        _requireAvailableTokenAccount(from);
        _requireAvailableTokenAccount(to);
        _requireExists(tokenId);
        _requireApprovedOrOwner(_msgSender(), tokenId);
        _transfer(from, to, tokenId);
        require(
            _checkOnERC721Received(from, to, tokenId, data),
            "ERC721:transfer to non ERC721Receiver ismplementer"
        );
    }

    /**
     * @dev See {IERC721-safeTransferFrom}.
     */
    function safeTransferFrom(
        address from,
        address to,
        uint256 tokenId
    ) public override {
        ERC721.safeTransferFrom(from, to, tokenId, "");
    }

    /**
     * @dev See {IERC721-transferFrom}.
     */
    function transferFrom(
        address from,
        address to,
        uint256 tokenId
    ) public override {
        _requireAvailableTokenAccount(from);
        _requireAvailableTokenAccount(to);
        _requireExists(tokenId);
        _requireApprovedOrOwner(_msgSender(), tokenId);
        _transfer(from, to, tokenId);
    }

    /**
     * @dev See {IERC721-approve}.
     */
    function approve(address to, uint256 tokenId) public override {
        address owner = ERC721.ownerOf(tokenId);
        _requireAvailableTokenAccount(to);
        require(to != owner, "ERC721:approval to current owner");
        require(
            _msgSender() == owner ||
                ERC721.isApprovedForAll(owner, _msgSender()),
            "ERC721:approve caller is not owner nor approved for all"
        );
        _tokenApprovals[tokenId] = to;
        emit Approval(owner, to, tokenId);
    }

    /**
     * @dev See {IERC721-getApproved}.
     */
    function getApproved(uint256 tokenId)
        public
        view
        override
        returns (address)
    {
        _requireExists(tokenId);
        return _tokenApprovals[tokenId];
    }

    /**
     * @dev See {IERC721-setApprovalForAll}.
     */
    function setApprovalForAll(address operator, bool approved)
        public
        override
    {
        _requireAvailableTokenAccount(operator);
        require(operator != _msgSender(), "ERC721:approve to caller");
        _operatorApprovals[_msgSender()][operator] = approved;
        emit ApprovalForAll(_msgSender(), operator, approved);
    }

    /**
     * @dev See {IERC721-isApprovedForAll}.
     */
    function isApprovedForAll(address owner, address operator)
        public
        view
        override
        returns (bool)
    {
        require(
            owner != address(0) && operator != address(0),
            "ERC721:zero address"
        );
        return _operatorApprovals[owner][operator];
    }

    /**
     * @dev Burns a token.
     *
     * Requirements:
     * - sender must own `tokenId` or be an approved operator.
     */
    function burn(uint256 tokenId) public {
        _requireExists(tokenId);
        _requireApprovedOrOwner(_msgSender(), tokenId);
        address owner = ERC721.ownerOf(tokenId);
        delete _tokenApprovals[tokenId];
        delete _tokenURIs[tokenId];
        _balances[owner] -= 1;
        delete _owners[tokenId];
        emit Transfer(owner, address(0), tokenId);
    }

    /**
     * @dev See {IERC721-balanceOf}.
     */
    function balanceOf(address owner) public view override returns (uint256) {
        require(
            owner != address(0),
            "ERC721:balance query for the zero address"
        );
        return _balances[owner];
    }

    /**
     * @dev See {IERC721-ownerOf}.
     */
    function ownerOf(uint256 tokenId) public view override returns (address) {
        address owner = _owners[tokenId];
        require(
            owner != address(0),
            "ERC721:owner query for nonexistent token"
        );
        return owner;
    }

    /**
     * @dev See {IERC721Metadata-name}.
     */
    function name() public view override returns (string memory) {
        return _name;
    }

    /**
     * @dev See {IERC721Metadata-symbol}.
     */
    function symbol() public view override returns (string memory) {
        return _symbol;
    }

    /**
     * @dev See {IERC721-tokenURI}.
     */
    function tokenURI(uint256 tokenId)
        public
        view
        override
        returns (string memory)
    {
        _requireExists(tokenId);
        return _tokenURIs[tokenId];
    }

    /**
     * @dev Returns the last tokenId.
     *
     * Requirements:
     */
    function getLatestTokenId() public view returns (uint256) {
        return _lastTokenId;
    }

    /**
     * @dev Mints `tokenId` and transfers it to `to`.
     *
     * Requirements:
     *
     * - `tokenId` must not exist.
     * - `to` cannot be the zero address.
     *
     * Emits a {Transfer} event.
     */
    function _mint(
        address to,
        uint256 tokenId,
        string memory tokenURI
    ) private {
        _requireAvailableTokenAccount(to);
        require(_owners[tokenId] == address(0), "ERC721:already minted");
        _balances[to] += 1;
        _owners[tokenId] = to;
        if (bytes(tokenURI).length != 0) {
            _tokenURIs[tokenId] = tokenURI;
        }
        _lastTokenId = tokenId;
        emit Transfer(address(0), to, tokenId);
    }

    /**
     * @dev Transfers `tokenId` from `from` to `to`.
     *
     * Requirements:
     *
     * - `to` cannot be the zero address.
     * - `tokenId` token must be owned by `from`.
     *
     * Emits a {Transfer} event.
     */
    function _transfer(
        address from,
        address to,
        uint256 tokenId
    ) private {
        require(
            ERC721.ownerOf(tokenId) == from,
            "ERC721:transfer of token that is not own"
        );
        delete _tokenApprovals[tokenId];
        _balances[from] -= 1;
        _balances[to] += 1;
        _owners[tokenId] = to;
        emit Transfer(from, to, tokenId);
    }

    /**
     * @dev Internal function to invoke {IERC721Receiver-onERC721Received} on a target address.
     * The call is not executed if the target address is not a contract.
     *
     * @param from address representing the previous owner of the given token ID
     * @param to target address that will receive the tokens
     * @param tokenId uint256 ID of the token to be transferred
     * @param data bytes optional data to send along with the call
     * @return bool whether the call correctly returned the expected magic value
     */
    function _checkOnERC721Received(
        address from,
        address to,
        uint256 tokenId,
        bytes memory data
    ) private returns (bool) {
        if (to.isContract()) {
            try
                IERC721Receiver(to).onERC721Received(
                    msg.sender,
                    from,
                    tokenId,
                    data
                )
            returns (bytes4 retval) {
                return retval == IERC721Receiver.onERC721Received.selector;
            } catch (bytes memory reason) {
                if (reason.length == 0) {
                    revert(
                        "ERC721: transfer to non ERC721Receiver implementer"
                    );
                } else {
                    /// @solidity memory-safe-assembly
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
     * @dev Requires `tokenId` exists.
     */
    function _requireExists(uint256 tokenId) private view {
        require(_owners[tokenId] != address(0), "ERC721:nonexistent token");
    }

    /**
     * @dev Requires a available account.
     *
     * Requirements:
     * - `sender` must be a available `token` account.
     */
    function _requireAvailableTokenAccount(address account) private view {
        require(account != address(0), "ERC721:zero address");
    }

    /**
     * @dev Requires `spender` is allowed to manage `tokenId`.
     *
     * Requirements:
     * - `tokenId` must exists.
     * - `spender` is owner or approved.
     */
    function _requireApprovedOrOwner(address spender, uint256 tokenId)
        private
        view
    {
        address owner = ERC721.ownerOf(tokenId);
        require(
            spender == owner ||
                ERC721.getApproved(tokenId) == spender ||
                ERC721.isApprovedForAll(owner, spender),
            "ERC721:not owner nor approved"
        );
    }

    /**
     * @dev Requires `spender` is Cross chain application contract address.
     */
    function _requireCrossChainAppliedContract() private view {
        require(
            _msgSender() == _eccapAddress,
            "ERC721:The sender must be a cross chain application contract"
        );
    }
}
