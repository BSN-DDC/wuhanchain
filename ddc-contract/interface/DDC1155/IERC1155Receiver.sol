// SPDX-License-Identifier: BSN DDC

pragma solidity ^0.8.0;

/**
 * @title DDC1155 receiver interface
 * @dev Interface for any contract that wants to support safeTransfers
 * from DDC1155 asset contracts.
 */
interface IERC1155Receiver {
    /**
     * @dev Whenever an {IDDC1155} `ddcId` ddc is transferred to this contract via {IDDC1155-safeTransferFrom}
     * by `operator` from `from`, this function is called.
        @param operator The address which initiated the transfer (i.e. msg.sender)
        @param from The address which previously owned the token
        @param id The ID of the token being transferred
        @param value The amount of tokens being transferred
        @param data Additional data with no specified format
        @return `bytes4(keccak256("onERC1155Received(address,address,uint256,uint256,bytes)"))` if transfer is allowed
    */
    function onERC1155Received(
        address operator,
        address from,
        uint256 id,
        uint256 value,
        bytes calldata data
    ) external returns (bytes4);

    /**
     *  @dev Whenever an {IDDC1155} `ddcId` ddcs is transferred to this contract via {IDDC1155-safeTransferFrom}
     *  by `operator` from `from`, this function is called.
        @param operator The address which initiated the batch transfer (i.e. msg.sender)
        @param from The address which previously owned the token
        @param ids An array containing ids of each token being transferred (order and length must match values array)
        @param values An array containing amounts of each token being transferred (order and length must match ids array)
        @param data Additional data with no specified format
        @return `bytes4(keccak256("onERC1155BatchReceived(address,address,uint256[],uint256[],bytes)"))` if transfer is allowed
    */
    function onERC1155BatchReceived(
        address operator,
        address from,
        uint256[] calldata ids,
        uint256[] calldata values,
        bytes calldata data
    ) external returns (bytes4);
}
