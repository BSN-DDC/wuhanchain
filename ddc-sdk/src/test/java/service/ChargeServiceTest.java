package service;

import com.reddate.ddc.DDCSdkClient;
import com.reddate.ddc.listener.SignEventListener;
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

    SignEventListener signEventListener = event -> transactionSignature(event.getRawTransaction());

    DDCSdkClient  service =new DDCSdkClient();//.instance("src/main/resources/contractConfig.json", signEventListener);

    private static String transactionSignature(RawTransaction transaction) {
        String privateKey = "0x82ab01647229a2179307bc47bb030fc55b6f69a45167644173602641f1967d93";
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        return Numeric.toHexString(signedMessage);
    }

    @Test
    public void selfRecharge() throws Exception {
        BigInteger amount = new BigInteger("1000");
        String txHash = service.chargeService.selfRecharge(amount);
        log.info(txHash);
        assertNotNull(txHash);
    }


    @Test
    public void recharge() throws Exception {

        String to = "0x3A47C06aD3b400B23481958dB6F94C05887a3b4c";
        BigInteger amount = new BigInteger("5");

        String txHash = service.chargeService.recharge(to, amount);
        log.info(txHash);
        assertNotNull(txHash);

    }


    @Test
    public void balanceOf() throws Exception {
        String accAddr = "0x019ba4600e117f06e3726c0b100a2f10ec52339e";
        BigInteger amount = service.chargeService.balanceOf(accAddr);
        log.info(String.valueOf(amount));
        assertNotNull(amount);
    }


    @Test
    public void queryFee() throws Exception {
        String ddcAddr = "0xf1E985d2FFCC7c4DAA821010Ed17126683Ce1809";
        String sig = "0xd0def521";

        BigInteger fee = service.chargeService.queryFee(ddcAddr, sig);
        assertNotNull(fee);
        log.info(String.valueOf(fee));
    }


    @Test
    public void set721Fee() throws Exception {
        String ddcAddr = "0xc355Bf9BFBAA333c46dFB248CA7BBf7A3d3bBC28";
        BigInteger amount = new BigInteger("1");

        ArrayList<String> sigList = new ArrayList<>();
        sigList.add("0xe985e9c5");
        sigList.add("0xb88d4fde");
        sigList.add("0x081812fc");
        sigList.add("0x4cd88b76");
        sigList.add("0xd0def521");
        sigList.add("0xa22cb465");
        sigList.add("0x01ffc9a7");
        sigList.add("0x95d89b41");
        sigList.add("0x095ea7b3");
        sigList.add("0xd7a78db8");
        sigList.add("0x6352211e");
        sigList.add("0x715018a6");
        sigList.add("0x41044052");
        sigList.add("0xd302b0dc");
        sigList.add("0x70a08231");
        sigList.add("0x06fdde03");
        sigList.add("0x8da5cb5b");
        sigList.add("0xa419a333");
        sigList.add("0x23b872dd");
        sigList.add("0xf2fde38b");
        sigList.add("0x42966c68");
        sigList.add("0x293ec97c");

        for (int i = 0; i < sigList.size(); i++) {
            String txHash = service.chargeService.setFee(ddcAddr, sigList.get(i), amount);
            assertNotNull(txHash);
            log.info(txHash);
            Thread.sleep(1000 * 15);
        }

    }

    @Test
    public void set1155Fee() throws Exception {

        String ddcAddr = "0x108a1bda5faD74b8B0aB001d4909E887576EAE55";
        BigInteger amount = new BigInteger("1");

        ArrayList<String> sigList = new ArrayList<>();

        String mint = "0xd3fc9864";
        String mintBatch = "0x146d9ddc";
        String setApprovalForAll = "0xa22cb465";
        String isApprovedForAll = "0xe985e9c5";
        String safeTransferFrom = "0xf242432a";
        String safeBatchTransferFrom = "0x2eb2c2d6";
        String freeze = "0xd7a78db8";
        String unFreeze = "0xd302b0dc";
        String burn = "0x9dc29fac";
        String burnBatch = "0xb2dc5dc3";
        String balanceOf = "0x00fdd58e";
        String balanceOfBatch = "0x4e1273f4";
        String ddcURI = "0x293ec97c";

        sigList.add(mint);
        sigList.add(mintBatch);
        sigList.add(setApprovalForAll);
        sigList.add(isApprovedForAll);
        sigList.add(safeTransferFrom);
        sigList.add(safeBatchTransferFrom);
        sigList.add(freeze);
        sigList.add(unFreeze);
        sigList.add(burn);
        sigList.add(burnBatch);
        sigList.add(balanceOf);
        sigList.add(balanceOfBatch);
        sigList.add(ddcURI);

        for (int i = 0; i < sigList.size(); i++) {

            String txHash = service.chargeService.setFee(ddcAddr, sigList.get(i), amount);
            assertNotNull(txHash);
            log.info(txHash);
            Thread.sleep(1000 * 14);
        }

    }


    @Test
    public void delFee() throws Exception {

        String ddcAddr = "0x45BCf28556494fB116C4623f8F32091476933fe3";
        String sig = "0x293ec97c";

        String txHash = service.chargeService.delFee(ddcAddr, sig);
        log.info(txHash);
        assertNotNull(txHash);
    }


    @Test
    public void delDDC() throws Exception {

        String ddcAddr = "0x45BCf28556494fB116C4623f8F32091476933fe3";

        String txHash = service.chargeService.delDDC(ddcAddr);
        log.info(txHash);
        assertNotNull(txHash);
    }


}
