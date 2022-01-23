package service;

import com.alibaba.fastjson.JSONObject;
import com.reddate.ddc.DDCSdkClient;
import com.reddate.ddc.dto.ddc.AccountInfo;
import com.reddate.ddc.dto.ddc.AccountState;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.listener.SignEventListener;
import com.reddate.ddc.net.DDCWuhan;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

import static org.junit.Assert.assertNotNull;

@Slf4j
public class AuthorityServiceTest {

    // sign event listener
    SignEventListener signEventListener = event -> transactionSignature(event.getSender(), event.getRawTransaction());

    // ddcSdkClient instantiation
    DDCSdkClient ddcSdkClient = new DDCSdkClient().instance(signEventListener);

    //  The address the transaction is send from.
    public String sender = "0x3a0427c496c7e9408885d132e9fec0b042beb399";

    static {
        DDCWuhan.setGatewayUrl("https://opbtest.bsngate.com:18602/api/4bbed86d890f42b6b70de34c9be425dd/rpc");
    }

    private static String transactionSignature(String sender, RawTransaction transaction) {
        // sender: Obtain the private key according to the sender and complete its signature

        //sender privateKey
        String privateKey = "0x8f9d8a1619e35892cd36acba0150fd3e2aac8a20865eff75b2500ea7661a1076";
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        return Numeric.toHexString(signedMessage);
    }


    @Test
    public void addAccountByOperator() throws Exception {

        try {
            String account = "0x154ebd1dc5832a521eac43bc982005ddd76eb73e";
            String accountName = "ces2211";
            String accountDID = "did:bsn:123456112211";
            String leaderDID = "";
            String txHash = ddcSdkClient.authorityService.addAccountByOperator(sender, account, accountName, accountDID, leaderDID);
            log.info(txHash);
            assertNotNull(txHash);
        } catch (DDCException e) {
            System.out.println(e.getMsg());
        }
    }

    @Test
    public void getAccount() throws Exception {
        AccountInfo accountInfo = ddcSdkClient.authorityService.getAccount("0x36fa15b35667eb407a60e05cb16b8b7b306cef1a");
        assertNotNull(accountInfo);
        log.info(JSONObject.toJSONString(accountInfo));
    }


    @Test
    public void updateAccState() throws Exception {
        String account = "0xa7ae002e653239f1c16817bd5d5bebc3fd1d30b3";

        String txHash = ddcSdkClient.authorityService.updateAccState(sender, account, AccountState.Active, false);
        assertNotNull(txHash);
        log.info(txHash);
    }


    @Test
    public void crossPlatformApproval() throws Exception {

        String txHash = ddcSdkClient.authorityService.crossPlatformApproval(sender, "0xa7ae002e653239f1c16817bd5d5bebc3fd1d30b3", "0xa7ae002e653239f1c16817bd5d5bebc3fd1d30b3", false);
        assertNotNull(txHash);
        log.info(txHash);
    }

}
