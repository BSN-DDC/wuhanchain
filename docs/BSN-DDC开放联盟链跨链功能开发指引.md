## BSN-DDC 武汉链跨链功能开发指引

> 本文介绍武汉链官方DDC OPB跨链功能的详细步骤，跨链功能目前支持文昌链，武汉链，泰安链之间的互跨。如果您没有此需求可忽略此文。



### 参数信息

  #### 链ID

  - 文昌链 chainId：`2`
  - 泰安链 chainId：`3`
  - 武汉链 chainId：`4`



#### 合约地址

  - 权限合约地址：`0x466D5b0eA174a2DD595D40e0B30e433FCe6517F5`
- 计费合约地址：`0xCa97bF3a19403805d391102908665b16B4d0217C`
- DDC 721合约地址：`0xad3B52B4F4bd9198DC69dD9cE4aC9846667461a2`
- DDC 1155合约地址：`0x061e59c74815994DAb4226a0D344711F18E0F418`
- DDC OPB跨链应用合约地址：`0xF2FFC996D612d35F3e86DF3179906E780749845D`



### 功能说明

#### 发起跨链交易


  ##### 1.初始化DDCSdkClient并调用跨链方法

​        为了从武汉链将DDC信息发送到其他开放联盟链，首先您需要初始化DDCSdkClient，调用 `crossChainTransfer` 方法发起跨链，参考示例代码：

  ``` java
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
      // 权限合约地址
      .setAuthorityAddress("0x466D5b0eA174a2DD595D40e0B30e433FCe6517F5")
      // 计费合约地址
      .setChargeAddress("0xCa97bF3a19403805d391102908665b16B4d0217C")
      // DDC 721合约地址
      .setDdc721Address("0xad3B52B4F4bd9198DC69dD9cE4aC9846667461a2")
      // DDC 1155合约地址
      .setDdc1155Address("0x061e59c74815994DAb4226a0D344711F18E0F418")
      // OPB跨链应用地址
      .setOpbCrossChainAddress("0xF2FFC996D612d35F3e86DF3179906E780749845D") 
      .setChainId(BigInteger.valueOf(5555))
      .build();
  
  // 设置网关
  DDCWuhan.setGatewayUrl("https://opbningxia.bsngate.com:18602/api/[项目ID]/rpc");
  // 设置网关API-KEY
  DDCWuhan.setGatewayApiKey("[项目KEY]");
  // 设置Nonce管理地址（通过该地址获得Nonce）
  DDCWuhan.setNonceManagerAddress(sender);
      
  
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
  
  // 调用SDK方法发起跨链交易
  String txHash = ddcSdkClient.opbCrossChainService.crossChainTransfer(params);
  ```

  **请注意：**

  - crossChainTransfer方法参数中的接收者账户地址一定要是目标链存在的账户，否则会交易失败。

  - 类型为1155的ddc发起跨链时，需要该DDC的拥有者账户拥有全部数量时才可以发起跨链。

    

#### 确认跨链交易

​        发起跨链交易后，您需要通知目标链接收者账户去访问[BSN-DDC开放联盟链跨链网关OpenAPI](BSN-DDC%E5%BC%80%E6%94%BE%E8%81%94%E7%9B%9F%E9%93%BE%E8%B7%A8%E9%93%BE%E7%BD%91%E5%85%B3OpenAPI.md) 去确认交易，才能在目标链执行跨链交易。因此目标链接收者账户需要进行以下步骤：

##### 1.获取跨链中心化服务访问权限

​        首先需要目标链链账户对自己的钱包地址进行签名，签名算法可参考[BSN-DDC开放联盟链跨链网关OpenAPI](BSN-DDC%E5%BC%80%E6%94%BE%E8%81%94%E7%9B%9F%E9%93%BE%E8%B7%A8%E9%93%BE%E7%BD%91%E5%85%B3OpenAPI.md) 的签名算法说明。签名后，请参见[BSN-DDC开放联盟链跨链网关OpenAPI](BSN-DDC%E5%BC%80%E6%94%BE%E8%81%94%E7%9B%9F%E9%93%BE%E8%B7%A8%E9%93%BE%E7%BD%91%E5%85%B3OpenAPI.md)的**【获取Token】**接口，得到访问Token。

  ##### 2.查询需要签名确认的跨链交易

​        获取到Token后，继续参见[BSN-DDC开放联盟链跨链网关OpenAPI](BSN-DDC%E5%BC%80%E6%94%BE%E8%81%94%E7%9B%9F%E9%93%BE%E8%B7%A8%E9%93%BE%E7%BD%91%E5%85%B3OpenAPI.md)的【**查询需要签名确认的跨链交易**】接口，得到需要签名确认的跨链交易的起始链交易hash，以便确认跨链交易使用。

  ##### 3.确认跨链交易

​        跨链交易只有目标链账户确认后才能继续进行，为了证明是目标链接收者账户亲自确认交易，目标链账户需要使用第1步获取到的签名和第2步获取到的起始链交易hash，结合目标链接收者账户地址，参见 [BSN-DDC开放联盟链跨链网关OpenAPI](BSN-DDC%E5%BC%80%E6%94%BE%E8%81%94%E7%9B%9F%E9%93%BE%E8%B7%A8%E9%93%BE%E7%BD%91%E5%85%B3OpenAPI.md)的【**确认跨链交易**】接口，进行确认。跨链交易确认完成后，BSN-DDC跨链网关会自动向目标链发起跨链交易。



