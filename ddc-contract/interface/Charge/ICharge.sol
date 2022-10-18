// SPDX-License-Identifier: BSN DDC

pragma solidity ^0.8.0;

interface ICharge {
    /// @dev  event for recharge
    event Recharge(address indexed from, address indexed to, uint256 amount);

    event RechargeBatch(
        address indexed from,
        address[] toList,
        uint256[] amounts
    );

    /**
     * @dev event for settlement
     * @param  accAddr account address for settlement
     * @param  ddcAddr DDC contract address for settlement
     * @param  amount Amount settled
     */
    event Settlement(
        address indexed accAddr,
        address indexed ddcAddr,
        uint256 amount
    );

    /**
     * @dev event for set contract fee
     * @param ddcAddr DDC contract address
     * @param sig DDC contract function sig
     * @param amount call price
     */
    event SetFee(address indexed ddcAddr, bytes4 sig, uint32 amount);

    /// @dev event for deleteFee
    event DelFee(address indexed ddcAddr, bytes4 sig);

    /// @dev event for deleteDDC
    event DelDDC(address indexed ddcAddr);

    /**
     * @dev  event for ddc pay
     *
     * Requirements:
     * - ``
     * - ``
     * @param  payer caller of DDC master contract
     * @param payee ddc contract address
     * @param sig function sig of DDC master contract called
     * @param amount deduction fee
     * @param ddcId unique identifier of the DDC
     */
    event Pay(
        address indexed payer,
        address indexed payee,
        bytes4 sig,
        uint32 amount,
        uint256 ddcId
    );

    event SetSwitcherStateOfBatch(address indexed operator,bool isOpen);

    /**
     * @dev  Sets authority proxy address.
     *
     * Requirements:
     * - sender must be the owner only.
     */
    function setAuthorityProxyAddress(address authorityProxyAddress) external;

    /**
     * @dev  Recharge DDC fee to the specified account.
     *
     * Requirements:
     * - `sender` and `toList` are both active.
     * - `sender` is Operator role or is the leader of each account from `toList` or is same role with each account from `toList` but they must not be consumers.
     * - `sender` balance must be greater than or equal to the amount recharged.
     */
    function recharge(address to, uint256 amount) external;

    /**
     * @dev Batch recharge DDC fee to the specified accounts.
     *
     * Requirements:
     * - `sender` and `toList` are both active.
     * - `sender` is Operator role or is the leader of each account from `toList` or is same role with each account from `toList` but they must not be consumers.
     * - `sender` balance must be greater than or equal to the amount recharged.
     */
    function rechargeBatch(address[] memory toList, uint256[] memory amounts)
        external;

    /**
     * @dev  add DDC fee to operator's account.
     *
     * Requirements:
     * - `sender` must be `Operator` role.
     * - ``
     * @param amount Increased amount
     */
    function selfRecharge(uint256 amount) external;

    /**
     * @dev  Call this method within the authorized contract to deduct fees from the caller of the DDC master contract
     *
     * Requirements:
     * - Must be called within an authorized DDC contract
     * - `from` account must have an expense greater than the call price
     *
     * @param payer caller of DDC master contract
     * @param sig function sig of DDC master contract called
     * @param ddcId unique identifier of the DDC
     */
    function pay(
        address payer,
        bytes4 sig,
        uint256 ddcId
    ) external;

    /**
     * @dev Settle the DDC contract account
     *
     * Requirements:
     * - the role of sender must be Operator
     * - ``
     * @param  ddcAddr DDC contract address for settlement
     * @param  amount Amount settled
     *
     */
    function settlement(address ddcAddr, uint256 amount) external;

    /**
     * @dev  set DDC contract function fee
     *
     * Requirements:
     * - If the setting is successful, mark the set DDC contract as the authorized DDC master contract and trigger an event
     * - ``
     * @param ddcAddr DDC contract address
     * @param sig DDC contract function sig
     * @param amount fee for call this function
     *
     */
    function setFee(
        address ddcAddr,
        bytes4 sig,
        uint32 amount
    ) external;

    /**
     * @dev  delete DDC contract function fee
     *
     * Requirements:
     * - Even deleting all charging rules of a DDC contract does not mean that the DDC contract is no longer authorized
     * - ``
     * @param ddcAddr DDC contract address
     * @param sig DDC contract function sig
     */
    function delFee(address ddcAddr, bytes4 sig) external;

    /**
     * @dev Delete DDC contract authorization
     *
     * Requirements:
     * - If the DDC contract authorization is deleted, all charging rules of the DDC contract will be deleted
     * - ``
     * @param ddcAddr DDC contract address
     *
     */
    function delDDC(address ddcAddr) external;

    /**
     * @dev  Returns the DDC fee balance for the specified account.
     *
     */
    function balanceOf(address accAddr) external view returns (uint256);

    /**
     * @dev  Returns the DDC fee balance for all accounts.
     *
     */
    function balanceOfBatch(address[] memory accAddrs)
        external
        view
        returns (uint256[] memory);

    /**
     * @dev query the usage fee of DDC contract
     *
     * Requirements:
     * - ``
     * - ``
     * @param ddcAddr DDC contract address
     * @param sig DDC contract function sig
     *
     * @return  the usage fee of DDC contract function
     */
    function queryFee(address ddcAddr, bytes4 sig)
        external
        view
        returns (uint32);

    /**
     * @dev query total DDC fee.
     */
    function totalSupply() external view returns (uint256);

    function setSwitcherStateOfBatch(bool isOpen) external;
}
