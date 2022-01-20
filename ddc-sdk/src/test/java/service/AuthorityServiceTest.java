package service;

import com.alibaba.fastjson.JSONObject;
import com.reddate.ddc.DDCSdkClient;
import com.reddate.ddc.dto.ddc.AccountInfo;
import com.reddate.ddc.dto.ddc.AccountState;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.listener.SignEventListener;
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
    DDCSdkClient ddcSdkClient = new DDCSdkClient().instance("src/main/resources/contractConfig.json", signEventListener);

    //  The address the transaction is send from.
    public String sender = "0x24a95d34dcbc74f714031a70b077e0abb3306066";

    private static String transactionSignature(String sender, RawTransaction transaction) {
        // sender: Obtain the private key according to the sender and complete its signature

        //sender privateKey
        String privateKey = "0x20bd77e9c6c920cba10f4ef3fdd10e0cfbf8a4781292d8c8d61e37458445888";
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        return Numeric.toHexString(signedMessage);
    }


    @Test
    public void addAccountByOperator() throws Exception {
        try {
            String account = "0x2c14db41e0077b3070c238463e07d4e3c22830fb";
            String accountName = "ces221";
            String accountDID = "did:bsn:12345611221";
            String leaderDID = "did:bsn:2JUxRPDDM6kL2UJE8GYpufkDjnXv";
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


}
