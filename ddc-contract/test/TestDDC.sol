// SPDX-License-Identifier: MIT
// BSN DDC Test Contract v0.0.1 (test/TestDDC.sol)

pragma solidity ^0.8.0;

// import "../interface/Charge/IChargeLogic.sol";

// contract TestDDC {

//     IChargeLogic private iChargeLogic;

//     mapping(string=>uint256) private data;

//     function setChargeLogic(address chargeLogic) external {
//         iChargeLogic = IChargeLogic(chargeLogic);
//     }

//     function setKey(string calldata key,uint256 value) external {

//         iChargeLogic.pay(msg.sender, msg.sig);
//         data[key] = value;
//     }

//     function getKey(string calldata key) external view returns(uint256){

//         return data[key];
//     }

//     function addKey(string calldata key) external {
//         iChargeLogic.pay(msg.sender, msg.sig);
//         data[key] += 1;
//     }



// }