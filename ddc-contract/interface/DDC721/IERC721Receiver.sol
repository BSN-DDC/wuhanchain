// SPDX-License-Identifier: BSN DDC

pragma solidity ^0.8.0;

/**
 * @title DDC721 receiver interface
 * @dev Interface for any contract that wants to support safeTransfers
 * from DDC721 asset contracts.
 */
interface IERC721Receiver {
    /**
     * @dev Whenever an {IDDC721} `ddcId` ddc is transferred to this contract via {IDDC721-safeTransferFrom}
     * by `operator` from `from`, this function is called.
     *
     * It must return its Solidity selector to confirm the ddc transfer.
     * If any other value is returned or the interface is not implemented by the recipient, the transfer will be reverted.
     *
     * The selector can be obtained in Solidity with `IERC721Receiver.onERC721Received.selector`.
     */
    function onERC721Received(
        address operator,
        address from,
        uint256 ddcId,
        bytes calldata data
    ) external returns (bytes4);

     /**
     * @dev Whenever an {IDDC721} `ddcId` ddc is transferred to this contract via {IDDC721-safeBatchTransferFrom}
     * by `operator` from `from`, this function is called.
     *
     * It must return its Solidity selector to confirm the ddc transfer.
     * If any other value is returned or the interface is not implemented by the recipient, the transfer will be reverted.
     *
     * The selector can be obtained in Solidity with `IERC721Receiver.onERC721Received.selector`.
     */
    function onERC721BatchReceived(
        address operator,
        address from,
        uint256[] calldata ddcIds,
        bytes calldata data
    ) external returns (bytes4);
}
