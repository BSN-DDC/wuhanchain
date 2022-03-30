package service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.reddate.wuhanddc.DDCSdkClient;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class DDC1155ServiceTest {

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
    void safeMint() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;
        String tx = ddcSdkClient.ddc1155Service.safeMint(sender, "0x24a95d34dcbc74f714031a70b077e0abb3308088", BigInteger.TEN, "Token-R88821", data);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void safeMintBatch() throws Exception {
        try {
            byte[] data = new byte[1];
            data[0] = 1;
            Multimap<BigInteger, String> map = ArrayListMultimap.create();
            map.put(new BigInteger("11"), "11");
            map.put(new BigInteger("22"), "12");

            String tx = ddcSdkClient.ddc1155Service.safeMintBatch(sender, "0x24a95d34dcbc74f714031a70b077e0abb3308088", map, data);
            log.info(tx);
            assertNotNull(tx);
        } catch (DDCException e) {
            System.out.println(e.getMsg());
        }
    }


    @Test
    void setApprovalForAll() throws Exception {
        String tx = ddcSdkClient.ddc1155Service.setApprovalForAll(sender, "0x24a95d34dcbc74f714031a70b077e0abb3308088", true);
        log.info(tx);
        assertNotNull(tx);

    }

    @Test
    void isApprovedForAll() throws Exception {
        Boolean tx = ddcSdkClient.ddc1155Service.isApprovedForAll("0x24a95d34dcbc74f714031a70b077e0abb3308088", "0x4655399c9c082304fe7a0af145c490f52d87d732");
        log.info(String.valueOf(tx));
        assertNotNull(tx);
    }

    @Test
    void safeTransferFrom() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;
        String tx = ddcSdkClient.ddc1155Service.safeTransferFrom(sender, "0x24a95d34dcbc74f714031a70b077e0abb3308088", "0x4655399c9c082304fe7a0af145c490f52d87d732", new BigInteger("8"), new BigInteger("1"), data);
        log.info(tx);
        assertNotNull(tx);
    }


    @Test
    void safeBatchTransferFrom() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;
        ArrayList<byte[]> datas = new ArrayList<>();
        datas.add(data);

        Map<BigInteger, BigInteger> ddcInfos = new HashMap<>();
        ddcInfos.put(new BigInteger("7"), new BigInteger("1"));
        String tx = ddcSdkClient.ddc1155Service.safeBatchTransferFrom(sender, "0x24a95d34dcbc74f714031a70b077e0abb3308088", "0x4655399c9c082304fe7a0af145c490f52d87d732", ddcInfos, datas);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void freeze() throws Exception {
        String tx = ddcSdkClient.ddc1155Service.freeze(sender, new BigInteger("7"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void unFreeze() throws Exception {
        String tx = ddcSdkClient.ddc1155Service.unFreeze(sender, new BigInteger("7"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void burn() throws Exception {
        String tx = ddcSdkClient.ddc1155Service.burn(sender, "0x24a95d34dcbc74f714031a70b077e0abb3308088", new BigInteger("203"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void burnBatch() throws Exception {
        ArrayList<BigInteger> arrayList = new ArrayList();
        arrayList.add(new BigInteger("6"));
        arrayList.add(new BigInteger("7"));

        String tx = ddcSdkClient.ddc1155Service.burnBatch(sender, "0x24a95d34dcbc74f714031a70b077e0abb3308088", arrayList);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void balanceOf() throws Exception {
        BigInteger bigInteger = ddcSdkClient.ddc1155Service.balanceOf("0x24a95d34dcbc74f714031a70b077e0abb3308088", new BigInteger("1"));
        log.info(bigInteger.toString());
        assertNotNull(bigInteger);
    }

    @Test
    void balanceOfBatch() throws Exception {
        Multimap<String, BigInteger> map = ArrayListMultimap.create();
        for (int i = 82; i < 85; i++) {
            map.put("0x24a95d34dcbc74f714031a70b077e0abb3308088", new BigInteger(String.valueOf(i)));
        }

        List<BigInteger> bigIntegerList = ddcSdkClient.ddc1155Service.balanceOfBatch(map);
        assertNotNull(bigIntegerList);
        bigIntegerList.forEach(t -> {
            log.info(String.valueOf(t));
        });
    }

    @Test
    void ddcURI() throws Exception {
        String result = ddcSdkClient.ddc1155Service.ddcURI(new BigInteger("7"));
        assertNotNull(result);
        log.info("URL: {}", result);
    }

    @Test
    void setURI() throws Exception {
        String tx = ddcSdkClient.ddc1155Service.setURI(sender ,new BigInteger(""),"");
        log.info(tx);
        assertNotNull(tx);

    }

}