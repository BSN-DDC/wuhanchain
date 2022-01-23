package service;

import com.reddate.ddc.DDCSdkClient;
import com.reddate.ddc.listener.SignEventListener;
import com.reddate.ddc.net.DDCWuhan;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;

@Slf4j
public class ChargeServiceTest {

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
    public void selfRecharge() throws Exception {
        // recharge amount
        BigInteger amount = new BigInteger("1000");
        String txHash = ddcSdkClient.chargeService.selfRecharge(sender, amount);
        log.info(txHash);
        assertNotNull(txHash);
    }


    @Test
    public void recharge() throws Exception {
        // recharge account address
        String to = "0x3A47C06aD3b400B23481958dB6F94C05887a3b4c";
        BigInteger amount = new BigInteger("5");

        String txHash = ddcSdkClient.chargeService.recharge(sender, to, amount);
        log.info(txHash);
        assertNotNull(txHash);

    }


    @Test
    public void balanceOf() throws Exception {
        // account address
        String accAddr = "0x019ba4600e117f06e3726c0b100a2f10ec52339e";
        BigInteger amount = ddcSdkClient.chargeService.balanceOf(accAddr);
        log.info(String.valueOf(amount));
        assertNotNull(amount);
    }


    @Test
    public void queryFee() throws Exception {
        // 721 contract
        String ddcAddr = "0xb4B46D6B2C7BC4389759f9EBE141cFE086771561";

        // mint sig
        String sig = "0xd0def521";

        BigInteger fee = ddcSdkClient.chargeService.queryFee(ddcAddr, sig);
        assertNotNull(fee);
        log.info(String.valueOf(fee));
    }


    @Test
    public void set721Fee() throws Exception {

        // 721 contract
        String ddcAddr = "0xb4B46D6B2C7BC4389759f9EBE141cFE086771561";

        // fee
        BigInteger amount = new BigInteger("100");

        ArrayList<String> sigList = new ArrayList<>();
        sigList.add("0xe985e9c5");

        for (int i = 0; i < sigList.size(); i++) {
            String txHash = ddcSdkClient.chargeService.setFee(sender, ddcAddr, sigList.get(i), amount);
            assertNotNull(txHash);
            log.info(txHash);
        }

    }

    @Test
    public void set1155Fee() throws Exception {

        // 1155 contract
        String ddcAddr = "0x5Bf9e07aBBF0cFbF21d02065529AE10e2Ef0a375";

        // fee
        BigInteger amount = new BigInteger("1");

        ArrayList<String> sigList = new ArrayList<>();

        // safeMint sig
        String safeMint = "0xb55bc617";
        sigList.add(safeMint);

        for (int i = 0; i < sigList.size(); i++) {

            String txHash = ddcSdkClient.chargeService.setFee(sender, ddcAddr, sigList.get(i), amount);
            assertNotNull(txHash);
            log.info(txHash);
            Thread.sleep(1000 * 14);
        }

    }


    @Test
    public void delFee() throws Exception {

        // 1155 contract
        String ddcAddr = "0x5Bf9e07aBBF0cFbF21d02065529AE10e2Ef0a375";

        // safeMint sig
        String sig = "0xb55bc617";

        String txHash = ddcSdkClient.chargeService.delFee(sender, ddcAddr, sig);
        log.info(txHash);
        assertNotNull(txHash);
    }


    @Test
    public void delDDC() throws Exception {

        // 1155 contract
        String ddcAddr = "0x5Bf9e07aBBF0cFbF21d02065529AE10e2Ef0a375";

        String txHash = ddcSdkClient.chargeService.delDDC(sender, ddcAddr);
        log.info(txHash);
        assertNotNull(txHash);
    }


}
