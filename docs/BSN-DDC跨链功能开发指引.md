### BSN-DDC跨链功能开发指引

> 本文以武汉链为例，介绍官方 ERC721 DDC 跨向以太坊主网的详细步骤，如果您没有此需求可忽略此文。


#### 参数信息

- 武汉链在 BSN 中继链上的侧链 ID：`1003650780676003`
- 武汉链跨链管理合约地址：`0x52ef671540086AEc1774aFbe26E0DB9061aeCBE2`
- 以太坊主网在 BSN 中继链上的侧链 ID：12305
- 以太坊主网跨链管理合约地址：`0x49f264851241694f80EACF616AA888eEAE5FB7E4`
- 以太坊主网 NFT 合约地址：`0xEeB4869FBe0FA8C6A1886c3e2B6fE2d01cc891B9 `



#### 1.更新本地仓库

- 请拉取仓库 `wuhanchain` 最新版本的代码及文档。

 `https://github.com/BSN-DDC/wuhanchain.git`

- 请拉取仓库 `docs` 最新版本的 "BSN-DDC 门户 OpenAPI 接口文档.pdf"。

  `https://github.com/BSN-DDC/docs.git`
  
#### 2.查看支持的跨链框架
您需通过官方门户 OpenAPI 里面的"跨链管理"->"查询跨链框架信息"接口，查验目前支持的官方 DDC 跨链的框架信息。如您的业务平台所选框架不在此信息内，敬请关注后续 BSN-DDC 的迭代公告。

#### 3.更新 DDC SDK

您需在业务系统内，适配新版本的 ddc-sdk。


#### 4.调用跨链方法
您需调用 ddc-sdk 里的 `crossChainTransfer` 方法发起跨链，调用示例代码：

``` java
CrossChainTransferParams params = CrossChainTransferParams.builder()
	.setSender("0x6da7e501dc26d8aa0d5a8bdec6deecd0c5f18343")   // 武汉链上的签名账户地址
	.setTo("0x6922D8af46d5e39c2a15cAa26eE692FCc118aDc5")       // 以太坊上的接收者账户地址
	.setDdcId(BigInteger.valueOf(9231))                        // DDC唯一标识
	.setData("additional data".getBytes())                     // 附加数据
	.setDDCType(DDCTypeEnum.ERC721)                            // DDC类型
	.setToChainID(BigInteger.valueOf(12305))                   // 以太坊主网在中继链上的侧链ID
	.setToCCAddr("0xEeB4869FBe0FA8C6A1886c3e2B6fE2d01cc891B9") // 以太坊 NFT 合约地址
	.setSigner("0xB476114385cB25DDB3E6d5EE74b9DC1011e28805")   // 以太坊签名账户地址
	.setFuncName("crossChainMint")                             // 以太坊 NFT 合约方法
	.build();
	
// 调用SDK方法发起跨链交易
String txHash = ddcSdkClient.crossChainService.crossChainTransfer(params);
log.info(txHash);
Thread.sleep(10000);

// 查询跨链交易事件
CrossChainTransferEventBean result = ddcSdkClient.crossChainService.getCrossChainTransferEvent(txHash);
log.info(JSON.toJSONString(result));
```

**请注意：**

- 以太坊主网在 BSN 中继链上的侧链 ID 为 12305，此为固定值不会发生变更。
- 我们在以太坊主网部署的 NFT 合约地址为 `0xEeB4869FBe0FA8C6A1886c3e2B6fE2d01cc891B9`，您可直接使用。如果您有自己部署的需求，请从`https://github.com/BSN-DDC/wuhanchain.git`拉取合约代码。如果您有自定义合约的需求，请参见[以太坊主网 NFT721 合约开发指南](更新github地址！！！！) 。



#### 5.获取跨链服务 Token
发起跨链交易后，您需要访问以太坊跨链服务，请参见[BSN-DDC跨链服务OpenAPI](更新github地址！！！！) 的【**获取Token**】接口，得到`session_id`。

#### 6.查询跨链状态
得到有效的 Token 后，结合跨链交易的签名账户（即sdk里的`setSender`），继续对接【**查询跨链状态**】接口，确保可以查到待签名的跨链数据。

#### 7.查询跨链数据
继续对接【**查询跨链数据**】接口，得到待签名的数据。

**请注意：**

该接口仅可查询待签名的跨链数据。

#### 8.对跨链数据进行签名
跨链数据的数据结构如下，其定义详情请参见`github.com/ethereum/go-ethereum/core/types`

```go
type DynamicFeeTx struct {
	ChainID    *big.Int
	Nonce      uint64
	GasTipCap  *big.Int // a.k.a. maxPriorityFeePerGas
	GasFeeCap  *big.Int // a.k.a. maxFeePerGas
	Gas        uint64
	To         *common.Address `rlp:"nil"` // nil means contract creation
	Value      *big.Int
	Data       []byte
	AccessList AccessList

	// Signature values
	V *big.Int `json:"v" gencodec:"required"`
	R *big.Int `json:"r" gencodec:"required"`
	S *big.Int `json:"s" gencodec:"required"`
}
```

**请注意：**

- `To`表示以太坊上的跨链管理合约地址，此值如发生变更，BSN-DDC将会提前在迭代公告内说明 `0x49f264851241694f80EACF616AA888eEAE5FB7E4`。
- 具体签名过程，请参见[示例代码](更新demo的github地址)。

#### 9.发送签名交易
完成签名后，继续对接[BSN-DDC跨链服务OpenAPI](更新github地址！！！！) 的【**发送交易到以太坊**】接口，向以太坊主网提交目标链交易。

**请注意:** 

- 如果您不通过此接口向以太坊主网发起交易，那么以太坊上交易成功，则 BSN-DDC 跨链服务也可识别，但是如果以太坊上交易失败，那么跨链服务无法识别，该笔跨链数据的跨链状态仍为“跨链中”，所以不会向您发起跨链业务费的退款。

- 如遇没有退跨链业务费的问题，您可以继续通过此接口再次向以太坊提交交易，BSN-DDC 跨链服务将自动识别并更新跨链状态，如跨链失败则向您发起跨链业务费的退款。



