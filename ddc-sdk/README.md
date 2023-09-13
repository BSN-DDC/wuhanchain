# DDC-SDK

- [DDC-SDK](#ddc-sdk)
    - [要求](#要求)
    - [文档](#文档)
  - [用法](#用法)
    - [合约地址信息：](#合约地址信息)
    - [1.初始化DDCSdkClient](#1初始化ddcsdkclient)
    - [2.BSN-DDC-权限管理](#2bsn-ddc-权限管理)
    - [3.BSN-DDC-费用管理](#3bsn-ddc-费用管理)
    - [4.BSN-DDC-721](#4bsn-ddc-721)
    - [5.BSN-DDC-1155](#5bsn-ddc-1155)
    - [6.BSN-DDC-跨链](#6bsn-ddc-跨链)
    - [7.BSN-DDC-开放联盟链跨链](#7bsn-ddc-开放联盟链跨链)
    - [8.BSN-DDC-交易查询](#8bsn-ddc-交易查询)
    - [9.BSN-DDC-区块查询](#9bsn-ddc-区块查询)
    - [10.BSN-DDC-数据解析](#10bsn-ddc-数据解析)
    - [11.离线账户创建](#11离线账户创建)
  - [测试用例](#测试用例)
  - [配置](#配置)
    - [按请求配置](#按请求配置)
    - [配置网关](#配置网关)
        - [网关地址](#网关地址)
        - [网关KEY](#网关key)
    - [配置自动重试](#配置自动重试)
    - [配置超时](#配置超时)
    - [配置 （ethGetTransactionCount）nonce 值](#配置-ethgettransactioncountnonce-值)

### 要求

-   **Java 1.8 或更高版本**

### 文档

武汉链网关接入说明：[BSN Gateway Docs](https://bsnbase.com/static/tmpFile/bzsc/openper/7-3-3.html)

以太坊开发文档：[ETH Developers Docs](https://ethereum.org/zh/developers/docs/)

## 用法

### 合约地址信息：

```
 权限代理合约地址：0x466D5b0eA174a2DD595D40e0B30e433FCe6517F5
 计费代理合约地址：0xCa97bF3a19403805d391102908665b16B4d0217C
 DDC 721代理合约地址：0xad3B52B4F4bd9198DC69dD9cE4aC9846667461a2
 DDC 1155代理合约地址：0x061e59c74815994DAb4226a0D344711F18E0F418
 DDC 跨链应用代理合约地址：0xc4E12bB845D9991ee26718E881C712B2c0cB2048
 DDC 开放联盟链跨链合约地址：0xF2FFC996D612d35F3e86DF3179906E780749845D
```

### 1.初始化DDCSdkClient

```java
    // 注册签名事件
    SignEventListener signEventListener = event -> transactionSignature(event.getSender(), event.getRawTransaction());

    // 签名账户地址
    public static String sender = "0xCd00A127C44E6E61070544e626ee5F9336D04e80";

    // 签名处理示例
    String transactionSignature(String sender, RawTransaction transaction) {
        // sender: Obtain the private key according to the sender and complete its signature

        // sender 对应的Hex格式私钥
        String privateKey = "0x9a42974510d63f697e7f69802c0eb8c061a4498d926d30505014ec1c9351202f";
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        return Numeric.toHexString(signedMessage);
    }
    
    // 创建客户端
    // 也可设置合约地址和相关参数值
    DDCSdkClient ddcSdkClient = DDCSdkClient.builder()
            .setSignEventListener(signEventListener)
            .setAuthorityAddress("0x466D5b0eA174a2DD595D40e0B30e433FCe6517F5")  // 权限代理合约地址
            .setChargeAddress("0xCa97bF3a19403805d391102908665b16B4d0217C")     // 计费代理合约地址
            .setDdc721Address("0xad3B52B4F4bd9198DC69dD9cE4aC9846667461a2")     // DDC 721代理合约地址
            .setDdc1155Address("0x061e59c74815994DAb4226a0D344711F18E0F418")    // DDC 1155代理合约地址
            .setCrossChainAddress("0xc4E12bB845D9991ee26718E881C712B2c0cB2048") // 跨链应用代理合约地址
    		.setOpbCrossChainAddress("0xF2FFC996D612d35F3e86DF3179906E780749845D") // DDC 开放联盟链跨链合约地址
            .setChainId(BigInteger.valueOf(5555))
            .build();

    // 设置网关
    DDCWuhan.setGatewayUrl("https://opbningxia.bsngate.com:18602/api/[项目ID]/rpc");
    // 设置网关API-KEY
    DDCWuhan.setGatewayApiKey("[项目KEY]");
    // 设置Nonce管理地址（通过该地址获得Nonce）
    DDCWuhan.setNonceManagerAddress(sender);
    
    // 进行交易的方法需要传入sender参数，即方法调用者地址
```

### 2.BSN-DDC-权限管理

```java
    AuthorityService authorityService = ddcSdkClient.authorityService; 
    
    // 添加账户
    // sender       签名账户地址
    // account      DDC链账户地址
    // accountName  DDC账户对应的账户名称
    // accountDID   DDC账户对应的DID信息
    // leaderDID    该普通账户对应的上级账户的DID
    // 返回交易哈希
    String txHash = authorityService.addAccountByOperator(sender, account, accountName, accountDID, leaderDID);
    
    // 批量添加账户
    List<String> accName = new ArrayList<>();
    List<String> accAddress = new ArrayList<>();
    List<String> accDID = new ArrayList<>();
    List<String> leaderDID = new ArrayList<>();
    accName.add("test001");
    accAddress.add("0xa9f2e4519f429306900796cdc6a5d795635ddfe0");
    accDID.add("did:test2");
    leaderDID.add("");
    // 返回交易哈希
    authorityService.addBatchAccountByOperator(sender, accName, accDID, accAddress, leaderDID);
        
    // 查询账户
	// account DDC用户链账户地址
    // 返回DDC账户信息
    AccountInfo info = authorityService.getAccount(account);
    
    // 更新账户状态
    // account DDC用户链账户地址
    // state   枚举，状态 ：Frozen - 冻结状态 ； Active - 活跃状态
    // changePlatformState
    // 返回交易哈希
    authorityService.updateAccState(sender, account, 1， false);
    
    // 设置平台方添加链账户开关
    // 返回交易哈希
    authorityService.setSwitcherStateOfPlatform(sender, true);
    
    // 查询平台方添加链账户开关状态
    authorityService.switcherStateOfPlatform();
    
    // 对 DDC 跨平台操作授权
    // 返回交易哈希
    authorityService.crossPlatformApproval(sender, from, to, true);
    
    // 同步平台方 DID
    // 返回交易哈希
    List<String> dids = new ArrayList<>();
    dids.add("did:bsn:2EBNdfmKiD4qpMqUCnMzuZZqq7GA");
    authorityService.syncPlatformDID(sender, dids);
    
```

### 3.BSN-DDC-费用管理

```java
    ChargeService chargeService = ddcSdkClient.chargeService;  
    
    // 充值
    // sender 签名账户地址
    // to 充值账户的地址
	// amount 充值金额
	// 返回交易哈希
    String txHash = chargeService.recharge(sender, to, amount);  
    
    // 批量充值
    Multimap<String, BigInteger> map = ArrayListMultimap.create();
    map.put("0x02a66ef232dac0cd4590d3af2ddb9c2cd95eccc1", new BigInteger("10"));
    map.put("0x201ea42500d8ff71cd897ca51269c0c4e5680aaa", new BigInteger("20"));
    chargeService.rechargeBatch(sender, map);
    
    // 链账户余额查询
    // accAddr 查询的账户地址
	// 返回账户所对应的业务费余额
    BigInteger balance = chargeService.balanceOf(accAddr);
    
    // 批量链账户余额查询
    List<String> accAddrs = new ArrayList<>();
    accAddrs.add("0x02CEB40D892061D457E7FA346988D0FF329935DF");
    List<BigInteger> balances = chargeService.balanceOfBatch(accAddrs);
    
    // DDC计费规则查询
    // ddcAddr DDC业务主逻辑合约地址
	// sig Hex格式的合约方法ID
	// 返回DDC合约业务费
    BigInteger fee = chargeService.queryFee(ddcAddr, "0x36351c7c");
    
    // 运营账户充值
    // amount 对运营方账户进行充值的业务费
    // 返回交易哈希
    chargeService.selfRecharge(sender, amount);
	
	// 设置DDC计费规则
    // ddcAddr DDC业务主逻辑合约地址
    // sig Hex格式的合约方法ID
    // amount 业务费用
    // 返回交易哈希
    chargeService.setFee(sender, ddcAddr, sig, amount);
    
    // 删除DDC计费规则
    // ddcAddr DDC业务主逻辑合约地址
    // sig Hex格式的合约方法ID
    // 返回交易哈希
    chargeService.delFee(sender, ddcAddr, sig);
    
    // 按合约删除DDC计费规则
    // ddcAddr DDC业务主逻辑合约地址
    // 返回交易哈希
    chargeService.delDDC(sender, ddcAddr);
    
```

### 4.BSN-DDC-721

```java
    DDC721Service ddc721Service = ddcSdkClient.ddc721Service; 
    
    // DDC授权
    // sender 签名账户地址
    // to     授权者账户
    // ddcId  DDC唯一标识
    // 返回交易哈希
    String txHash = ddc721Service.approve(sender, to, ddcId);
    
    // DDC授权查询
    
    // ddcId DDC唯一标识
    // 返回授权的账户
    String account = ddc721Service.getApproved(ddcId);
    
    // 账户授权
    // operator 授权者账户
    // approved 授权标识
    // 返回交易hash
    ddc721Service.setApprovalForAll(sender,operator, true);
    
    // 账户授权查询
    // owner    拥有者账户
    // operator 授权者账户
    // 返回授权标识
    Boolean result = ddc721Service.isApprovedForAll(owner, operator);

    // 安全生成
    // sender  签名账户
    // to      授权者账户
    // ddcURI  DDC资源标识符
    // data    附加数据
    // 返回交易hash
    ddc721Service.safeMint(sender, to, ddcURI, data);
    
    // 生成
    // sender  签名账户
    // to      接收者账户
    // ddcURI  DDC资源标识符
    // 返回交易hash
    ddc721Service.mint(sender, to, ddcURI);
    
    // 安全转移
    // from  拥有者账户
    // to    授权者账户
    // ddcId DDC唯一标识
    // data  附加数据
    // 返回交易hash
    ddc721Service.safeTransferFrom(sender, from, to, ddcId, data);
    
    // 转移
    // from  拥有者账户
    // to    接收者账户
    // ddcId ddc唯一标识
    // 返回交易hash
    ddc721Service.transferFrom(sender, from, to, ddcId);
    
    // 冻结
    // ddcId DDC唯一标识
    // 返回交易hash
    ddc721Service.freeze(sender, ddcId);
    
    // 解冻
    // ddcId DDC唯一标识
    // 返回交易hash
     ddc721Service.unFreeze(sender, ddcId);
    
    // 销毁
    // ddcId DDC唯一标识
    // 返回交易hash
    ddc721Service.burn(sender, ddcId);
    
    // 查询数量
    // owner 拥有者账户
    // 返回ddc的数量
    BigInteger num = ddc721Service.balanceOf(owner);
    
    // 查询拥有者
    // ddcId ddc唯一标识
    // 返回拥有者账户
    String account = ddc721Service.ownerOf(ddcId);
    
    // 获取名称
    // 返回DDC运营方名称
    String name = ddc721Service.name();
    
    // 获取符号
    // 返回DDC运营方符号
    String symbol = ddc721Service.symbol();
    
    // 获取DDCURI
    // 返回DDC资源标识符
    String ddcURI = ddc721Service.ddcURI(ddcId);
    
    // 设置DDCURI
    // sender   ddc拥有者或授权者
    // ddcId    ddc唯一标识
    // ddcURI   ddc资源标识符
    ddc721Service.setURI(sender, ddcId, ddcURI)
    
    // 名称符号设置
    ddc721Service.setNameAndSymbol(sender, "ddc", "ddc721");
    
    // 最新DDCID查询
    BigInteger DDCID = ddc721Service.getLatestDDCId();

    // 元交易Nonce查询
    // from    元交易签名账户地址
    BigInteger nonce = ddc721Service.getNonce(from);

    // 元交易生成
    // to       接收者账户地址
    // ddcURI   DDC资源标识符
    // nonce    接收者nonce值
    // deadline 元交易有效期
    // sig      元交易签名信息
    String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
    String ddcURI = "http://ddcUrl";
    BigInteger deadline = BigInteger.valueOf(1671096761);
    BigInteger nonce = ddcSdkClient.ddc721Service.getNonce(to).add(BigInteger.ONE);

    String digest = ddcSdkClient.ddc721MetaTransaction.getMintDigest(to, ddcURI, nonce, deadline);
    byte[] sign = ddcSdkClient.ddc721MetaTransaction.generateSignature(originPrivateKey, digest);
    byte[] data = Numeric.hexStringToByteArray("0x16");
    
    ddc721Service.metaMint(sender, to, ddcURI, nonce, deadline, sign);
    
    // 元交易安全生成
    // to       接收者账户地址
    // ddcURI   DDC资源标识符
    // data     附加数据
    // nonce    接收者nonce值
    // deadline 元交易有效期
    // sig      元交易签名信息
    String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
    String ddcURI = "http://ddcUrl";
    BigInteger deadline = BigInteger.valueOf(1671096761);
    BigInteger nonce = ddcSdkClient.ddc721Service.getNonce(to).add(BigInteger.ONE);

    String digest = ddcSdkClient.ddc721MetaTransaction.getSafeMintDigest(to, ddcURI, data, nonce, deadline);
    byte[] sign = ddcSdkClient.ddc721MetaTransaction.generateSignature(originPrivateKey, digest);
    byte[] data = Numeric.hexStringToByteArray("0x16");
    
    ddc721Service.metaSafeMint(sender, to, ddcURI, data, nonce, deadline, sign);
    
    // 元交易转移
    // from     拥有者账户地址
    // to       接收者账户地址
    // ddcURI   DDC资源标识符
    // nonce    拥有者nonce值
    // deadline 元交易有效期
    // sig      元交易签名信息
    String from = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
    String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
    BigInteger ddcId = BigInteger.valueOf(8525);
    BigInteger deadline = BigInteger.valueOf(1671096761);
    BigInteger nonce = ddcSdkClient.ddc721Service.getNonce(from).add(BigInteger.ONE);

    String digest = ddcSdkClient.ddc721MetaTransaction.getTransferFromDigest(from, to, ddcId, nonce, deadline);
    byte[] sign = ddcSdkClient.ddc721MetaTransaction.generateSignature(originPrivateKey, digest);
    log.info("TransferFrom sign: {}", Numeric.toHexString(signature));

    ddcSdkClient.ddc721Service.metaTransferFrom(sender, from, to, ddcId, nonce, deadline, sign);
    
    // 元交易安全转移
    // from     拥有者账户地址
    // to       接收者账户地址
    // ddcURI   DDC资源标识符
    // data     附加数据
    // nonce    拥有者nonce值
    // deadline 元交易有效期
    // sig      元交易签名信息
    String from = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
    String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
    BigInteger ddcId = BigInteger.valueOf(8525);
    byte[] data = Numeric.hexStringToByteArray("0x16");
    BigInteger deadline = BigInteger.valueOf(1671096761);
    BigInteger nonce = ddcSdkClient.ddc721Service.getNonce(from).add(BigInteger.ONE);

    String digest = ddcSdkClient.ddc721MetaTransaction.getSafeTransferFromDigest(from, to, ddcId, data, nonce, deadline);
    byte[] sign = ddcSdkClient.ddc721MetaTransaction.generateSignature(originPrivateKey, digest);

    ddcSdkClient.ddc721Service.metaSafeTransferFrom(sender, from, to, ddcId, data, nonce, deadline, sign);
    
    // 元交易销毁
    // ddcId    DDC唯一标识符
    // nonce    拥有者nonce值
    // deadline 元交易有效期
    // sig      元交易签名信息
    String from = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
    BigInteger ddcId = BigInteger.valueOf(8526);
    BigInteger deadline = BigInteger.valueOf(1671096761);
    BigInteger nonce = ddcSdkClient.ddc721Service.getNonce(from).add(BigInteger.ONE);

    String digest = ddcSdkClient.ddc721MetaTransaction.getBurnDigest(ddcId, nonce, deadline);
    byte[] sign = ddcSdkClient.ddc721MetaTransaction.generateSignature(originPrivateKey, digest);

    ddcSdkClient.ddc721Service.metaBurn(sender, ddcId, nonce, deadline, sign);

```

### 5.BSN-DDC-1155

```java
    DDC1155Service ddc1155Service = ddcSdkClient.ddc1155Service; 
    
    // 账户授权
    // sender   签名账户
    // operator 授权者账户
    // approved 授权标识
    // 返回交易哈希
    String txHash  = ddc1155Service.setApprovalForAll(sender, operator, approved);
    
    // 账户授权查询
    // owner    拥有者账户
    // operator 授权者账户
    // 返回授权结果（boolean）
    Boolean result = isApprovedForAll(owner, operator);
    
    // 安全转移
    // from   拥有者账户
    // to     接收者账户
    // ddcId  DDCID
    // amount 需要转移的DDC数量
    // data   附加数据
    // 返回交易哈希
    ddc1155Service.safeTransferFrom(sender, from, to, ddcId, amount, data);
    
    // 批量安全转移
    // from 拥有者账户
    // to   接收者账户
    // ddcs 拥有者DDCID集合
    // data 附加数据
    // 返回交易哈希
    String txHash  = ddc1155Service.safeBatchTransferFrom(sender, from, to, ddcs, data);
    
    // 冻结
    // ddcId DDC唯一标识
    // 返回交易哈希
    ddc1155Service.freeze(sender, ddcId);
    
    // 解冻
    // ddcId DDC唯一标识
    // 返回交易哈希
    ddc1155Service.unFreeze(sender, ddcId);
    
    // 销毁
    ddc1155Service.burn(sender, owner, ddcId);
    
    // 批量销毁
    ddc1155Service.burnBatch(sender, owner, ddcIds);
    
    // 查询数量
    BigInteger balance = ddc1155Service.balanceOf(owner, ddcId);
    
    // 批量查询数量
    Multimap<String, BigInteger> map = ArrayListMultimap.create();
    map.put("0x9dff125d6562df4d72b9bd4616c815a2b45c39ab", new BigInteger(82));
    map.put("0x9dff125d6562df4d72b9bd4616c815a2b45c39ab", new BigInteger(83));
    List<BigInteger> balances = ddc1155Service.balanceOfBatch(map);
    
    // 获取DDCURI
    String ddcURI = ddc1155Service.ddcURI(ddcId);
    
    // 设置ddcURL
    // sender 签名账户
    // ddcId  DDC唯一标识
    // ddcURL DDC资源标识符
    ddc1155Service.setURI(sender, owner，new BigInteger(""),"");
    
    // 最新DDCID查询
    BigInteger DDCID = ddc1155Service.getLatestDDCId();
    
    // 元交易Nonce查询
    BigInteger Nonce = ddc1155Service.getNonce(owner);
    
    // 元交易安全生成
    ddc1155Service.metaSafeMint(sender,  to,  amount,  ddcURI,  data,  nonce,  deadline,  sign);
    
    // 元交易安全转移
    ddc1155Service.metaSafeTransferFrom(sender, from, to, ddcId, amount, data, nonce, deadline, sign);
    
    // 元交易销毁
    ddc1155Service.metaBurn(sender, owner, ddcId, amount, data, nonce, deadline, sign);
    
```

### 6.BSN-DDC-跨链

```java
    // 跨链转移
    // sender       签名账户
    // to           目标链接收账户地址
    // ddcId        源链上DDC唯一标识
    // data         附加数据
    // ddcType      DDCType，721或1155
    // toChainID    目标链侧链ID
    // toCCAddr     目标链跨链应用合约地址
    // signer       目标链生成Token的签名账户地址
    // funcName     目标链合约方法名称
    // 返回交易Hash
    byte[] data = new byte[1];
    //data[0] = 1;
    CrossChainTransferParams params = CrossChainTransferParams.builder()
            .setSender(sender)
            .setTo("0x6922D8af46d5e39c2a15cAa26eE692FCc118aDc5")
            .setDdcId(BigInteger.valueOf(9196))
            .setData(data)
            .setDDCType(DDCTypeEnum.ERC721)
            .setToChainID(BigInteger.valueOf(100003))
            .setToCCAddr("0x44A175f7E830e4d66DC8BEdF8cfb9a9330B3F472")
            .setSigner("0x9bde88224e7cf3ada6045fc0236d10b8cd5a94da")
            .setFuncName("crossChainMint")
            .build();
    String txHash = ddcSdkClient.crossChainService.crossChainTransfer(params);


    // 查询跨链转移事件
    // 返回跨链转移事件
    CrossChainTransferEventBean result = ddcSdkClient.crossChainService.getCrossChainTransferEvent(txHash);

```

### 7.BSN-DDC-开放联盟链跨链

```java
    // 构造跨链转移方法参数对象
    OpbCrossChainTransferParams params = OpbCrossChainTransferParams.builder()
            // 武汉链签名账户地址
            .setSender(sender) 
            // 目标链接收者账户地址
            .setTo("0x950D9693381B62791787F0E772C24DEA93FD612D")    
            // 是否锁定
            .setIsLock(true)     
            // DDC唯一标识
            .setDdcId(BigInteger.valueOf(138))  
            // 附加数据
            .setData("0x".getBytes(StandardCharsets.UTF_8))   
            // DDC类型
            .setDDCType(DDCTypeEnum.ERC721)       
            // 目标链chainId
            .setToChainID(BigInteger.valueOf(2))                     
            .build();

    // 调用跨链方法发起跨链交易，返回交易hash
    String txHash = ddcSdkClient.opbCrossChainService.crossChainTransfer(params);
```



### 8.BSN-DDC-交易查询

```java
    BaseService baseService = new BaseService();
	
    // 查询交易回执
    // hash 交易哈希
    // 返回交易回执
     TransactionReceipt TxReceipt = baseService.getTransactionReceipt(hash);
     
     // 查询交易信息
     // hash 交易哈希
     // 返回交易信息
     Transaction Tx = baseService.getTransactionByHash(hash);
     
     // 查询交易状态
     // hash 交易哈希
     // 返回交易状态
     Boolean state = baseService.getTransByStatus(hash);

     // 查询交易数（Nonce）
     // address 账户地址
     // 返回交易数
     BigInteger nonce = baseService.getTransactionCount(address);
	
```

### 9.BSN-DDC-区块查询

```java
    BaseService baseService = new BaseService();
	
    // 获取区块信息
    EthBlock.Block blockinfo = baseService.getBlockByNumber(blockNumber)
    
```

### 10.BSN-DDC-数据解析

```
3.1.9	BSN-DDC-数据解析
    3.1.9.1	权限数据
        3.1.9.1.1	添加账户开关设置
        3.1.9.1.2	添加账户
        3.1.9.1.3	批量添加账户
        3.1.9.1.4	更新账户状态
        3.1.9.1.5	跨平台授权
        3.1.9.1.6	同步平台方DID
    3.1.9.2	充值数据
        3.1.9.2.1	充值
        3.1.9.2.2	批量充值
        3.1.9.2.3	DDC业务费扣除
        3.1.9.2.4	设置DDC计费规则
        3.1.9.2.5	删除DDC计费规则
        3.1.9.2.6	删除DDC合约授权
    3.1.9.3	721数据
        3.1.9.3.1	生成
        3.1.9.3.2	安全生成
        3.1.9.3.3	批量生成
        3.1.9.3.4	批量安全生成
        3.1.9.3.5	转移
        3.1.9.3.6	安全转移
        3.1.9.3.7	冻结
        3.1.9.3.8	解冻
        3.1.9.3.9	销毁
        3.1.9.3.10	URI设置
        3.1.9.3.11	跨链锁定
        3.1.9.3.12	跨链解锁
        3.1.9.3.13	元交易生成
        3.1.9.3.14	元交易安全生成
        3.1.9.3.15	元交易批量生成
        3.1.9.3.16	元交易批量安全生成
        3.1.9.3.17	元交易转移
        3.1.9.3.18	元交易安全转移
        3.1.9.3.19	元交易销毁
    3.1.9.4	1155数据
        3.1.9.4.1	安全生成
        3.1.9.4.2	批量安全生成
        3.1.9.4.3	安全转移
        3.1.9.4.4	批量安全转移
        3.1.9.4.5	冻结
        3.1.9.4.6	解冻
        3.1.9.4.7	销毁
        3.1.9.4.8	批量销毁
        3.1.9.4.9	URI设置
        3.1.9.4.10	跨链锁定
        3.1.9.4.11	跨链解锁
        3.1.9.4.12	元交易安全生成
        3.1.9.4.13	元交易批量安全生成
        3.1.9.4.14	元交易安全转移
        3.1.9.4.15	元交易批量安全转移
        3.1.9.4.16	元交易销毁
        3.1.9.4.17	元交易批量销毁
    3.1.9.5	跨链应用
        3.1.9.5.1	基础数据设置
        3.1.9.5.2	DDC跨链流转
        3.1.9.5.3	DDC跨链通知
```

```java
	BlockEventService blockEventService = ddcSdkClient.blockEventService;

	// 获取区块事件并解析
	// 1. 根据块高获取区块信息
    // 2. 根据块中交易获取交易回执
    // 3. 遍历交易回执中的事件并解析
    // blockNumber 块高
    // 返回 ArrayList<Object>

	ArrayList<BaseEventResponse> blockEvent = blockEventService.getBlockEvent("28684");
	blockEvent.forEach(b->{
        System.out.println(b.log);
    });
        
```

### 11.离线账户创建

```java
    // 创建Hex格式账户
    // 返回包含助记词，公钥，私钥，hex格式地址的Account对象
    AccountService accountService = ddcSdkClient.accountService;
    Account acc = accountService.createAccount();
    System.out.println("================================" + acc.getAddress());
```


## 测试用例

权限服务单元测试 - [AuthorityServiceTest.java](src/test/java/service/AuthorityServiceTest.java)

计费服务单元测试 - [ChargeServiceTest.java](src/test/java/service/ChargeServiceTest.java)

基本服务单元测试 - [BaseServiceTest.java](src/test/java/service/BaseServiceTest.java)

区块事件服务单元测试 - [BlockEventServiceTest.java](src/test/java/service/BlockEventServiceTest.java)

DDC721服务单元测试 - [DDC721ServiceTest.java](src/test/java/service/DDC721ServiceTest.java)

DDC1155服务单元测试 - [DDC1155ServiceTest.java](src/test/java/service/DDC1155ServiceTest.java)

账户服务单元测试 - [AccountTest.java](src/test/java/service/AccountTest.java)

跨链服务单元测试 - [CrossChainServiceTest.java](src/test/java/service/CrossChainServiceTest.java)

开放联盟链跨链服务单元测试 - [OpbCrossChainServiceTest.java](src/test/java/service/OpbCrossChainServiceTest.java)

## 配置

### 按请求配置

所有请求方法都接受可选的 `RequestOptions` 对象。如果要设置网关地址等，则使用此选项。

gasPrice，gasLimit，可为空。建议进行配置以减少对网关的请求数。

```java
// mint
sdkClient.ddc721Service.mint("0x24a95d34dcbc74f714031a70b077e0abb3308088", "ddcURI");

// use options mint
        RequestOptions options = RequestOptions.builder()
        .setGasLimit("1000000")
        .build();
        sdkClient.ddc721Service.mint("0x24a95d34dcbc74f714031a70b077e0abb3308088", "ddcURI",options);
```

### 配置网关

##### 网关地址

必须配置网关URI

```java
DDCWuhan.setGatewayUrl("https://opbningxia.bsngate.com:18602/api/[项目ID]/rpc");
```

##### 网关KEY
如果在BSN门户中启用项目密钥，则需要在sdk中进行配置。此配置将全局生效。

```java
DDCWuhan.setGatewayApiKey("[项目KEY]");
```



### 配置自动重试

可以配置全局自动重试：

```java
DDCWuhan.setMaxNetworkRetries(5);
```
或者在更精细的粒度级别上使用 `RequestOptions`：

```java
RequestOptions requestOptions = RequestOptions.builder()
        .setNetworkRetries(2)
        .build();
```



### 配置超时

可以配置全局连接和读取超时：

```java
DDCWuhan.setConnectTimeout(10 * 1000); // in milliseconds
        DDCWuhan.setReadTimeout(10 * 1000);
```

或者在更精细的粒度级别上使用 `RequestOptions`：

```java
RequestOptions requestOptions = RequestOptions.builder()
        .setConnectTimeout(10 * 1000) // in milliseconds
        .build();
```



### 配置 （ethGetTransactionCount）nonce 值

nonce的默认值是根据DDCWuhan.nonceManagerAddress设置的账户地址从网关获取的。如果频繁调用建议进行本地维护，请使用RequestOptions传递此参数。

```java
RequestOptions options = RequestOptions.builder().build();
        requestOptions.setNonce("2");
```

### 配置 gasPrice 值

gasPrice的默认值为1000000000，如果通过DDCWuhan.setGasPrice方法进行设置将会覆盖默认值

```java
DDCWuhan.setGasPrice(BigInteger.valueOf(1000000000));
```

或者在更精细的粒度级别上使用 RequestOptions：

```java
RequestOptions requestOptions = RequestOptions.builder()
  .setGasPrice(BigInteger.valueOf(2000000000))
  .build()
);
```
