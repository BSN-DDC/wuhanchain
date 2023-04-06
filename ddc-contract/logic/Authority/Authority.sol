// SPDX-License-Identifier: BSN DDC

pragma solidity ^0.8.0;

import "../../interface/Authority/IAuthority.sol";
import "../../utils/OwnableUpgradeable.sol";
import "../../utils/StringsUpgradeable.sol";
import "../../proxy/utils/UUPSUpgradeable.sol";

/**
 * @title Authority
 * @author kuan
 * @dev Authority contract - Logical contract
 */
contract Authority is IAuthority, OwnableUpgradeable, UUPSUpgradeable {
    using StringsUpgradeable for string;

    // @dev Used to store account information for different roles.
    mapping(address => AccountInfo) private _accountsInfo;

    // @dev List of accessible methods for each logic corresponding to the DDC role.
    mapping(Role => FuncAcl[]) private _funcAclList;

    // note: index start 1, use 0 to judge whether it exists.
    mapping(Role => mapping(address => uint256)) private _contractIndexList;

    // @dev Account DID authorization collection.
    mapping(string => mapping(string => bool)) private _didApprovals;

    // @dev Platform DID collection.
    mapping(string => bool) private _platformDIDs;

    // @dev switcher represents whether some methods can be called by platform.
    bool private _platformSwitcher;

    bool private _enableBatch;

    constructor() initializer {}

    function initialize() public initializer {
        __Ownable_init();
        __UUPSUpgradeable_init();
    }

    /**
     * @dev Function that should revert when `_msgSender()` is not authorized to upgrade the contract. Called by
     * {upgradeTo} and {upgradeToAndCall}.
     *
     * Normally, this function will use an xref:access.adoc[access control] modifier such as {Ownable-onlyOwner}.
     */
    function _authorizeUpgrade(
        address newImplementation
    ) internal override onlyOwner {}

    /**
     * @dev See {IAuthority-addOperator}.
     **/
    function addOperator(
        address operator,
        string memory accountName,
        string memory accountDID
    ) external override onlyOwner {
        require(
            bytes(accountDID).length != 0,
            "Authority: DID cannot be empty!"
        );
        _requireAccountName(accountName);
        _requireNotExist(operator);
        _addAccount(
            operator,
            accountDID,
            accountDID,
            accountName,
            Role.Operator
        );
        emit AddAccount(_msgSender(), operator);
    }

    /**
     * @dev See {IAuthority-addCrossChain}.
     **/
    function addCrossChain(
        address crossChain,
        string memory accountName,
        string memory accountDID
    ) external override onlyOwner {
        require(
            bytes(accountDID).length != 0,
            "Authority: DID cannot be empty!"
        );
        _requireAccountName(accountName);
        _requireNotExist(crossChain);
        _addAccount(
            crossChain,
            accountDID,
            accountDID,
            accountName,
            Role.CrossChain
        );
        emit AddAccount(_msgSender(), crossChain);
    }

    /**
     * @dev See {IAuthority-addAccountByPlatform}.
     **/
    function addAccountByPlatform(
        address account,
        string memory accountName,
        string memory accountDID
    ) external override {
        _requireRole(Role.PlatformManager);
        _requireOpenedSwitcherOfPlatform();
        _requireAccountName(accountName);
        _requireNotExist(account);
        AccountInfo memory leader = _getAccount(_msgSender());
        _addAccount(
            account,
            accountDID,
            leader.accountDID,
            accountName,
            Role.Consumer
        );
        emit AddAccount(_msgSender(), account);
    }

    /**
     * @dev See {IAuthority-addBatchAccountByPlatform}.
     **/
    function addBatchAccountByPlatform(
        address[] memory accounts,
        string[] memory accountNames,
        string[] memory accountDIDs
    ) external override {
        _requireOpenedSwitcherStateOfBatch();
        _requireRole(Role.PlatformManager);
        require(
            accounts.length == accountNames.length &&
                accountNames.length == accountDIDs.length,
            "Authority: length mismatch"
        );
        AccountInfo memory leader = _getAccount(_msgSender());
        for (uint256 i = 0; i < accounts.length; i++) {
            _requireOpenedSwitcherOfPlatform();
            _requireAccountName(accountNames[i]);
            _requireNotExist(accounts[i]);
            _addAccount(
                accounts[i],
                accountDIDs[i],
                leader.accountDID,
                accountNames[i],
                Role.Consumer
            );
        }
        emit AddBatchAccount(_msgSender(), accounts);
    }

    /**
     * @dev See {IAuthority-setSwitcherStateOfPlatform}.
     **/
    function setSwitcherStateOfPlatform(bool isOpen) external override {
        _requireRole(Role.Operator);
        require(
            isOpen != switcherStateOfPlatform(),
            "Authority:invalid operation"
        );
        _platformSwitcher = isOpen;
        emit SetSwitcherStateOfPlatform(_msgSender(), isOpen);
    }

    /**
     * @dev See {IAuthority-syncPlatformDID}.
     **/
    function syncPlatformDID(string[] memory dids) external override {
        _requireRole(Role.Operator);
        for (uint256 i = 0; i < dids.length; i++) {
            require(bytes(dids[i]).length != 0, "Authority:invalid did");
            _platformDIDs[dids[i]] = true;
        }
        emit SyncPlatformDID(_msgSender(), dids);
    }

    /**
     * @dev See {IAuthority-addAccountByOperator}.
     **/
    function addAccountByOperator(
        address account,
        string memory accountName,
        string memory accountDID,
        string memory leaderDID
    ) external override {
        _requireRole(Role.Operator);
        _addAccountByOperator(account, accountName, accountDID, leaderDID);
        emit AddAccount(_msgSender(), account);
    }

    /**
     * @dev See {IAuthority-addBatchAccountByOperator}.
     **/
    function addBatchAccountByOperator(
        address[] memory accounts,
        string[] memory accountNames,
        string[] memory accountDIDs,
        string[] memory leaderDIDs
    ) external override {
        _requireOpenedSwitcherStateOfBatch();
        _requireRole(Role.Operator);
        require(
            accounts.length == accountNames.length &&
                accountNames.length == accountDIDs.length &&
                accountDIDs.length == leaderDIDs.length,
            "Authority: length mismatch"
        );
        for (uint256 i = 0; i < accounts.length; i++) {
            _addAccountByOperator(
                accounts[i],
                accountNames[i],
                accountDIDs[i],
                leaderDIDs[i]
            );
        }
        emit AddBatchAccount(_msgSender(), accounts);
    }

    /**
     * @dev See {IAuthority-updateAccountState}.
     **/
    function updateAccountState(
        address account,
        State state,
        bool changePlatformState
    ) external override {
        (
            AccountInfo memory accountInfo,
            AccountInfo memory leaderInfo
        ) = _updateAccountCheck(account);
        // - Verify the account is active
        _requireAccountActive(leaderInfo);
        // - only leader or operator can call this function
        require(
            (_leaderCheckByAccountInfo(accountInfo, leaderInfo) ||
                leaderInfo.accountRole == Role.Operator),
            "Authority: Account's role does not match"
        );
        require(
            leaderInfo.accountRole != Role.Consumer,
            "Authority: wrong role"
        );
        // - Verify that the accountInfo account status is operable by the caller.
        if (leaderInfo.accountRole == Role.Operator) {
            if (changePlatformState) {
                require(
                    accountInfo.platformState != state,
                    "Authority: PlatformState doesn't need to change"
                );
                accountInfo.platformState = state;
            } else {
                require(
                    accountInfo.operatorState != state,
                    "Authority: OperatorState doesn't need to change"
                );
                accountInfo.operatorState = state;
            }
        } else if (leaderInfo.accountRole == Role.PlatformManager) {
            require(
                accountInfo.platformState != state,
                "Authority: PlatformState doesn't need to change"
            );
            accountInfo.platformState = state;
        }
        _accountsInfo[account] = accountInfo;
        emit IAuthority.UpdateAccountState(
            account,
            accountInfo.platformState,
            accountInfo.operatorState
        );
    }

    /**
     * @dev See {IAuthority-getAccount}.
     **/
    function getAccount(
        address account
    )
        public
        view
        override
        returns (
            string memory,
            string memory,
            Role,
            string memory,
            State,
            State,
            string memory
        )
    {
        AccountInfo memory accountInfo = _accountsInfo[account];
        return (
            accountInfo.accountDID,
            accountInfo.accountName,
            accountInfo.accountRole,
            accountInfo.leaderDID,
            accountInfo.platformState,
            accountInfo.operatorState,
            accountInfo.field
        );
    }

    /**
     * @dev See {IAuthority-checkAvailableAndRole}.
     **/
    function checkAvailableAndRole(
        address account,
        Role role
    ) public view override returns (bool) {
        // - Verify that the account exists
        AccountInfo memory accountInfo = _getAccount(account);
        _requireAccountExists(accountInfo.accountName);
        _requireAccountActive(accountInfo);
        return uint8(accountInfo.accountRole) == uint8(role);
    }

    /**
     * @dev See {IAuthority-accountAvailable}.
     **/
    function accountAvailable(
        address account
    ) public view override returns (bool) {
        // - Verify that the account exists
        AccountInfo memory accountInfo = _getAccount(account);
        _requireAccountExists(accountInfo.accountName);
        _requireAccountActive(accountInfo);
        return true;
    }

    /**
     * @dev Add function
     * @param role role
     * @param contractAddress contractAddress
     * @param sig sig
     **/
    function addFunction(
        Role role,
        address contractAddress,
        bytes4 sig
    ) external override {
        _requireRole(Role.Operator);
        _requireNotZeroAddress(contractAddress);
        _requireValidSig(sig);
        FuncAcl storage funcAcl;
        uint256 contractIndex = _contractIndexList[role][contractAddress];
        //note: 0 indicates that it does not exist
        if (contractIndex == 0) {
            uint256 idx = _funcAclList[role].length;
            _funcAclList[role].push();
            funcAcl = _funcAclList[role][idx];
            _contractIndexList[role][contractAddress] = _funcAclList[role]
                .length;
            funcAcl.contractAddress = contractAddress;
            funcAcl.funcList.push(sig);
            funcAcl.sigIndexList[sig] = funcAcl.funcList.length;
        } else {
            funcAcl = _funcAclList[role][contractIndex - 1];
            require(
                funcAcl.sigIndexList[sig] == 0,
                "Authority: func already exists"
            );
            funcAcl.funcList.push(sig);
            funcAcl.sigIndexList[sig] = funcAcl.funcList.length;
        }
        emit AddFunction(_msgSender(), role, contractAddress, sig);
    }

    /**
     * @dev See {IAuthority-delFunction}.
     **/
    function delFunction(
        Role role,
        address contractAddress,
        bytes4 sig
    ) external override {
        _requireRole(Role.Operator);
        _requireNotZeroAddress(contractAddress);
        _requireValidSig(sig);
        FuncAcl storage funcAcl = _funcAclList[role][
            _getContractIndexOfRole(role, contractAddress)
        ];
        uint256 sigIndex = funcAcl.sigIndexList[sig];
        require(sigIndex > 0, "Authority:func does not exists");
        funcAcl.funcList[sigIndex - 1] = bytes4(0);
        delete funcAcl.sigIndexList[sig];
        emit DelFunction(_msgSender(), role, contractAddress, sig);
    }

    /**
     * @dev See {IAuthority-crossPlatformApproval}.
     **/
    function crossPlatformApproval(
        address from,
        address to,
        bool approved
    ) external override {
        _requireRole(Role.Operator);
        _requireNotZeroAddress(from);
        _requireNotZeroAddress(to);

        AccountInfo memory fromInfo = _getAccount(from);
        _requireAccountExists(fromInfo.accountName);
        _requireAccountActive(fromInfo);

        AccountInfo memory toInfo = _getAccount(to);
        _requireAccountExists(toInfo.accountName);
        _requireAccountActive(toInfo);

        require(
            fromInfo.accountRole == Role.PlatformManager &&
                toInfo.accountRole == Role.PlatformManager,
            "Authority:Both must be `platform` roles"
        );
        require(
            !fromInfo.accountDID.equal(toInfo.accountDID),
            "Authority:Both are the same platform account"
        );

        _didApprovals[fromInfo.accountDID][toInfo.accountDID] = approved;
        emit CrossPlatformApproval(from, to, approved);
    }

    /**
     * @dev See {IAuthority-getFunctions}.
     **/
    function getFunctions(
        Role role,
        address contractAddress
    ) public view override returns (bytes4[] memory) {
        _requireNotZeroAddress(contractAddress);
        return
            _funcAclList[role][_getContractIndexOfRole(role, contractAddress)]
                .funcList;
    }

    /**
     * @dev See {IAuthority-hasFunctionPermission}.
     **/
    function hasFunctionPermission(
        address account,
        address contractAddress,
        bytes4 sig
    ) public view override returns (bool) {
        AccountInfo memory accountInfo = _getAccount(account);
        _requireAccountExists(accountInfo.accountName);
        _requireAccountActive(accountInfo);
        _requireNotZeroAddress(contractAddress);
        _requireValidSig(sig);
        uint256 contractIdx = _getContractIndexOfRole(
            accountInfo.accountRole,
            contractAddress
        );
        return
            _funcAclList[accountInfo.accountRole][contractIdx].sigIndexList[
                sig
            ] > 0;
    }

    /**
     * @dev See {IAuthority-onePlatformCheck}.
     **/
    function onePlatformCheck(
        address acc1,
        address acc2
    ) public view override returns (bool) {
        // 1. Accounts `acc1` and `acc2` exist, and platformState and operatorState are active
        AccountInfo memory acc1Info = _getAccount(acc1);
        _requireAccountExists(acc1Info.accountName);
        _requireAccountActive(acc1Info);
        AccountInfo memory acc2Info = _getAccount(acc2);
        _requireAccountExists(acc2Info.accountName);
        _requireAccountActive(acc2Info);

        // 2. Check role
        // a. All are platform roles
        if (
            acc1Info.accountRole == Role.PlatformManager &&
            acc2Info.accountRole == Role.PlatformManager
        ) {
            return (acc1Info.leaderDID.equal(acc2Info.leaderDID) &&
                acc1Info.accountDID.equal(acc2Info.accountDID));
        }
        //  b. `acc1` is the platform, `acc2` is the consumer
        else if (
            acc1Info.accountRole == Role.PlatformManager &&
            acc2Info.accountRole == Role.Consumer
        ) {
            return acc1Info.accountDID.equal(acc2Info.leaderDID);
        }
        //   c. `acc2` is the platform, `acc1` is the consumer
        else if (
            acc1Info.accountRole == Role.Consumer &&
            acc2Info.accountRole == Role.PlatformManager
        ) {
            return acc2Info.accountDID.equal(acc1Info.leaderDID);
        }
        // d. Both are consumers
        else if (
            acc1Info.accountRole == Role.Consumer &&
            acc2Info.accountRole == Role.Consumer
        ) {
            return acc1Info.leaderDID.equal(acc2Info.leaderDID);
        } else {
            //- 3. return false
            return false;
        }
    }

    /**
     * @dev See {IAuthority-crossPlatformCheck}.
     **/
    function crossPlatformCheck(
        address from,
        address to
    ) public view override returns (bool) {
        // 1. Accounts `from` and `to` exist, and platformState and operatorState are active
        AccountInfo memory fromInfo = _getAccount(from);
        _requireAccountExists(fromInfo.accountName);
        _requireAccountActive(fromInfo);
        AccountInfo memory toInfo = _getAccount(to);
        _requireAccountExists(toInfo.accountName);
        _requireAccountActive(toInfo);

        // 2. Check role
        // a. All are platform roles
        if (
            fromInfo.accountRole == Role.PlatformManager &&
            toInfo.accountRole == Role.PlatformManager
        ) {
            return (fromInfo.leaderDID.equal(toInfo.leaderDID) &&
                _didApprovals[fromInfo.accountDID][toInfo.accountDID]);
        }
        //  b. `from` is the platform, `to` is the consumer
        else if (
            fromInfo.accountRole == Role.PlatformManager &&
            toInfo.accountRole == Role.Consumer
        ) {
            return _didApprovals[fromInfo.accountDID][toInfo.leaderDID];
        }
        //   c. `to` is the platform, `from` is the consumer
        else if (
            fromInfo.accountRole == Role.Consumer &&
            toInfo.accountRole == Role.PlatformManager
        ) {
            return _didApprovals[fromInfo.leaderDID][toInfo.accountDID];
        }
        // d. Both are consumers
        else if (
            fromInfo.accountRole == Role.Consumer &&
            toInfo.accountRole == Role.Consumer
        ) {
            return _didApprovals[fromInfo.leaderDID][toInfo.leaderDID];
        } else {
            //- 3. return false
            return false;
        }
    }

    /**
     * @dev See {IAuthority-switcherStateOfPlatform}.
     **/
    function switcherStateOfPlatform() public view override returns (bool) {
        return _platformSwitcher;
    }

    /**
     * @dev See {IAuthority-setSwitcherStateOfBatch}.
     **/
    function setSwitcherStateOfBatch(bool isOpen) public {
        _requireRole(Role.Operator);
        require(isOpen != _enableBatch, "Authority:invalid operation");
        _enableBatch = isOpen;
        emit SetSwitcherStateOfBatch(_msgSender(), isOpen);
    }

    /**
     * @dev get real contract index of _funcAclList
     **/
    function _getContractIndexOfRole(
        Role accRole,
        address contractAddress
    ) private view returns (uint256) {
        uint256 idx = _contractIndexList[accRole][contractAddress];
        require(idx > 0, "Authority:`role` or `contractAddress` doesn't exist");
        return idx - 1;
    }

    /**
     * @dev Require both operatorState and platformState to be active
     **/
    function _isActive(
        State operatorState,
        State platformState
    ) private pure returns (bool) {
        return (operatorState == State.Active && platformState == State.Active);
    }

    /**
     * @dev add account by operator
     **/
    function _addAccountByOperator(
        address account,
        string memory accountName,
        string memory accountDID,
        string memory leaderDID
    ) private {
        _requireAccountName(accountName);
        _requireNotExist(account);
        Role role = Role.Consumer;
        if (bytes(leaderDID).length == 0) {
            // PlatformManager
            require(
                bytes(accountDID).length != 0,
                "Authority: accountDID cannot be empty"
            );
            AccountInfo memory leader = _getAccount(_msgSender());
            leaderDID = leader.accountDID;
            role = Role.PlatformManager;
            _platformDIDs[accountDID] = true;
        } else {
            // Consumer
            require(_platformDIDs[leaderDID], "Authority:invalid leaderDID");
        }
        _addAccount(account, accountDID, leaderDID, accountName, role);
    }

    /**
     * @dev add account by params
     **/
    function _addAccount(
        address account,
        string memory accountDID,
        string memory leaderAccountDID,
        string memory accountName,
        Role accountRole
    ) private {
        _accountsInfo[account] = AccountInfo({
            accountDID: accountDID,
            leaderDID: leaderAccountDID,
            accountName: accountName,
            platformState: State.Active,
            operatorState: State.Active,
            accountRole: accountRole,
            field: ""
        });
    }

    /**
     * @dev check whether the account exits
     **/
    function _requireAccountExists(string memory accountName) private pure {
        require(
            bytes(accountName).length != 0,
            "Authority: Account does not exist!"
        );
    }

    /**
     * @dev Get account's info
     **/
    function _getAccount(
        address account
    ) private view returns (AccountInfo memory) {
        _requireNotZeroAddress(account);
        return _accountsInfo[account];
    }

    function _leaderCheckByAccountInfo(
        AccountInfo memory lowerLevelInfo,
        AccountInfo memory higherLevelInfo
    ) private pure returns (bool) {
        return lowerLevelInfo.leaderDID.equal(higherLevelInfo.accountDID);
    }

    function _updateAccountCheck(
        address account
    ) private view returns (AccountInfo memory, AccountInfo memory) {
        // - A data contract is invoked to verify that the account already exists, or false is returned.
        AccountInfo memory accountInfo = _getAccount(account);
        require(
            bytes(accountInfo.accountName).length != 0,
            "Authority: Account does not exist!"
        );
        // - Check whether the superior of the accountInfo account is the caller. If no, false is returned.
        AccountInfo memory leaderAccountInfo = _getAccount(_msgSender());
        require(
            bytes(leaderAccountInfo.accountName).length != 0,
            "Authority: LeaderAccountInfo does not exist!"
        );
        return (accountInfo, leaderAccountInfo);
    }

    function _requireAccountActive(AccountInfo memory info) private pure {
        require(
            _isActive(info.operatorState, info.platformState),
            "Authority: Account has been frozen!"
        );
    }

    function _requireAccountName(string memory accountName) private pure {
        require(
            bytes(accountName).length != 0,
            "Authority: AccountName cannot be empty!"
        );
    }

    function _requireNotExist(address account) private view {
        require(
            bytes(_getAccount(account).accountName).length == 0,
            "Authority: Account already exists!"
        );
    }

    function _requireNotZeroAddress(address addr) private pure {
        require(addr != address(0), "Authority:zero address");
    }

    function _requireValidSig(bytes4 sig) private pure {
        require(sig != bytes4(0), "Authority:invalid sig");
    }

    /**
     * @dev Requires a matched role.
     *
     * Requirements:
     * - `sender` must be a available `ddc` account.
     * - `sender` must be the specified role.
     */
    function _requireRole(Role role) private view {
        require(
            Authority.checkAvailableAndRole(_msgSender(), role),
            "Authority: incorrect role or disabled"
        );
    }

    /**
     * @dev Requires the switcher of platform is opened.
     */
    function _requireOpenedSwitcherOfPlatform() private view {
        require(
            Authority.switcherStateOfPlatform(),
            "Authority:switcher of platform is closed"
        );
    }

    /**
     * @dev Requires the switcher of platform is opened.
     */
    function _requireOpenedSwitcherStateOfBatch() private view {
        require(_enableBatch, "Authority:switcher of batch is closed");
    }
}
