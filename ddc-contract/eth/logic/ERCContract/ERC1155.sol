pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC1155/IERC1155.sol";
import "@openzeppelin/contracts/token/ERC1155/IERC1155Receiver.sol";
import "@openzeppelin/contracts/token/ERC1155/extensions/IERC1155MetadataURI.sol";
import "@openzeppelin/contracts/utils/introspection/ERC165.sol";
import "@openzeppelin/contracts/utils/Address.sol";
import "@openzeppelin/contracts/utils/Counters.sol";
import "../../utils/OwnableUpgradeable.sol";
import "../../proxy/utils/UUPSUpgradeable.sol";

/**
 * @dev Implementation of the basic standard multi-token.
 * See https://eips.ethereum.org/EIPS/eip-1155
 */
contract ERC1155 is ERC165, IERC1155, UUPSUpgradeable, OwnableUpgradeable {
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

    // Mapping owner address to token count
    mapping(uint256 => mapping(address => uint256)) private _balances;

    // Mapping from token ID to token state
    mapping(address => mapping(address => bool)) private _operatorApprovals;

    // Mapping from token ID list
    mapping(uint256 => bool) _tokenIds;

    // Mapping from token ID to token amounts
    mapping(uint256 => uint256) _nftAmts;

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
            interfaceId == type(IERC1155).interfaceId ||
            super.supportsInterface(interfaceId);
    }

    /**
     * @dev Creates `amount` tokens of token type `tokenId`, and assigns them to `to`.
     *
     * Emits a {TransferSingle} event.
     *
     * Requirements:
     *
     * - `to` cannot be the zero address.
     * - If `to` refers to a smart contract, it must implement {IERC1155Receiver-onERC1155Received} and return the
     */
    function safeMint(
        address to,
        uint256 amount,
        string memory tokenURI,
        bytes memory data
    ) public {
        _requireCrossChainAppliedContract();
        _requireAvailableTokenAccount(to);
        uint256 tokenId = _lastTokenId + 1;
        _mint(to, tokenId, amount, tokenURI);
        emit TransferSingle(_msgSender(), address(0), to, tokenId, amount);
        _doSafeTransferAcceptanceCheck(
            _msgSender(),
            address(0),
            to,
            tokenId,
            amount,
            data
        );
    }

    /**
     * @dev Bulk create `amount` tokens with token type `tokenId` and assign them to `to`.
     *
     * Emits a {TransferBatch} event.
     *
     * Requirements:
     *
     * - `to` cannot be the zero address.
     * - If `to` refers to a smart contract, it must implement {IERC1155Receiver-onERC1155Received} and return the
     */
    function safeMintBatch(
        address to,
        uint256[] memory amounts,
        string[] memory tokenURIs,
        bytes memory data
    ) public {
        _requireCrossChainAppliedContract();
        _requireAvailableTokenAccount(to);
        require(amounts.length == tokenURIs.length, "ERC1155:length mismatch");
        uint256 tokenId = _lastTokenId;
        uint256[] memory tokenIds = new uint256[](amounts.length);
        for (uint256 i = 0; i < amounts.length; i++) {
            tokenId += 1;
            tokenIds[i] = tokenId;
            _mint(to, tokenIds[i], amounts[i], tokenURIs[i]);
        }
        emit TransferBatch(_msgSender(), address(0), to, tokenIds, amounts);
        _doSafeBatchTransferAcceptanceCheck(
            _msgSender(),
            address(0),
            to,
            tokenIds,
            amounts,
            data
        );
    }

    /**
     * @dev See {IERC1155-setApprovalForAll}.
     */
    function setApprovalForAll(address operator, bool approved)
        public
        override
    {
        _requireAvailableTokenAccount(operator);
        require(_msgSender() != operator, "ERC1155:setting approval for self");
        _operatorApprovals[_msgSender()][operator] = approved;
        emit ApprovalForAll(_msgSender(), operator, approved);
    }

    /**
     * @dev See {IERC1155-isApprovedForAll}.
     */
    function isApprovedForAll(address owner, address operator)
        public
        view
        override
        returns (bool)
    {
        require(
            owner != address(0) && operator != address(0),
            "ERC1155:zero address"
        );
        return _operatorApprovals[owner][operator];
    }

    /**
     * @dev See {IERC1155-safeTransferFrom}.
     */
    function safeTransferFrom(
        address from,
        address to,
        uint256 tokenId,
        uint256 amount,
        bytes memory data
    ) public override {
        _requireAvailableTokenAccount(from);
        _requireAvailableTokenAccount(to);
        _requireApprovedOrOwner(from, _msgSender());
        _transfer(from, to, tokenId, amount);
        emit TransferSingle(_msgSender(), from, to, tokenId, amount);
        _doSafeTransferAcceptanceCheck(
            _msgSender(),
            from,
            to,
            tokenId,
            amount,
            data
        );
    }

    /**
     * @dev See {IERC1155-safeBatchTransferFrom}.
     */
    function safeBatchTransferFrom(
        address from,
        address to,
        uint256[] memory tokenIds,
        uint256[] memory amounts,
        bytes memory data
    ) public override {
        _requireAvailableTokenAccount(from);
        _requireAvailableTokenAccount(to);
        _requireApprovedOrOwner(from, _msgSender());
        require(tokenIds.length == amounts.length, "ERC1155:length mismatch");
        for (uint256 i = 0; i < tokenIds.length; ++i) {
            _transfer(from, to, tokenIds[i], amounts[i]);
        }
        emit TransferBatch(_msgSender(), from, to, tokenIds, amounts);
        _doSafeBatchTransferAcceptanceCheck(
            _msgSender(),
            from,
            to,
            tokenIds,
            amounts,
            data
        );
    }

    /**
     * @dev Destroy a single token owned by the owner
     *
     * Requirements:
     *
     */
    function burn(address owner, uint256 tokenId) public {
        _requireApprovedOrOwner(owner, _msgSender());
        _burn(owner, tokenId);
        emit TransferSingle(_msgSender(), owner, address(0), tokenId, 0);
    }

    /**
     * @dev Destroy the token list owned by the owner in batches
     *
     * Requirements:
     *
     */
    function burnBatch(address owner, uint256[] memory tokenIds) public {
        _requireApprovedOrOwner(owner, _msgSender());
        require(tokenIds.length != 0, "ERC1155: length cannot be zero");
        uint256[] memory amounts = new uint256[](tokenIds.length);
        for (uint256 i = 0; i < tokenIds.length; i++) {
            _burn(owner, tokenIds[i]);
        }
        emit TransferBatch(_msgSender(), owner, address(0), tokenIds, amounts);
    }

    /**
     * @dev See {IERC1155-balanceOf}.
     */
    function balanceOf(address owner, uint256 tokenId)
        public
        view
        override
        returns (uint256)
    {
        require(owner != address(0), "ERC1155:zero address");
        return _balances[tokenId][owner];
    }

    /**
     * @dev See {IERC1155-balanceOfBatch}.
     */
    function balanceOfBatch(address[] memory owners, uint256[] memory tokenIds)
        public
        view
        override
        returns (uint256[] memory)
    {
        require(owners.length == tokenIds.length, "ERC1155:length mismatch");
        uint256[] memory batchBalances = new uint256[](owners.length);
        for (uint256 i = 0; i < tokenIds.length; i++) {
            batchBalances[i] = ERC1155.balanceOf(owners[i], tokenIds[i]);
        }
        return batchBalances;
    }

    /**
     * @dev Returns the token collection name.
     */
    function name() public view returns (string memory) {
        return _name;
    }

    /**
     * @dev Returns the token collection symbol.
     */
    function symbol() public view returns (string memory) {
        return _symbol;
    }

    /**
     * @dev See {IERC1155-tokenURI}.
     */
    function tokenURI(uint256 tokenId) public view returns (string memory) {
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
     * @dev Creates `amount` tokens of token type `tokenId`, and assigns them to `to`.
     *
     * Requirements:
     *
     * - `to` cannot be the zero address.
     * - If `to` refers to a smart contract, it must implement {IERC1155Receiver-onERC1155Received} and return the
     * acceptance magic value.
     */
    function _mint(
        address to,
        uint256 tokenId,
        uint256 amount,
        string memory tokenURI
    ) private {
        _requireMintConditions(tokenId, amount);
        _balances[tokenId][to] += amount;
        if (bytes(tokenURI).length != 0) {
            _tokenURIs[tokenId] = tokenURI;
        }
        _nftAmts[tokenId] = amount;
        _tokenIds[tokenId] = true;
        _lastTokenId = tokenId;
    }

    /**
     * @dev Transfers `amount` tokens of token type `tokenId` from `from` to `to`.
     *
     *
     * Requirements:
     *
     * - `to` cannot be the zero address.
     * - `from` must have a balance of tokens of type `tokenId` of at least `amount`.
     */
    function _transfer(
        address from,
        address to,
        uint256 tokenId,
        uint256 amount
    ) private {
        _requireExists(tokenId);
        _requireAdequateBalance(from, tokenId, amount);
        uint256 fromBalance = _balances[tokenId][from];
        unchecked {
            _balances[tokenId][from] = fromBalance - amount;
        }
        _balances[tokenId][to] += amount;
    }

    /**
     * @dev Destroys `amount` tokens of token type `tokenId` from `from`
     *
     *
     * Requirements:
     *
     * - `from` cannot be the zero address.
     * - `from` must have at least `amount` tokens of token type `tokenId`.
     */
    function _burn(address owner, uint256 tokenId) private {
        _requireAvailableTokenAccount(owner);
        _requireExists(tokenId);
        _requireAdequateBalance(owner, tokenId);
        uint256 amount = ERC1155.balanceOf(owner, tokenId);
        _nftAmts[tokenId] -= amount;
        _balances[tokenId][owner] = 0;
        if (_nftAmts[tokenId] == 0) {
            delete _tokenURIs[tokenId];
            _tokenIds[tokenId] = false;
        }
    }

    /**
     * @dev  check acceptance for SafeTransfer
     */
    function _doSafeTransferAcceptanceCheck(
        address operator,
        address from,
        address to,
        uint256 tokenId,
        uint256 amount,
        bytes memory data
    ) private {
        if (to.isContract()) {
            try
                IERC1155Receiver(to).onERC1155Received(
                    operator,
                    from,
                    tokenId,
                    amount,
                    data
                )
            returns (bytes4 response) {
                if (response != IERC1155Receiver.onERC1155Received.selector) {
                    revert("ERC1155:ERC1155Receiver rejected");
                }
            } catch Error(string memory reason) {
                revert(reason);
            } catch {
                revert("ERC1155:transfer to non ERC1155Receiver implementer");
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
        uint256[] memory tokenIds,
        uint256[] memory amounts,
        bytes memory data
    ) private {
        if (to.isContract()) {
            try
                IERC1155Receiver(to).onERC1155BatchReceived(
                    operator,
                    from,
                    tokenIds,
                    amounts,
                    data
                )
            returns (bytes4 response) {
                if (
                    response != IERC1155Receiver.onERC1155BatchReceived.selector
                ) {
                    revert("ERC1155:ERC1155Receiver rejected");
                }
            } catch Error(string memory reason) {
                revert(reason);
            } catch {
                revert("ERC1155:transfer to non ERC1155Receiver implementer");
            }
        }
    }

    /**
     * @dev Requires `tokenId` exists.
     */
    function _requireExists(uint256 tokenId) private view {
        require(_tokenIds[tokenId], "ERC1155:nonexistent token");
    }

    /**
     * @dev Requires `tokenId` does not exist, amount greater than zero.
     */
    function _requireMintConditions(uint256 tokenId, uint256 amount)
        private
        view
    {
        require(amount > 0, "ERC1155:invalid amount");
        require(!_tokenIds[tokenId], "ERC1155:already minted");
    }

    /**
     * @dev Requires adequate balance.
     */
    function _requireAdequateBalance(address owner, uint256 tokenId)
        private
        view
    {
        require(
            ERC1155.balanceOf(owner, tokenId) > 0,
            "ERC1155:insufficient balance"
        );
    }

    /**
     * @dev Requires adequate balance.
     */
    function _requireAdequateBalance(
        address owner,
        uint256 tokenId,
        uint256 amount
    ) private view {
        require(amount > 0, "ERC1155:invalid amount");
        require(
            ERC1155.balanceOf(owner, tokenId) >= amount,
            "ERC1155:insufficient balance"
        );
    }

    /**
     * @dev Requires a available account.
     *
     * Requirements:
     * - `sender` must be a available `token` account.
     */
    function _requireAvailableTokenAccount(address account) private view {
        require(account != address(0), "ERC1155:zero address");
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
            ERC1155.isApprovedForAll(owner, spender) || spender == owner,
            "ERC1155:not owner nor approved"
        );
    }

    /**
     * @dev Requires `spender` is Cross chain application contract address.
     */
    function _requireCrossChainAppliedContract() private view {
        require(
            _msgSender() == _eccapAddress,
            "ERC1155:The sender must be a cross chain application contract"
        );
    }
}
