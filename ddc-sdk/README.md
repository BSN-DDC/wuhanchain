本SDK中包含平台方可调用的如下方法：
```

3.2.1BSN-DDC-权限管理
    3.2.1.1查询账户
    3.2.1.2更新账户状态

3.2.2BSN-DDC-费用管理
    3.2.2.1充值
    3.2.2.2链账户余额查询
    3.2.2.3DDC计费规则查询

3.2.3BSN-DDC-721
    3.2.3.1生成
    3.2.3.2DDC授权
    3.2.3.3DDC授权查询
    3.2.3.4账户授权
    3.2.3.5账户授权查询
    3.2.3.6安全转移
    3.2.3.7转移
    3.2.3.10销毁
    3.2.3.11查询数量
    3.2.3.12查询拥有者
    3.2.3.13获取名称
    3.2.3.14获取符号
    3.2.3.15获取DDCURI

3.2.4BSN-DDC-1155
    3.2.4.1生成
    3.2.4.2批量生成
    3.2.4.3账户授权
    3.2.4.4账户授权查询
    3.2.4.5安全转移
    3.2.4.6批量安全转移
    3.2.4.9销毁
    3.2.4.10批量销毁
    3.2.4.11查询数量
    3.2.4.12批量查询数量
    3.2.4.13获取DDCURI

3.2.5BSN-DDC-交易查询
    3.2.5.1查询交易信息
    3.2.5.2查询交易回执
    3.2.5.3查询交易状态

3.2.6BSN-DDC-区块查询
    3.2.6.1获取区块信息

3.2.7BSN-DDC-签名事件

3.2.8BSN-DDC-数据解析

    3.2.8.1权限数据
        3.2.8.1.1添加账户
        3.2.8.1.2更新账户状态

    3.2.8.2充值数据
        3.2.8.2.1充值
        3.2.8.2.2DDC业务费扣除
        3.2.8.2.3设置DDC计费规则
        3.2.8.2.4删除DDC计费规则
        3.2.8.2.5按合约删除DDC计费规则

    3.2.8.3BSN-DDC-721数据
        3.2.8.3.1生成
        3.2.8.3.2转移/安全转移
        3.2.8.3.3冻结
        3.2.8.3.4解冻
        3.2.8.3.5销毁

    3.2.8.4BSN-DDC-1155数据
        3.2.8.4.1生成
        3.2.8.4.2批量生成
        3.2.8.4.3安全转移
        3.2.8.4.4批量安全转移
        3.2.8.4.5冻结
        3.2.8.4.6解冻
        3.2.8.4.7销毁
        3.2.8.4.8批量销毁

```

### 要求

**Java 1.8 或 更高**



### Maven 依赖

使用SDK，添加以下依赖jar包。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>com.reddate.ddc</groupId>
    <artifactId>ddc-sdk-wuhan</artifactId>
    <version>0.0.1</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/libs/ddc-sdk-Wuhan.jar</systemPath>
</dependency>
<dependency>
    <groupId>org.fisco-bcos</groupId>
    <artifactId>web3sdk</artifactId>
    <version>2.4.0.0601-bsn</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/libs/web3sdk.jar</systemPath>
</dependency>
<dependency>
    <groupId>org.fisco.solc</groupId>
    <artifactId>solcJ</artifactId>
    <version>0.6.10.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/libs/solcJ-0.6.10.0.jar</systemPath>
</dependency>
```





## Usage

SdkExampleTest.java

```java
package service;

import com.reddate.ddc.DDCSdkClient;
import com.reddate.ddc.dto.ddc.Account;
import com.reddate.ddc.listener.SignEventListener;
import com.reddate.ddc.net.RequestOptions;
import com.reddate.ddc.service.DDC721Service;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertNotNull;


public class SdkExampleTest {

    SignEventListener signEventListener = event -> transactionSignature(event.getRawTransaction());

    DDCSdkClient  sdkClient = DDCSdkClient.instance("src/main/resources/contractConfig.json", signEventListener);

    private static String transactionSignature(RawTransaction transaction) {
        String privateKey = "0x2050cadaf97df6c99c************";
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        return Numeric.toHexString(signedMessage);
    }

    /**
     *  Mint
     * @throws Exception
     */
    @Test
    void mint() throws Exception {
        String tx = sdkClient.ddc721Service.mint("0x019ba4600e117f06e3726c0b100a2f10ec52339e", "ddcURI");
        assertNotNull(tx);
    }

    /**
     * Mint By Options
     * @throws Exception
     */
    @Test
    void mintByOptions() throws Exception {
        RequestOptions options = RequestOptions.builder(DDC721Service.class)
                .setGateWayUrl("http://********/rpc")
                .build();

        String tx = sdkClient.ddc1155Service.mint("0x019ba4600e117f06e3726c0b100a2f10ec523391", BigInteger.TEN, "ddcURL", options);
        assertNotNull(tx);
    }

    /**
     * Create account
     */
    @Test
    void createAccount() {
        Account account = sdkClient.accountService.createAccount();
        assertNotNull(account);
    }

}
```



### Configuration

请参考：src/main/resources/contractConfig.json

```
{
    "gateWayUrl":"",//网关地址
    "gasPrice":"",// gasPrice
    "gasLimit":"",// gasLimit
    "contracts":[
        {
            "configType":"721",//分别对应的合约： 721、1155、charge、authority
            "contractAbi":"",//合约abi
            "contractBytecode":"",//合约byteCode
            "contractAddress":"",//合约地址
            "signUserAddress":""// 签名用户地址
        },
        ...
    ]
}
```

gasPrice,gasLimit,可空。建议配置，减少请求网关次数。

该配置文件默认**全局有效**，如有特殊配置请参考以下示例。



### Per-request Configuration

所有请求方法都接受可选的'RequestOptions'对象，如有特殊配置请求信息请使用此对象。

```java
RequestOptions requestOptions = RequestOptions.builder()
    .setGateWayUrl("http://********/rpc")
    .setGasLimit("1000000")
    .build();

sdkClient.ddc721Service.mint("0x019ba4600e117f06e3726c0b100a2f10ec52339e", "ddcURI",options);
```



### Configure the （ethGetTransactionCount）nonce value

nonce 默认值根据配置文件中的：signUserAddress 从网关获取，如果调用频繁建议本地维护。

```
RequestOptions requestOptions = RequestOptions.builder().build();
requestOptions.setNonce("2");

sdkClient.ddc721Service.mint("0x019ba4600e117f06e3726c0b100a2f10ec52339e", "ddcURI",requestOptions);
```



### Configuring automatic retries

配置网络错误请求重试次数，该配置单次请求有效。

```java
RequestOptions requestOptions = RequestOptions.builder()
    .setNetworkRetries(2)
    .build();
```



### Configuring Timeouts

配置超时时间。

```java
RequestOptions requestOptions = RequestOptions.builder()
    .setReadTimeout(1000 * 3) // in milliseconds
    .setConnectTimeout(100)
    .build();
```

Default Configure：

```
public static final int DEFAULT_CONNECT_TIMEOUT = 30 * 1000;
public static final int DEFAULT_READ_TIMEOUT = 80 * 1000;
```

