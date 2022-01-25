// SPDX-License-Identifier: MIT

pragma solidity ^0.8.0;

/**
 * @dev String operations.
 */
library StringsUpgradeable {
    bytes16 private constant _HEX_SYMBOLS = "0123456789abcdef";

    /**
     * @dev Converts a `uint256` to its ASCII `string` decimal representation.
     */
    function toString(uint256 value) internal pure returns (string memory) {
        // Inspired by OraclizeAPI's implementation - MIT licence
        // https://github.com/oraclize/ethereum-api/blob/b42146b063c7d6ee1358846c198246239e9360e8/oraclizeAPI_0.4.25.sol

        if (value == 0) {
            return "0";
        }
        uint256 temp = value;
        uint256 digits;
        while (temp != 0) {
            digits++;
            temp /= 10;
        }
        bytes memory buffer = new bytes(digits);
        while (value != 0) {
            digits -= 1;
            buffer[digits] = bytes1(uint8(48 + uint256(value % 10)));
            value /= 10;
        }
        return string(buffer);
    }

    /**
     * @dev Converts a `uint256` to its ASCII `string` hexadecimal representation.
     */
    function toHexString(uint256 value) internal pure returns (string memory) {
        if (value == 0) {
            return "0x00";
        }
        uint256 temp = value;
        uint256 length = 0;
        while (temp != 0) {
            length++;
            temp >>= 8;
        }
        return toHexString(value, length);
    }

    /**
     * @dev Converts a `uint256` to its ASCII `string` hexadecimal representation with fixed length.
     */
    function toHexString(uint256 value, uint256 length)
        internal
        pure
        returns (string memory)
    {
        bytes memory buffer = new bytes(2 * length + 2);
        buffer[0] = "0";
        buffer[1] = "x";
        for (uint256 i = 2 * length + 1; i > 1; --i) {
            buffer[i] = _HEX_SYMBOLS[value & 0xf];
            value >>= 4;
        }
        require(value == 0, "Strings: hex length insufficient");
        return string(buffer);
    }

    function equal(string memory self, string memory other)
        internal
        pure
        returns (bool)
    {
        bytes memory self_rep = bytes(self);
        bytes memory other_rep = bytes(other);

        if (self_rep.length != other_rep.length) {
            return false;
        }
        uint256 selfLen = self_rep.length;
        for (uint256 i = 0; i < selfLen; i++) {
            if (self_rep[i] != other_rep[i]) return false;
        }
        return true;
    }

    function bytes4Tostring(bytes4 _newname)
        public
        pure
        returns (string memory)
    {
        uint256 count = 0;

        for (uint256 i = 0; i < _newname.length; i++) {
            bytes1 char = _newname[i];
            if (char != 0) {
                count++;
            }
        }

        bytes memory newname = new bytes(count);

        for (uint256 j = 0; j < count; j++) {
            newname[j] = _newname[j];
        }

        return string(newname);
    }
}
