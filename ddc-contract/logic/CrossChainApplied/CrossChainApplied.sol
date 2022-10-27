// SPDX-License-Identifier:BSN DDC

pragma solidity ^0.8.0;

import "../../interface/DDC721/IDDC721.sol";
import "../../interface/DDC1155/IDDC1155.sol";
import "../../utils/OwnableUpgradeable.sol";
import "../../proxy/utils/UUPSUpgradeable.sol";
import "../../utils/AddressUpgradeable.sol";
import "../../interface/DDC1155/IERC1155Receiver.sol";
import "../../interface/DDC721/IERC721Receiver.sol";
import "../../interface/Charge/ICharge.sol";
import "../../interface/Authority/IAuthority.sol";
import "../../interface/CrossChainApplied/ICrossChainApplied.sol";
import "../../interface/CrossChainApplied/IEthCrossChainManager.sol";
import "../../interface/CrossChainApplied/IEthCrossChainManagerProxy.sol";
import "../../utils/ZeroCopySink.sol";
import "../../utils/ZeroCopySource.sol";

/**
 * @title CrossChain
 * @author
 * @dev CrossChain Application contract
 */
contract CrossChainApplied is
    ICrossChainApplied,
    OwnableUpgradeable,
    UUPSUpgradeable
{
    using AddressUpgradeable for address;
    // Cross-chain management contract address
    address _address;

    // From chain id
    uint64 _chainID;

    // DDC cross-chain original chain record
    mapping(uint256 => CrossChainInfo) private tagCrossChainList;

    // Latest cross-chain ID
    uint256 lastCrossChainID;

    // DDC721 proxy contract
    IDDC721 private _ddc721Proxy;

    // DDC1155 proxy contract
    IDDC1155 private _ddc1155Proxy;

    // Charge proxy contract
    ICharge private _chargeProxy;

    // Authority proxy contract
    IAuthority private _authorityProxy;

    // Cross-chain management proxy contract
    IEthCrossChainManagerProxy private _ethCrossChainManagerProxy;

    // Cross-chain management contract
    IEthCrossChainManager private eccm;

    // Target chain chainId whitelist
    uint64[] private _chainId;
    mapping(uint64 => uint256) _chainIdIndex;

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
     * @dev See {ICrossChainBusiness-setDDC1155Proxy}.
     */
    function setDDC1155Proxy(address ddc1155ProxyAddress) public onlyOwner {
        _requireContract(ddc1155ProxyAddress);
        _ddc1155Proxy = IDDC1155(ddc1155ProxyAddress);
    }

    /**
     * @dev See {ICrossChainBusiness-setDDC721Proxy}.
     */
    function setDDC721Proxy(address ddc721ProxyAddress) public onlyOwner {
        _requireContract(ddc721ProxyAddress);
        _ddc721Proxy = IDDC721(ddc721ProxyAddress);
    }

    /**
     * @dev See {ICrossChainBusiness-setChargeProxyAddress}.
     */
    function setChargeProxyAddress(address chargeProxyAddress)
        public
        onlyOwner
    {
        _requireContract(chargeProxyAddress);
        _chargeProxy = ICharge(chargeProxyAddress);
    }

    /**
     * @dev See {ICrossChainBusiness-setAuthorityProxyAddress}.
     */
    function setAuthorityProxyAddress(address authorityProxyAddress)
        external
        onlyOwner
    {
        _requireContract(authorityProxyAddress);
        _authorityProxy = IAuthority(authorityProxyAddress);
    }

    /**
     * @dev See {ICrossChainBusiness-setBaseData}.
     */
    function setBaseData(address eccmpAddress, uint64 fromChainID)
        public
        override
        onlyOwner
    {
        // Check if the cross-chain contract address is 0
        _requireContract(eccmpAddress);
        // Check the source chain ID
        require(fromChainID > 0, "CrossChainBusiness: invalid chainId.");
        // Store cross-chain contract address and original chain ID
        _address = eccmpAddress;
        _chainID = fromChainID;
        // Call event
        emit SetBaseData(_msgSender(), eccmpAddress, fromChainID);
    }

    /**
     * @dev See {ICrossChainBusiness-crossChainTransfer}.
     */
    function crossChainTransfer(
        DDCType ddcType,
        address signer,
        address to,
        uint256 ddcId,
        bytes memory data,
        uint64 toChainID,
        address toCCAddr,
        string memory funcName
    ) public override {
        // Check whether the caller account has permission, if not, return a prompt message
        _requireSenderHasFuncPermission();

        // Check whether the target chain signer's account address is 0 address, if yes, return a prompt message
        _requireContract(signer);

        // Check whether the recipient account address of the target chain is the 0 address, and if so, return a prompt message
        _requireContract(to);

        // Requires an available chainId
        _requireEffectiveChainId(toChainID);

        // Check whether the DDC type is 721, if not, return a prompt message,DDC1155 is not supported temporarily
        require(
            ddcType == DDCType.ddc721,
            "CrossChainApplied: unsupport ddctype"
        );

        // Define temporary transfer data
        tempArgs memory arg;

        //Call different business main contracts to lock DDC
        if (ddcType == DDCType.ddc721) {
            //Get DDC Owner
            arg.fromOwner = _ddc721Proxy.ownerOf(ddcId);
            // Check if the account meets the requirements
            _requireApprovedOrOwner(_msgSender(), ddcId);
            // Lock ddc721
            _ddc721Proxy.lock(ddcId);
            // Get ddcURI of ddc
            arg.ddcURI = _ddc721Proxy.ddcURI(ddcId);
            // The number of ddc is 1
            arg.amount = 1;
        } else {
            //Get DDC Owner
            address[] memory owner = new address[](1);
            owner = _ddc1155Proxy.ownerOf(ddcId);
            arg.fromOwner = owner[0];
            // Check if the account meets the requirements
            _requireApprovedOrOwner(arg.fromOwner, _msgSender());
            // Lock ddc1155
            _ddc1155Proxy.lock(ddcId);
            // Get ddcURI of ddc
            arg.ddcURI = _ddc1155Proxy.ddcURI(ddcId);
            // Get the number of ddc1155
            arg.amount = _ddc1155Proxy.balanceOf(arg.fromOwner, ddcId);
        }

        // Check ddcURI length
        _requireDDClength(arg.ddcURI);

        // Call the billing contract to pay the DDC cross-chain business fee
        _chargeProxy.pay(_msgSender(), msg.sig, ddcId);

        // Set the latest cross-chain ID
        lastCrossChainID += 1;
        // Save cross-chain information
        CrossChainInfo memory crossChainInfo;
        crossChainInfo.crossChainId = lastCrossChainID;
        crossChainInfo.ddcType = uint8(ddcType);
        crossChainInfo.sender = _msgSender();
        crossChainInfo.owner = arg.fromOwner;
        crossChainInfo.ddcId = ddcId;
        crossChainInfo.fee = _chargeProxy.queryFee(address(this), msg.sig);
        crossChainInfo.state = uint8(State.CrossChain);
        crossChainInfo.remark = "";
        _saveCrossChain(lastCrossChainID, crossChainInfo);

        // Convert data format
        // Final assembly of cross-chain business data
        DDCCrossChainData memory crossArgs;
        crossArgs.crossChainId = lastCrossChainID;
        crossArgs.from = arg.fromOwner.addressToBytes();
        crossArgs.ddcType = uint8(ddcType);
        crossArgs.signer = signer.addressToBytes();
        crossArgs.to = to.addressToBytes();
        crossArgs.ddcId = ddcId;
        crossArgs.ddcURI = bytes(arg.ddcURI);
        crossArgs.data = data;
        crossArgs.amount = arg.amount;

        // Invoke the cross-chain management proxy contract
        eccm = IEthCrossChainManager(
            IEthCrossChainManagerProxy(_address).getEthCrossChainManager()
        );
        eccm.crossChain(
            toChainID,
            toCCAddr.addressToBytes(),
            bytes(funcName),
            _serializeTxArgs(crossArgs)
        );

        // Call event
        _crossChainTransferEvent(
            ddcType,
            signer,
            to,
            ddcId,
            toChainID,
            toCCAddr,
            arg
        );
    }

    /**
     * @dev See {ICrossChainBusiness-rollBackTransfer}.
     */
    function updateCrossChainStatus(
        uint256 crossChainID,
        State state,
        string memory remark
    ) public override {
        // Check whether the caller account has permission, if not, return a prompt message
        _requireSenderHasFuncPermission();

        // Check if it is an operator account
        _requireOperator();

        // Check whether the cross-chain ID is greater than 0, if not, return a prompt message
        _requireCrossChainID(crossChainID);

        // Check whether the DDC type is 721 or 1155, if not, return a prompt message
        CrossChainInfo memory cross = _getCrossChain(crossChainID);

        //Check whether the DDC cross-chain original chain record corresponding to the cross-chain ID exists, if not, return a prompt message
        require(
            cross.crossChainId == crossChainID,
            "CrossChainApplied: crossChainID does not exist"
        );
        //If the status is successful, update the DDC cross-chain original chain record status according to the cross-chain ID
        if (state == State.CrossChainSuccess) {
            cross.state = uint8(state);
            _saveCrossChain(crossChainID, cross);
        } else if (state == State.CrossChainFailure) {
            // Call different business main contracts to unlock DDC according to DDC type
            if (cross.ddcType == uint8(DDCType.ddc721)) {
                // Unlock ddc721
                _ddc721Proxy.unlock(cross.ddcId);
            } else {
                // Unlock ddc1155
                _ddc1155Proxy.unlock(cross.ddcId);
            }
            // Call the billing contract again to exit the DDC cross-chain business fee
            _chargeProxy.recharge(cross.sender, cross.fee);
        }
        // Call event
        emit UpdateCrossChainStatus(_msgSender(), crossChainID, state, remark);
    }

    /**
     * @dev See {ICrossChainBusiness-crossChainMint}.
     */
    function crossChainMint(
        bytes memory ccData,
        bytes memory fromCCAddr,
        uint64 fromChainID
    ) public override {
        // Check whether the caller account has permission, if not, return a prompt message
        _requireSenderHasFuncPermission();

        // Parse cross-chain data
        DDCCrossChainData memory crossData = _deserializeTxArgs(ccData);

        // According to the parsed cross-chain data, check whether the recipient's account address is the 0 address, and if so, return a prompt message
        _requireDDCID(crossData.ddcId);

        // Safe mint ddc
        if (crossData.ddcType == uint8(DDCType.ddc721)) {
            // Safe mint ddc721
            _ddc721Proxy.safeMint(
                _bytesToAddress(crossData.to),
                string(crossData.ddcURI),
                crossData.data
            );
        } else {
            // Safe mint ddc1155
            _ddc1155Proxy.safeMint(
                _bytesToAddress(crossData.to),
                crossData.amount,
                string(crossData.ddcURI),
                crossData.data
            );
        }

        // Call event
        emit CrossChainMint(
            _msgSender(),
            0,
            DDCType(crossData.ddcType),
            _bytesToAddress(crossData.signer),
            _bytesToAddress(crossData.to),
            crossData.ddcId,
            string(crossData.ddcURI),
            crossData.amount,
            fromChainID,
            block.chainid,
            _bytesToAddress(fromCCAddr),
            address(this)
        );
    }

    /**
     * @dev Add target chain chainId.
     */
    function addTargetChainId(uint64 chainId) public onlyOwner {
        uint256 index = _chainIdIndex[chainId];
        if (index == 0) {
            _chainId.push(chainId);
            _chainIdIndex[chainId] = _chainId.length;
        } else {
            _chainId[index - 1] = chainId;
        }
    }

    /**
     * @dev Delete target chain chainId.
     */
    function deleteTargetChainId(uint64 chainId) public onlyOwner {
        uint256 index = _getChainIdIndex(chainId);
        _chainId[index - 1] = _chainId[_chainId.length - 1];
        _chainIdIndex[_chainId[_chainId.length - 1]] = _chainIdIndex[chainId];
        delete _chainIdIndex[chainId];
        _chainId.pop();
    }

    /**
     * @dev Get the list of target chain chainIds.
     */
    function getChainId() public view returns (uint64[] memory) {
        return _chainId;
    }

    /**
     * @dev Save cross-chain information.
     */
    function _saveCrossChain(uint256 key, CrossChainInfo memory cross) private {
        tagCrossChainList[key] = cross;
    }

    /**
     * @dev Get cross-chain information.
     */
    function _getCrossChain(uint256 key)
        private
        view
        returns (CrossChainInfo memory)
    {
        return tagCrossChainList[key];
    }

    /**
     * @dev Cross-chain transfer events.
     */
    function _crossChainTransferEvent(
        DDCType ddcType,
        address signer,
        address to,
        uint256 ddcId,
        uint64 toChainID,
        address toCCAddr,
        tempArgs memory arg
    ) private {
        // Call event
        emit CrossChainTransfer(
            _msgSender(),
            lastCrossChainID,
            ddcType,
            signer,
            to,
            ddcId,
            arg.ddcURI,
            arg.amount,
            _chainID,
            toChainID,
            _msgSender(),
            toCCAddr,
            _chargeProxy.queryFee(address(this), msg.sig)
        );
    }

    /**
     * @dev Transfer data encoding.
     */
    function _serializeTxArgs(DDCCrossChainData memory args)
        internal
        pure
        returns (bytes memory)
    {
        bytes memory buff;
        buff = abi.encodePacked(
            ZeroCopySink.WriteUint256(args.crossChainId),
            ZeroCopySink.WriteVarBytes(args.from),
            ZeroCopySink.WriteUint8(args.ddcType),
            ZeroCopySink.WriteVarBytes(args.signer),
            ZeroCopySink.WriteVarBytes(args.to),
            ZeroCopySink.WriteUint256(args.ddcId),
            ZeroCopySink.WriteUint256(args.amount),
            ZeroCopySink.WriteVarBytes(args.ddcURI),
            ZeroCopySink.WriteVarBytes(args.data)
        );
        return buff;
    }

    /**
     * @dev Decoding transmitted data.
     */
    function _deserializeTxArgs(bytes memory values)
        internal
        pure
        returns (DDCCrossChainData memory)
    {
        DDCCrossChainData memory args;
        uint256 off = 0;
        (args.crossChainId, off) = ZeroCopySource.NextUint256(values, off);
        (args.from, off) = ZeroCopySource.NextVarBytes(values, off);
        (args.ddcType, off) = ZeroCopySource.NextUint8(values, off);
        (args.signer, off) = ZeroCopySource.NextVarBytes(values, off);
        (args.to, off) = ZeroCopySource.NextVarBytes(values, off);
        (args.ddcId, off) = ZeroCopySource.NextUint256(values, off);
        (args.amount, off) = ZeroCopySource.NextUint256(values, off);
        (args.ddcURI, off) = ZeroCopySource.NextVarBytes(values, off);
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
        address owner = _ddc721Proxy.ownerOf(ddcId);
        require(
            spender == owner ||
                _ddc721Proxy.getApproved(ddcId) == spender ||
                _ddc721Proxy.isApprovedForAll(owner, spender),
            "CrossChainApplied:not owner nor approved"
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
            _ddc1155Proxy.isApprovedForAll(owner, spender) || spender == owner,
            "CrossChainApplied:not owner nor approved"
        );
    }

    /**
     * @dev Check ddcId.
     *
     * Requirements:
     * - `ddcId` is DDC ID.
     */
    function _requireDDCID(uint256 ddcId) private pure {
        require(ddcId > 0, "CrossChainApplied:ddcID must be greater than 0");
    }

    /**
     * @dev Check ddcId.
     *
     * Requirements:
     * - `crossChainID` is the ID returned in the cross-chain event.
     */
    function _requireCrossChainID(uint256 crossChainID) private pure {
        require(
            crossChainID > 0,
            "CrossChainApplied:cross chain ID must be greater than 0"
        );
    }

    /**
     * @dev Check ddcURI.
     *
     * Requirements:
     * - `ddcURI` is a link.
     */
    function _requireDDClength(string memory ddcURI) private pure {
        require(
            bytes(ddcURI).length > 0,
            "CrossChainApplied:ddcURI cannot be empty"
        );
    }

    /**
     * @dev Requires contract address on chain.
     *
     * Requirements:
     * - `account` must not be zero address.
     * - `account` must be a contract.
     */
    function _requireContract(address account) private pure {
        require(account != address(0), "CrossChainApplied:zero address");
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
            "CrossChainApplied:not a operator role or disabled"
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
            "no permission"
        );
    }

    /**
     * @dev Require a effective chainId.
     **/
    function _requireEffectiveChainId(uint64 chainId) private view{
        uint256 index = _getChainIdIndex(chainId);
        if (index > 0) {
            require(
                _chainId[index - 1] == chainId,
                "CrossChainApplied: invalid chainId"
            );
        } else {
            revert("CrossChainApplied: invalid chainId");
        }
    }

    /**
     * @dev Get the index of the chainId list.
     **/
    function _getChainIdIndex(uint64 chainId) private view returns (uint256) {
        return _chainIdIndex[chainId];
    }
}
