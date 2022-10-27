### BSN-DDC跨链功能开发指引

> 本文以武汉链为例，介绍官方 ERC721 DDC 跨向以太坊主网的详细步骤，如果您没有此需求可忽略此文。


#### 参数信息

- 以太坊主网侧链 ID：`12305`

- 武汉链侧链 ID：`1003650780676003`

- 武汉链跨链管理合约地址：`0x52ef671540086AEc1774aFbe26E0DB9061aeCBE2`

- 以太坊主网跨链管理合约地址：`0x49f264851241694f80EACF616AA888eEAE5FB7E4`

- 以太坊主网 NFT 合约地址：`0xEeB4869FBe0FA8C6A1886c3e2B6fE2d01cc891B9`

- 以太坊主网 ERC721 合约地址：`0xB476114385cB25DDB3E6d5EE74b9DC1011e28805`

**请注意：** 以太坊主网上，跨链管理合约直接调用 NFT 合约，NFT 合约内部调用 ERC721 合约。


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

- BSN 体系内以太坊主网的侧链 ID 为固定值不会发生变更。

- 如果您有自己部署 NFT 合约的需求，请从`https://github.com/BSN-DDC/wuhanchain.git`拉取合约代码进行部署，部署成本约 10 元人民币（以太币 1300 美元计算），具体费用与以太坊主网环境及以太币有关。

