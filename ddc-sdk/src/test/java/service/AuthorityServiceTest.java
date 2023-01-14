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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@Slf4j
public class AuthorityServiceTest {

    // sign event listener
    SignEventListener signEventListener = event -> transactionSignature(event.getSender(), event.getRawTransaction());

    // ddcSdkClient instantiation
    DDCSdkClient ddcSdkClient = DDCSdkClient.builder()
            .setSignEventListener(signEventListener)
            .setAuthorityAddress("0x466D5b0eA174a2DD595D40e0B30e433FCe6517F5")
            .setChargeAddress("0xCa97bF3a19403805d391102908665b16B4d0217C")
            .setDdc721Address("0xad3B52B4F4bd9198DC69dD9cE4aC9846667461a2")
            .setDdc1155Address("0x061e59c74815994DAb4226a0D344711F18E0F418")
            .setCrossChainAddress("0xc4E12bB845D9991ee26718E881C712B2c0cB2048")
            .setChainId(BigInteger.valueOf(5555))
            .build();


//     // The address the transaction is send from.
//    // 运营方
//    public String sender = "0x6922d8af46d5e39c2a15caa26ee692fcc118adc5";
//
//    static {
//        DDCWuhan.setGatewayUrl("https://opbtest.bsngate.com:18602/api/4bbed86d890f42b6b70de34c9be425dd/rpc");
//    }
//
//    private static String transactionSignature(String sender, RawTransaction transaction) {
//
//        String privateKey = "...";
//        Credentials credentials = Credentials.create(privateKey);
//        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
//        return Numeric.toHexString(signedMessage);
//    }

    // 平台方
    public static String sender = "0xd17104BB8f8B253a04e0aC1F40312a627a4d2a80";

    static {
        DDCWuhan.setGatewayUrl("https://opbningxia.bsngate.com:18602/api/[projectId]/rpc");
        DDCWuhan.setNonceManagerAddress(sender);
    }

    private static String transactionSignature(String sender, RawTransaction transaction) {
        // sender: Obtain the private key according to the sender and complete its signature

        String privateKey = "...";
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        return Numeric.toHexString(signedMessage);
    }

    @Test
    public void setSwitcherStateOfPlatform() throws Exception {

        try {
            boolean isOpen = true;
            String txHash = ddcSdkClient.authorityService.setSwitcherStateOfPlatform(sender, isOpen);
            log.info(txHash);
            assertNotNull(txHash);
        } catch (DDCException e) {
            System.out.println(e.getMsg());
        }
    }

    @Test
    public void switcherStateOfPlatform() throws Exception {
        boolean status = ddcSdkClient.authorityService.switcherStateOfPlatform();
        assertNotNull(status);
        log.info(status + "");
    }

    @Test
    public void addAccountByPlatform() throws Exception {
        try {
            String account = "0x4199137a1ffdb01829f9447d5620ac9fc160ca3b";
            String accountName = "crosschain-platform";
            String accountDID = "crosschain-88888888";

            String txHash = ddcSdkClient.authorityService.addAccountByPlatform(sender, account, accountName, accountDID);
            log.info(txHash);
            assertNotNull(txHash);
        } catch (DDCException e) {
            System.out.println(e.getMsg());
        }
    }

    @Test
    public void addBatchAccountByPlatform() throws Exception {
        try {
            List<AccountInfo> accounts = new ArrayList<>();

            AccountInfo account1 = new AccountInfo();
            AccountInfo account2 = new AccountInfo();

            account1.setAccount("0x5299Be6bcA7c4CDacD7Ca1D964Ed977c13f1a888");
            account1.setAccountName("account-111");
            account1.setAccountDID("account-did-111");

            account2.setAccount("0x0690DC12143A7166735d86BF95B970a1Bdf8e777");
            account2.setAccountName("account-222");
            account2.setAccountDID("account-did-222");

            accounts.add(account1);
            accounts.add(account2);
            String txHash = ddcSdkClient.authorityService.addBatchAccountByPlatform(sender, accounts);
            log.info(txHash);
            assertNotNull(txHash);
        } catch (DDCException e) {
            System.out.println(e.getMsg());
        }
    }

    @Test
    public void addAccountByOperator() throws Exception {

        try {
            String account = "0xf3328573721eb7e217dffb9c76f86c84163cec50";
            String accountName = "0xf3328573721eb7e217dffb9c76f86c84163cec50";
            String accountDID = "did:bsn:0xf3328573721eb7e217dffb9c76f86c84163cec50";
            /**
             * leaderDID 为空代表是平台方
             * leaderDID 不为空，代表是终端用户
             */
            String leaderDID = "";
            String txHash = ddcSdkClient.authorityService.addAccountByOperator(sender, account, accountName, accountDID, leaderDID);
            log.info(txHash);
            assertNotNull(txHash);
        } catch (DDCException e) {
            log.error(e.getMsg(), e);
            System.out.println(e.getMsg());
        }
    }

    @Test
    public void addBatchAccountByOperator() throws Exception {

        List<AccountInfo> accounts = new ArrayList<>();

        AccountInfo account1 = new AccountInfo();
        AccountInfo account2 = new AccountInfo();

        account1.setAccount("0x99161d531B1bB82778F2BA149034264D0dD13F93");//
        account1.setAccountName("account-a-1");
        account1.setAccountDID("account-d-a");

        account2.setAccount("0x68972ACeB380355f06578BfE31bdbBb389649bC2");//
        account2.setAccountName("account-a-2");
        account2.setAccountDID("account-d-a2");
        account2.setLeaderDID("account-a-1");

        accounts.add(account1);
        accounts.add(account2);

        try {
            String txHash = ddcSdkClient.authorityService.addBatchAccountByOperator(sender, accounts);
            log.info(txHash);
            assertNotNull(txHash);
        } catch (DDCException e) {
            System.out.println(e.getMsg());
        }
    }


    @Test
    public void getAccount() throws Exception {
        AccountInfo accountInfo = ddcSdkClient.authorityService.getAccount("0xadf8d413d7fbb62f4237a1ce86bc90655ba995a9");
        assertNotNull(accountInfo);
        log.info(JSONObject.toJSONString(accountInfo));
    }


    @Test
    public void updateAccState() throws Exception {
        String account = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";

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

    @Test
    public void syncPlatformDID() throws Exception {
        List<String> dids = new ArrayList<>();
        dids.add("did:bsn:2JUxRPDDM6kL2UJE8GY88fkD222");
        dids.add("did:bsn:2JUxRPDDM6kL2UJE8GY88fkD111");
        String txHash = ddcSdkClient.authorityService.syncPlatformDID(sender, dids);
        assertNotNull(txHash);
        log.info(txHash);
    }

    @Test
    public void setSwitcherStateOfBatch() throws Exception {
        boolean isOpen = true;
        String txHash = ddcSdkClient.authorityService.setSwitcherStateOfBatch(sender, isOpen);
        assertNotNull(txHash);
        log.info(txHash);
    }

}
