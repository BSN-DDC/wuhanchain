package service;

import com.alibaba.fastjson.JSONObject;
import com.reddate.wuhanddc.DDCSdkClient;
import com.reddate.wuhanddc.dto.ddc.AccountInfo;
import com.reddate.wuhanddc.dto.ddc.AccountState;
import com.reddate.wuhanddc.exception.DDCException;
import com.reddate.wuhanddc.listener.SignEventListener;
import com.reddate.wuhanddc.net.DDCWuhan;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class AuthorityServiceTest {

    // sign event listener
    SignEventListener signEventListener = event -> transactionSignature(event.getSender(), event.getRawTransaction());

    // ddcSdkClient instantiation
    DDCSdkClient ddcSdkClient = new DDCSdkClient().instance(signEventListener);

    //  The address the transaction is send from.
    public String sender = "0x24a95d34dcbc74f714031a70b077e0abb3308088";

    // set gateway url
    static {
        DDCWuhan.setGatewayUrl("https://opbtest.bsngate.com:18602/api/4bbed86d895422b6b70de34c854si5dd/rpc");
    }

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
            String account = "0x24a95d34dcbc74f714031a70b077e0abb3308088";
            String accountName = "bsn-ddc";
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
        AccountInfo accountInfo = ddcSdkClient.authorityService.getAccount("0x24a95d34dcbc74f714031a70b077e0abb3308088");
        assertNotNull(accountInfo);
        log.info(JSONObject.toJSONString(accountInfo));
    }


    @Test
    public void updateAccState() throws Exception {
        String account = "0x24a95d34dcbc74f714031a70b077e0abb3308088";

        String txHash = ddcSdkClient.authorityService.updateAccState(sender, account, AccountState.Active, false);
        assertNotNull(txHash);
        log.info(txHash);
    }


    @Test
    public void crossPlatformApproval() throws Exception {

        String txHash = ddcSdkClient.authorityService.crossPlatformApproval(sender, "0x24a95d34dcbc74f714031a70b077e0abb3308088", "0x24a95d34dcbc74f714031a70b077e0abb3306066", false);
        assertNotNull(txHash);
        log.info(txHash);
    }

}
