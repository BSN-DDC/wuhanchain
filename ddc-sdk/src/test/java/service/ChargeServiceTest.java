package service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.reddate.wuhanddc.DDCSdkClient;
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
public class ChargeServiceTest {

    // sign event listener
    SignEventListener signEventListener = event -> transactionSignature(event.getSender(), event.getRawTransaction());

    // ddcSdkClient instantiation
    DDCSdkClient ddcSdkClient = DDCSdkClient.builder()
            .setSignEventListener(signEventListener)
            .setAuthorityAddress("0xB746e96bC24bc9bC11515b6F39Cbe135d1b67a59")
            .setChargeAddress("0xf1b4db42b9a96CA2943C8e047552Fd6E05D55396")
            .setDdc721Address("0xb4B46D6B2C7BC4389759f9EBE141cFE086771561")
            .setDdc1155Address("0x5Bf9e07aBBF0cFbF21d02065529AE10e2Ef0a375")
            .setCrossChainAddress("0x6ca34e1bFcC9A36113DdCE0D76d35E71dBbdd770")
            .setChainId(BigInteger.valueOf(5555))
            .build();

    // 运营方
    public static String sender = "0x6922D8af46d5e39c2a15cAa26eE692FCc118aDc5";

    static {
        DDCWuhan.setGatewayUrl("https://opbtest.bsngate.com:18602/api/4bbed86d890f42b6b70de34c9be425dd/rpc");
        DDCWuhan.setNonceManagerAddress(sender);
    }

    private static String transactionSignature(String sender, RawTransaction transaction) {

        String privateKey = "...";
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        return Numeric.toHexString(signedMessage);
    }

    @Test
    public void selfRecharge() throws Exception {
        // recharge amount
        BigInteger amount = new BigInteger("9999999999999999999999");
        String txHash = ddcSdkClient.chargeService.selfRecharge(sender, amount);
        log.info(txHash);
        assertNotNull(txHash);
    }


    @Test
    public void recharge() throws Exception {
        // recharge account address
        String to = "0x6da7e501dc26d8aa0d5a8bdec6deecd0c5f18343";
        BigInteger amount = new BigInteger("99");

        String txHash = ddcSdkClient.chargeService.recharge(sender, to, amount);
        log.info(txHash);
        assertNotNull(txHash);

    }

    @Test
    public void rechargeBatch() throws Exception {
        Multimap<String, BigInteger> map = ArrayListMultimap.create();
        map.put("0x02a66ef232dac0cd4590d3af2ddb9c2cd95eccc1", new BigInteger("1"));
        map.put("0x201ea42500d8ff71cd897ca51269c0c4e5680aaa", new BigInteger("2"));

        String txHash = ddcSdkClient.chargeService.rechargeBatch(sender, map);
        log.info(txHash);
        assertNotNull(txHash);

    }

    @Test
    public void balanceOf() throws Exception {
        // account address
        String accAddr = "0x6da7e501dc26d8aa0d5a8bdec6deecd0c5f18343";
        BigInteger amount = ddcSdkClient.chargeService.balanceOf(accAddr);
        log.info(String.valueOf(amount));
        assertNotNull(amount);
    }

    @Test
    public void balanceOfBatch() throws Exception {

        List<String> addresses = new ArrayList<>();
        String accAddr1 = "0x28b6264066ce89e28379066bec3f6545ba719b61";
        String accAddr2 = "0xb69d87cae302774ff16874183172cef8984249e6";

        addresses.add(accAddr1);
        addresses.add(accAddr2);
        List<BigInteger> amounts = ddcSdkClient.chargeService.balanceOfBatch(addresses);
        assertNotNull(amounts);
        log.info(String.valueOf(amounts));
    }

    @Test
    public void queryFee() throws Exception {

        //String ddcAddr = "0x5Bf9e07aBBF0cFbF21d02065529AE10e2Ef0a375"; // 1155 contract


        String ddcAddr = "0x983d935D626a97eD219D95b11d36082b9D1A4A2d"; // 721 contract


        String sig = "0x08ceb5ad"; //1155
        //String sig = "0xf6dda936"; // 721 contract

        BigInteger fee = ddcSdkClient.chargeService.queryFee(ddcAddr, sig);
        assertNotNull(fee);
        log.info(String.valueOf(fee));
    }


    @Test
    public void set721Fee() throws Exception {

        // 721 contract
        String ddcAddr = "0x983d935D626a97eD219D95b11d36082b9D1A4A2d";

        // fee
        BigInteger amount = new BigInteger("120");

        ArrayList<String> sigList = new ArrayList<>();
        sigList.add("0x08ceb5ad");

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

    @Test
    public void setSwitcherStateOfBatch() throws Exception {
        boolean isOpen = true;
        String txHash = ddcSdkClient.chargeService.setSwitcherStateOfBatch(sender, isOpen);
        assertNotNull(txHash);
        log.info(txHash);
    }


}
