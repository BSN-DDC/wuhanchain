package service;

import com.alibaba.fastjson.JSONObject;
import com.reddate.ddc.DDCSdkClient;
import com.reddate.ddc.dto.ddc.AccountInfo;
import com.reddate.ddc.dto.ddc.AccountState;
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

    SignEventListener signEventListener = event -> transactionSignature(event.getRawTransaction());

    DDCSdkClient  ddcSdkClient = new DDCSdkClient().instance("src/main/resources/contractConfig.json", signEventListener);

    private static String transactionSignature(RawTransaction transaction) {
        String privateKey = "0x82ab01647229a2179307bc47bb030fc55b6f69a45167644173602641f1967d93";
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        return Numeric.toHexString(signedMessage);
    }

    @Test
    public void addAccount() throws Exception {

        String account = "0x7a88851860d34bf71f628b4f3fb4829305948c7c";
        String accountName = "user-b1-1-121";
        String accountDID = "234sdfsdf33-211";

        String txHash = ddcSdkClient.authorityService.addAccount(account, accountName, accountDID);
        log.info(txHash);
        assertNotNull(txHash);
    }


    @Test
    public void addConsumerByOperator() throws Exception {
        String account = "0x705e01fae3a1bc9e81466c2e600556b752c0d72f";
        String accountName = "platform-b-user-4-1";
        String accountDID = "b-user-1-4-1";
        String leaderDID = "platform-b-b";
        String txHash = ddcSdkClient.authorityService.addConsumerByOperator(account, accountName, accountDID, leaderDID);
        log.info(txHash);
        assertNotNull(txHash);
    }

    @Test
    public void getAccount() throws Exception {
        AccountInfo accountInfo = ddcSdkClient.authorityService.getAccount("0xa7ae002e653239f1c16817bd5d5bebc3fd1d30b3");
        assertNotNull(accountInfo);
        log.info(JSONObject.toJSONString(accountInfo));
    }


    @Test
    public void updateAccState() throws Exception {
        String account = "0xa7ae002e653239f1c16817bd5d5bebc3fd1d30b3";

        String txHash = ddcSdkClient.authorityService.updateAccState(account, AccountState.Active, false);
        assertNotNull(txHash);
        log.info(txHash);
    }


}
