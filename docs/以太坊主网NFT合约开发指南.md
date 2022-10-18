# 以太坊主网NFT合约开发指南



> 本开发指南专门针对官方 DDC 跨向以太坊主网的场景，如果您没有此需求可忽略此文。

我们在以太坊主网部署了一个 NFT 合约 **0xEeB4869FBe0FA8C6A1886c3e2B6fE2d01cc891B9**，BSN-DDC 的平台方可以直接使用其作为目标链的合约地址发起跨链操作；平台方也可以拉取我们的 NFT 合约源码在以太坊主网自己部署。如果平台方有自定义 NFT 合约的需求，需按以下要求开发才可成功完成官方 DDC 向以太坊主网的跨链流转。




###  1. 跨链数据结构
自定义 NFT 合约需按如下定义数据结构：

```solidity
struct TxArgs {
	uint256 	crossChainId;	//跨链交易ID
	bytes 		from;			//起始链 DDC ID 的 Owner
	uint8 		ddcType;		//721 或者 1155
	bytes 		signer;  		//跨链数据的签名账户
	bytes 		to;				//以太坊账户地址
	uint256 	ddcId;			//官方 DDC ID
	uint256 	amount; 		//ddcType 为 1155 时的数量；721 则填写 1
  	bytes 		ddcURI; 		//URI内容
 	bytes 		data;			//附加数据
}
```


###  2. 序列化跨链数据
起始链的跨链应用合约内对 `TxArgs` 结构的跨链数据按以下方式序列化为 `bytes` 数据。

**注意：**这里仅为说明序列化过程，NFT721 合约作为目标链的跨链应用合约，不需要定义此函数。


```solidity
function _serializeTxArgs(TxArgs memory args) internal pure returns (bytes memory) {
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
 
```



###  3. 接收跨链数据 

NFT 合约需要定义接收跨链数据的函数，该函数由以太坊主网上的跨链管理合约进行调用。

三个入参分别表示：序列化后的跨链数据、起始链应用合约地址、起始链在中继链上注册的链 ID。


```solidity

function receiveCrossDDC(bytes memory args, bytes memory _fromContractAddr, uint64 _fromChainId) public returns (bool) {
	TxArgs memory txArgs = _deserializeTxArgs(args);
 	require(txArgs.to.length != 0, "toAddress cannot be empty");
 	address toAddress = Utils.bytesToAddress(txArgs.to);
 	_safeMint(toAddress, txArgs.ddcId, txArgs.data);
  	_setTokenURI(txArgs.ddcId, string(txArgs.ddcURI));
  	
  	emit EventReceiveCrossDDC(
   		toAddress,
    	txArgs.ddcId,
     	string(txArgs.ddcURI),
    	_fromContractAddr,
     	_fromChainId
	);
        
	return true;
}

```


###  4. 反序列化跨链数据    
NFT 合约接收到中继链跨链数据后，需要反序列化跨链数据。
    
```solidity    
function _deserializeTxArgs(bytes memory values) internal pure returns (TxArgs memory) {
  	TxArgs memory args;
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
```