- 如果您有自定义 NFT 合约的需求，请参见[以太坊主网 NFT 合约开发指南](https://github.com/BSN-DDC/wuhanchain/blob/master/docs/%E4%BB%A5%E5%A4%AA%E5%9D%8A%E4%B8%BB%E7%BD%91NFT%E5%90%88%E7%BA%A6%E5%BC%80%E5%8F%91%E6%8C%87%E5%8D%97.md) 按其定义进行开发。


#### 5.获取跨链服务 Token
发起跨链交易后，您需要访问以太坊跨链服务，请参见[BSN-DDC跨链服务OpenAPI](https://github.com/BSN-DDC/wuhanchain/blob/master/docs/BSN-DDC%E8%B7%A8%E9%93%BE%E6%9C%8D%E5%8A%A1OpenAPI.md) 的【**获取Token**】接口，得到`session_id`。

#### 6.查询跨链状态
得到有效的 Token 后，结合跨链交易的签名账户（即sdk里的`setSender`），继续对接【**查询跨链状态**】接口，确保可以查到待签名的跨链数据。

#### 7.查询跨链数据
继续对接【**查询跨链数据**】接口，得到待签名的数据。

**请注意：**该接口仅可查询待签名的跨链数据。

#### 8.对跨链数据进行签名
- 跨链数据的数据结构如下，其定义详情请参见`github.com/ethereum/go-ethereum/core/types`

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

**请注意：** `To`表示以太坊上的跨链管理合约地址`0x49f264851241694f80EACF616AA888eEAE5FB7E4`，该地址如发生变更，我们将会在 BSN-DDC 的迭代公告内提前说明。

- 签名方法请参见下述代码

```go
func SignTx(dynamicFeeTx *types.DynamicFeeTx) (signedtx *types.Transaction, err error) {
    // 连接以太坊节点 RPC 地址
	ethClient, err := ethclient.Dial(ethRpcAddr)
	if err != nil {
		fmt.Printf("dial eth client err: %s \n", err)
		return
	}
	
	defer ethClient.Close()
	
	// 以太坊链 ID
	chainId, err := ethClient.ChainID(context.Background())
	if err != nil {
		fmt.Printf("get chain id err: %v \n", err)
		return
	}
	
	// 加载钱包
	keyStore := util.NewEthKeyStore(chainId)
	err = keyStore.UnlockKeys()
	if err != nil {
		fmt.Printf("unlock keys err: %v \n", err)
		return
	}
	
	// 取钱包账户
	accArr := keyStore.GetAccounts()
	nonce, err := ethClient.PendingNonceAt(context.Background(), accArr[0].Address)
	if err != nil {
		fmt.Printf("cannot get account %s nonce, err: %s, set it to nil! \n", accArr[0].Address, err)
		return
	}
	
    // 获取当前小费上限
	gasTipCap, err := ethClient.SuggestGasTipCap(context.Background())
	if err != nil {
		fmt.Printf("get suggest gas tipCap failed error: %s \n", err.Error())
		return
	}
	
    // 获取当前块高
	number, err := ethClient.BlockNumber(context.Background())
	if err != nil {
		fmt.Printf("get New BlockNumber failed error: %s \n", err.Error())
		return
	}
	
   // 获取当前区块头
	newHead, err := ethClient.HeaderByNumber(context.Background(), big.NewInt(int64(number)))
	if err != nil {
		fmt.Printf("get Block By Number failed error: %s \n", err.Error())
		return
	}
	
    // 计算本交易的费用上限
	gasFeeCap := new(big.Int).Add(
		gasTipCap,
		new(big.Int).Mul(newHead.BaseFee, big.NewInt(2)),
	)
	
	dynamicFeeTx.Nonce = nonce
	dynamicFeeTx.ChainID = chainId
	dynamicFeeTx.GasFeeCap = gasFeeCap
	dynamicFeeTx.GasTipCap = gasTipCap
	
	dynamicFeeTx.Gas = 500000
	
	tx := types.NewTx(dynamicFeeTx)
	
    // 签名
	signedtx, err = keyStore.SignTransaction(tx, accArr[0])
	if err != nil {
		fmt.Printf("sign transactions err: %s \n", err.Error())
		return
	}
	
    // 返回签名数据
	hash := signedtx.Hash()
	fmt.Printf("eth tx hash:%v \n", hash.String())
	return
}
```


#### 9.发送签名交易
完成签名后，继续对接[BSN-DDC跨链服务OpenAPI](https://github.com/BSN-DDC/wuhanchain/blob/master/docs/BSN-DDC%E8%B7%A8%E9%93%BE%E6%9C%8D%E5%8A%A1OpenAPI.md) 的【**发送交易到以太坊**】接口，向以太坊主网提交目标链交易。

**请注意:** 

- 如果您不通过此接口向以太坊主网发起交易，那么以太坊上交易成功，则 BSN-DDC 跨链服务也可识别，但是如果以太坊上交易失败，那么跨链服务无法识别，该笔跨链数据的跨链状态仍为“跨链中”，所以不会向您发起跨链业务费的退款。

- 如遇遇到没有退跨链业务费的问题，您可以继续通过此接口再次向以太坊提交交易，BSN-DDC 跨链服务将自动识别并更新跨链状态，如跨链失败则向您发起跨链业务费的退款。

- 从发送签名交易到以太坊主网打包落块，一般需要 2～7 分钟，具体时间跟以太坊主网环境有关。

- 一笔官方 DDC 的跨链数据，在以太坊主网上的交易 Gas 大约为 40 元人民币（以太币 1300 美元计算），具体费用与以太坊主网环境及以太币有关。

#### 特别说明！！！
如果您期望在 OpenSea 上售卖/购买官方 DDC，需注意以下几点： 

- 调用官方 DDC 的生成方法时，URI 必须符合 [Metadata标准](https://docs.opensea.io/docs/metadata-standards)，同时 URI 的参数值应为公网可访问的 HTTPS 地址（OSS、IPFS、自搭建的服务地址等都可以）例如：`https://my-json-server.typicode.com/Arkln/demo/tokens/0`
 
- NFT 合约需要符合 [OpenSea 的要求](https://support.opensea.io/hc/en-us/articles/4403934341907-How-do-I-import-my-contract-automatically-) ，否则在 OpenSea 上也无法查到铸造的 Token。

- 通过 BSN 的跨链机制到 OpenSea 上的 DDC，OpenSea 不收取“创作费”。

- 如果您自己部署 NFT 合约，需要对合约进行验证，详情请参加 [etherscan 说明](https://info.etherscan.com/types-of-contract-verification/)；需要对合约定义名称，详情请参考 [stackoverflow](https://stackoverflow.com/questions/68891144/how-to-fix-unidentified-contract-opensea-is-unable-to-understand-erc1155) 的介绍。
