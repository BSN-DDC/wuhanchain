package service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

@Slf4j
class DDC1155ServiceTest {

    SignEventListener signEventListener = event -> transactionSignature(event.getRawTransaction());

    DDCSdkClient ddcSdkClient = new DDCSdkClient().instance("src/main/resources/contractConfig.json", signEventListener);

    private String transactionSignature(RawTransaction transaction) {
        String privateKey = "0x82ab01647229a2179307bc47bb030fc55b6f69a45167644173602641f1967d93";
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        String HexSignature = Numeric.toHexString(signedMessage);
        return HexSignature;
    }


    @Test
    void mint() throws Exception {

        byte[] data = new byte[1];
        data[0] = 1;
        String tx = ddcSdkClient.ddc1155Service.mint("0x24a95d34dcbc74f714031a70b077e0abb3306066", BigInteger.TEN, "Token-R888-5-1-111", data);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void mintBatch() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;
        Multimap<BigInteger, String> map = ArrayListMultimap.create();
        map.put(new BigInteger("10"), "ddc-1");
        map.put(new BigInteger("20"), "ddc-2");
        String tx = ddcSdkClient.ddc1155Service.mintBatch("0x9d37d92d3bca605a49f21642c309e578b16040fd", map, data);
        log.info(tx);
        assertNotNull(tx);
    }


    @Test
    void setApprovalForAll() throws Exception {
        String tx = ddcSdkClient.ddc1155Service.setApprovalForAll("0x4655399c9c082304fe7a0af145c490f52d87d732", true);
        log.info(tx);
        assertNotNull(tx);

    }

    @Test
    void isApprovedForAll() throws Exception {
        Boolean tx = ddcSdkClient.ddc1155Service.isApprovedForAll("0x019ba4600e117f06e3726c0b100a2f10ec52339e", "0x4655399c9c082304fe7a0af145c490f52d87d732");
        log.info(String.valueOf(tx));
        assertNotNull(tx);
    }

    @Test
    void safeTransferFrom() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;
        String tx = ddcSdkClient.ddc1155Service.safeTransferFrom("0x019ba4600e117f06e3726c0b100a2f10ec52339e", "0x4655399c9c082304fe7a0af145c490f52d87d732", new BigInteger("8"), new BigInteger("1"), data);
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
        String tx = ddcSdkClient.ddc1155Service.safeBatchTransferFrom("0x019ba4600e117f06e3726c0b100a2f10ec52339e", "0x4655399c9c082304fe7a0af145c490f52d87d732", ddcInfos, datas);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void freeze() throws Exception {
        String tx = ddcSdkClient.ddc1155Service.freeze(new BigInteger("7"));
        log.info(tx);
        assertNotNull(tx);
    }


    @Test
    void unFreeze() throws Exception {
        String tx = ddcSdkClient.ddc1155Service.unFreeze(new BigInteger("7"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void burn() throws Exception {
        String tx = ddcSdkClient.ddc1155Service.burn("0x24a95d34dcbc74f714031a70b077e0abb3306066", new BigInteger("203"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void burnBatch() throws Exception {
        ArrayList<BigInteger> arrayList = new ArrayList();
        arrayList.add(new BigInteger("6"));
        arrayList.add(new BigInteger("7"));

        String tx = ddcSdkClient.ddc1155Service.burnBatch("0x019ba4600e117f06e3726c0b100a2f10ec52339e", arrayList);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void balanceOf() throws Exception {
        BigInteger bigInteger = ddcSdkClient.ddc1155Service.balanceOf("0x019ba4600e117f06e3726c0b100a2f10ec52339e", new BigInteger("1"));
        log.info(bigInteger.toString());
        assertNotNull(bigInteger);
    }

    @Test
    void balanceOfBatch() throws Exception {
        Multimap<String, BigInteger> map = ArrayListMultimap.create();
        for (int i = 82; i < 85; i++) {
            map.put("0x9dff125d6562df4d72b9bd4616c815a2b45c39ab", new BigInteger(String.valueOf(i)));
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

}