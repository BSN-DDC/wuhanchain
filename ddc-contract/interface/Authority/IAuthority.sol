// SPDX-License-Identifier: BSN DDC

pragma solidity ^0.8.0;

/**
 * @title IAuthority
 * @author ccDown
 * @dev Authority contract - Logical contract interface
 */
interface IAuthority {
    // @dev Store account information.
    struct AccountInfo {
        // @dev The user's private key address.
        string accountDID;
        // @dev The user's account name.
        string accountName;
        // @dev The user's identity role.
        Role accountRole;
        /* @dev The upper-level manager of the user. This value is required only for Consumer.
         * For ordinary users Consumer, this address is the associated platform manager PlatformManager.
         */
        string leaderDID;
        // @dev The current status of the account, only the platform can operate this status.
        State platformState;
        // @dev The current status of the account, only the operator can operate this status.
        State operatorState;
        // @dev Redundant field.
        string field;
    }

    // @dev Store all accessible methods under the DDC contract address.
    struct FuncAcl {
        address contractAddress;
        bytes4[] funcList;
        //note: sig=>index.  index start 1, use 0 to judge whether func exists.
        mapping(bytes4 => uint256) sigIndexList;
    }

    // @dev The identity information corresponding to the DDC account. Value contains: Operator, PlatformManager, Consumer
    enum Role {
        Operator,
        PlatformManager,
        Consumer
    }

    // @dev Freeze:Frozen state, unable to perform DDC related operations; Active: Active state, DDC related operations can be performed.
    enum State {
        Frozen,
        Active
    }

    // @dev Added account method notification event.
    event AddAccount(address indexed caller, address indexed account);

    // @dev Batch add account method notification event
    event AddBatchAccount(address indexed operator, address[] accounts);

    // @dev Update user state method notification event
    event UpdateAccountState(
        address indexed account,
        State platformState,
        State operatorState
    );

    // @dev Add function method to notify event
    event AddFunction(
        address indexed operator,
        Role indexed role,
        address contractAddress,
        bytes4 sig
    );

    // @dev Delete function method to notify event
    event DelFunction(
        address indexed operator,
        Role indexed role,
        address contractAddress,
        bytes4 sig
    );

    // @dev Cross-platform authorization method notification events
    event CrossPlatformApproval(
        address indexed from,
        address indexed to,
        bool approved
    );

    // @dev The platform side adds the chain account switch method notification event.
    event SetSwitcherStateOfPlatform(address indexed operator, bool isOpen);

    // @dev The operator adds the account switch method notification event in batches.
    event SetSwitcherStateOfBatch(address indexed operator,bool isOpen);

    // @dev Synchronize notification events for DID methods.
    event SyncPlatformDID(address indexed operator, string[] dids);

    /**
     * @dev Add operator account information
     * @param operator operator
     * @param accountName accountName
     * @param accountDID accountDID
     **/
    function addOperator(
        address operator,
        string memory accountName,
        string memory accountDID
    ) external;

    /**
     * @dev add consumer(only leader can add it's flower)
     * @param account account
     * @param accountDID accountDID
     **/
    function addAccountByPlatform(
        address account,
        string memory accountName,
        string memory accountDID
    ) external;

    /**
     * @dev add consumers
     *
     * Requirements:
     * - sender is `platformmanger` role.
     * - the switcher of platform is opened.
     * - `accounts` & `accountNames` & `accountDIDs` must have the same length.
     **/
    function addBatchAccountByPlatform(
        address[] memory accounts,
        string[] memory accountNames,
        string[] memory accountDIDs
    ) external;

    /**
     * @dev set the state of switcher on the platform side to control whether some methods can be called.
     * Requirements:
     * - `sender` must be `Operator` role
     * @param isOpen true or false
     **/
    function setSwitcherStateOfPlatform(bool isOpen) external;

    /**
     * @dev Sync the platform dids added earlier to the new validation collection.
     * Requirements:
     * - `sender` must be `Operator` role
     */
    function syncPlatformDID(string[] memory dids) external;

    /**
     * @dev add consumer and platformmanager(only operator can invoke this function)
     * Requirements:
     * - 'platformmanager' role needs to support multiple addresses for one accountDID
     * @param account account
     * @param accountName accountName
     * @param accountDID accountDID
     * @param leaderDID leaderDID
     **/
    function addAccountByOperator(
        address account,
        string memory accountName,
        string memory accountDID,
        string memory leaderDID
    ) external;

