# wuhanchain java client library



### Requirements

-   **Java 1.8 or later**



### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
    <groupId>com.reddate.wuhanddccom.reddate.wuhanddc</groupId>
    <artifactId>ddc-sdk-wuhan</artifactId>
    <version>0.0.1</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/ddc-sdk-wuhan.jar</systemPath>
</dependency>

<dependency>
    <groupId>org.fisco.bcos</groupId>
    <artifactId>web3sdk</artifactId>
    <version>2.4.0</version>
    <scope>system</scope>
    <systemPath>${basedir}/lib/web3sdk.jar</systemPath>
</dependency>

<dependency>
    <groupId>org.fisco.solc</groupId>
    <artifactId>solcJ</artifactId>
    <version>0.6.10.0</version>
    <scope>system</scope>
    <systemPath>${basedir}/lib/solcJ-0.6.10.0.jar</systemPath>
</dependency>
```

## Documentation

Gateway Access Instructions：[BSN Gateway](https://bsnbase.com/static/tmpFile/bzsc/openper/7-3-3.html)

Contract development information：[ETH Developers Docs](https://ethereum.org/zh/developers/docs/)

## Usage

SdkExampleTest.java

```java
package service;

import com.reddate.wuhanddc.DDCSdkClient;
import com.reddate.wuhanddc.dto.ddc.Account;
import com.reddate.wuhanddc.listener.SignEventListener;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertNotNull;


public class SdkExampleTest {

    // sign event listener
    SignEventListener signEventListener = event -> transactionSignature(event.getSender(), event.getRawTransaction());

    // ddcSdkClient instantiation
    DDCSdkClient ddcSdk = new DDCSdkClient().instance(signEventListener);

    // set gateway url
    static {
        DDCWuhan.setGatewayUrl("https://opbningxia.bsngate.com:18602/api/[projectId]/rpc");
    }

    //  The address the transaction is send from.
    public String sender = "0x24a95d34dcbc74f714031a70b077e0abb3308088";

    private static String transactionSignature(String sender, RawTransaction transaction) {
        // sender: Obtain the privateKey according to the sender and complete its signature

        // sender privateKey
        String privateKey = "0x20bd77e9c6c920cba10f4ef3fdd10e0cfbf8a4781292d8c8d61e37458445888";
        Credentials credentials = Credentials.create(privateKey);

        /**
         * 5555
         * wuhanchain id,call example
         * curl -X POST --data '{"jsonrpc":"2.0","method":"eth_chainId","params":[],"id":1}'
         */
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        return Numeric.toHexString(signedMessage);
    }

    /**
     * DDC721 mint
     * @throws Exception
     */
    @Test
    void mint() throws Exception {
        String tx = ddcSdk.ddc721Service.mint(sender, "0x24a95d34dcbc74f714031a70b077e0abb3308088", "ddcURI");
        assertNotNull(tx);
    }

    /**
     * DDC1155 safeMint
     * @throws Exception
     */
    @Test
    void safeMint() throws Exception {

        byte[] data = new byte[1];
        data[0] = 1;
        String tx = ddcSdk.ddc1155Service.safeMint(sender, "0x24a95d34dcbc74f714031a70b077e0abb3308088", BigInteger.TEN, "Token-R88821", data);
        assertNotNull(tx);

    }

    /**
     * create account
     */
    @Test
    void createAccount() {
        Account account = ddcSdk.accountService.createAccount();
        assertNotNull(account);
    }

}
```



### Configuration

### Per-request Configuration

All of the request methods accept an optional `RequestOptions` object. This is used if you want to set the gateway address etc.

gasPrice,gasLimit,Nullable. It is recommended to configure to reduce the number of requests to the gateway.

```java
// mint
sdkClient.ddc721Service.mint("0x24a95d34dcbc74f714031a70b077e0abb3308088", "ddcURI");

// use options mint
RequestOptions options = RequestOptions.builder()
        .setGasLimit("1000000")
        .build();
sdkClient.ddc721Service.mint("0x24a95d34dcbc74f714031a70b077e0abb3308088", "ddcURI",options);
```



### Configure gateway

##### gateway URL

gateway url must be set

```
DDCWuhan.setGatewayUrl("https://opbningxia.bsngate.com:18602/api/[projectId]/rpc");
```

##### x-api-key

If you enable the project key in the BSN portal, it needs to be configured in the sdk,This configuration takes effect globally.

```
DDCWuhan.setGatewayApiKey("d8438f145351511503f572d632");
```



### Configuring automatic retries

Automatic retriescan be configured globally:

```
DDCWuhan.setMaxNetworkRetries(5);
```

Or on a finer grain level using `RequestOptions`:

```java
RequestOptions requestOptions = RequestOptions.builder()
        .setNetworkRetries(2)
        .build();
```



### Configuring Timeouts

Connect and read timeouts can be configured globally:

```
DDCWuhan.setConnectTimeout(10 * 1000); // in milliseconds
DDCWuhan.setReadTimeout(10 * 1000);
```

Or on a finer grain level using `RequestOptions`:

```
RequestOptions requestOptions = RequestOptions.builder()
    .setConnectTimeout(10 * 1000) // in milliseconds
    .build();
```



### Configure the （ethGetTransactionCount）nonce value

The default value of nonce is obtained from the gateway according to: signUserAddress in the configuration file. If calling frequently recommends local maintenance, use RequestOptions to pass this parameter.

```
RequestOptions options = RequestOptions.builder().build();
requestOptions.setNonce("2");
```

