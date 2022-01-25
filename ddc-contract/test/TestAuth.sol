// SPDX-License-Identifier: MIT
// BSN DDC Test Contract v0.0.1 (test/TestAuth.sol)

pragma solidity ^0.8.0;

// import "../interface/Authority/IAuthorityLogic.sol";
// import "../interface/Authority/IAuthorityData.sol";

// abstract contract TestAuth is IAuthorityLogic {


//     mapping(address=> bool) private testData;

//     function setTestData(address addr, bool value) external {
//         testData[addr] = value ;
//     }

//       /**
//      * @dev Get account's info
//      * @param account account
//      **/
//     function getAccount(address account) external view override returns ( string memory,
//             string memory,
//             IAuthorityData.Role,
//             string memory,
//             IAuthorityData.State,
//             IAuthorityData.State,
//             string memory) {

//     }

//     /**
//      * @dev Determine whether the leaders of two accounts are the same
//      * 
//      * Requirements:
//      * - The account1 exists
//      * - The account2 exists
//      * - The account1's leaderDID is the same as account2's
//      **/
//     function sameLeaderCheck(address account1, address account2) external override view returns (bool) {
        
//         return testData[account1] || testData[account2] ;
//     }

//     /**
//      * @dev Verify that the two accounts are the same platformManager
//      * 
//      * Requirements:
//      * - The account1 exists
//      * - The account2 exists
//      * - The account1's DID is the same as account2's
//      * - The account1's role and account2's are the same as platformManager
//      **/
//     function samePlatformManagerCheck(address account1,address account2) external override view returns (bool){
//         return testData[account1] || testData[account2] ;
//     }

//     /**
//      * @dev Verify that the account's leader is leaderAccount
//      * 
//      * Requirements:
//      * - The account exists
//      * - The leaderAccount exists
//      * - The account's leaderDID is the same as leaderAccount's accountDID
//      **/
//     function leaderCheck(address account, address leaderAccount) external override view returns (bool){
//         return testData[account] || testData[leaderAccount] ;
//     }

//     /**
//      * @dev Check whether the role of the account is the same as that of the _role
//      * 
//      * Requirements:
//      * - The account exists
//      * - The account's role is the same as `role`
//      **/
//     function assertAccountRole(address account, Role role) external override view returns (bool) {
//          return testData[account] && role == Role.Operator ;
//     }

//      /**
//      * @dev Verify that the two accounts are the same platformManager
//      * 
//      * Requirements:
//      * - The account1 exists
//      * - The account2 exists
//      * - The account1's DID is the same as account2's
//      **/
//     function sameDIDCheck(address account1,address account2) external override view returns (bool) {
//         return testData[account1] || testData[account2];
//     }

//     /**
//      * @dev Check whether the account is available
//      * 
//      * Requirements: 
//      * - The account exists
//      * - The account's platformState is active
//      * - The account's operatorState is active
//      **/
//     function accountAvailable(address account) external override view returns (bool){
//          return testData[account] ;
//     }

//     /**
//      * @dev Check whether the account is available
//      * 
//      * Requirements:
//      * - The account exists
//      * - The account's platformState is active
//      * - The account's operatorState is active 
//      * - Check whether the contract address and method exist
//      **/
//     function hasFunctionPermission(address account,address contractAddress,bytes4 sig) external override view returns (bool){

//          return testData[account] || testData[contractAddress] || msg.sig == sig;
//     }

// }