    /**
     * @dev add consumers and platformmanagers(only operator can invoke this function)
     *
     * Requirements:
     * - `accounts` & `accountNames` & `accountDIDs` & `leaderDIDs` must have the same length.
     *
     * @param accounts account
     * @param accountNames accountName
     * @param accountDIDs accountDID
     * @param leaderDIDs leaderDID
     **/
    function addBatchAccountByOperator(
        address[] memory accounts,
        string[] memory accountNames,
        string[] memory accountDIDs,
        string[] memory leaderDIDs
    ) external;

    /**
     * @dev Update account state
     * @param account account
     * @param state state
     * @param changePlatformState changePlatformState
     **/
    function updateAccountState(
        address account,
        State state,
        bool changePlatformState
    ) external;

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
    ) external;

    /**
     * @dev Delete function
     * @param role role
     * @param contractAddress contractAddress
     * @param sig sig
     **/
    function delFunction(
        Role role,
        address contractAddress,
        bytes4 sig
    ) external;

    /**
     * @dev Cross-platform approval, platform `from` can transfer DDC to platform `to`
     * Requirements:
     * - sender must be `Operator` role.
     * - both `from` and `to` must be `Platform` roles.
     * - `from` and `to` do not belong to the same platform.
     **/
    function crossPlatformApproval(
        address from,
        address to,
        bool approved
    ) external;

    /**
     * @dev Get account's info
     * @param account account
     **/
    function getAccount(address account)
        external
        view
        returns (
            string memory,
            string memory,
            Role,
            string memory,
            State,
            State,
            string memory
        );

    /**
     * @dev Check whether the account is available
     *
     * Requirements:
     * - The account exists
     * - The account's platformState is active
     * - The account's operatorState is active
     **/
    function accountAvailable(address account) external view returns (bool);

    /**
     * @dev Check whether the account is available
     *
     * Requirements:
     * - The account exists
     * - The account's platformState is active
     * - The account's operatorState is active
     * - The account's role is the require role
     **/
    function checkAvailableAndRole(address account, Role role)
        external
        view
        returns (bool);

    /**
     * @dev Get functions
     * @param role role
     * @param contractAddress contractAddress
     **/
    function getFunctions(Role role, address contractAddress)
        external
        view
        returns (bytes4[] memory);

    /**
     * @dev Check whether the account is available
     *
     * Requirements:
     * - The account exists
     * - The account's platformState is active
     * - The account's operatorState is active
     * - Check whether the contract address and method exist
     **/
    function hasFunctionPermission(
        address account,
        address contractAddress,
        bytes4 sig
    ) external returns (bool);

    /**
     * @dev Check whether the two accounts belong to the same platform
     *
     * Require:
     *  1. Accounts `acc1` and `acc2` exist, and platformState and operatorState are active
     *  2. Check role
     *     a. All are platform roles
     *        - Check if the leaderDID of `acc1` is the same as the leaderDID of `acc2`
     *        - Check if the accountDID of `acc1` is the same as the accountDID of `acc2`
     *     b. `acc1` is the platform, `acc2` is the consumer
     *        - Check if the accountDID of `acc1` is the same as the leaderDID of `acc2`
     *     c. `acc2` is the platform, `acc1` is the consumer
     *        - Check if the accountDID of `acc2` is the same as the leaderDID of `acc1`
     *     d. Both are consumers
     *        - Check if the leaderDID of `acc1` is the same as the leaderDID of `acc2`
     * - 3. returns false
     **/
    function onePlatformCheck(address acc1, address acc2)
        external
        view
        returns (bool);

    /**
     * @dev cross-platform authorization check, check whether `from` can be transferred to `to`
     *
     * Require:
     * - 1. Accounts from and to exist, and platformState and operatorState are active
     * - 2. Check role
     *      a. All are platform roles
     *         - Check if the leaderDID of `from` is the same as the leaderDID of `to`
     *         - Check if the accountDID of `from` and the accountDID of `to` match the authorization
     *      b. `from` is the platform, `to` is the consumer
     *         - Check if the accountDID of `from` and the leaderDID of `to` match the authorization
     *      c. `to` is the platform, `from` is the consumer
     *         - Check if the accountDID of `to` and the leaderDID of `from` match the authorization
     *      d. Both are consumers
     *         - Check if their leaderDID matches the authorization
     * - 3. returns false
     **/
    function crossPlatformCheck(address from, address to)
        external
        view
        returns (bool);

    /**
     * @dev Get the state of platform-side switcher
     **/
    function switcherStateOfPlatform() external view returns (bool);
}
