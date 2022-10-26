package com.reddate.wuhanddc.eip712;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class DDC1155MetaTransactionTest {

    String privateKey = "0xb1e104c4c3d74dc4a9131cde8f3619c1c2d68a442da19dc146159eae0311d8b1";
    DDC1155MetaTransaction metaTransaction = DDC1155MetaTransaction.builder()
            .setChainId(BigInteger.valueOf(5555))
            .setContractAddress("0x061e59c74815994DAb4226a0D344711F18E0F418")
            .build();

//    @Test
//    void getDomainSeparator() {
//        log.info("DDC1155 Domain: {}", metaTransaction.getDomainSeparator());
//    }

    @Test
    void generateSignature() {
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        BigInteger amount = BigInteger.valueOf(1);
        String ddcURI = "http://ddcUrl";
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);
        byte[] data = Numeric.hexStringToByteArray("0x16");

        String digest = metaTransaction.getSafeMintDigest(to, amount, ddcURI, data, nonce, deadline);
        byte[] signature = metaTransaction.generateSignature(privateKey, digest);
        log.info("Mint sign: {}", Numeric.toHexString(signature));
        assertEquals("0xd89e2029cb399c4300e6da7722f2dbda516ebde507002d432393b5b3633989537e2cd2ae86ce0d1583dfd430edc49fcc95a519baf95f87d483b3f63bbae6e0ca1b", Numeric.toHexString(signature));
    }

    @Test
    void getSafeMintDigest() {
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        BigInteger amount = BigInteger.valueOf(1);
        String ddcURI = "http://ddcUrl";
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);
        byte[] data = Numeric.hexStringToByteArray("0x16");

        String digest = metaTransaction.getSafeMintDigest(to, amount, ddcURI, data, nonce, deadline);
        log.info("SafeMint digest: {}", digest);
        assertEquals("0xe14c6eb304b587970eddc8db5da18c220be772d130c24b444c6442d92e61ebaf", digest);
    }

    @Test
    void getSafeMintBatchDigest() {
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        List<BigInteger> amounts = Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(1));
        List<String> ddcURIs = Arrays.asList("http://ddcUrl", "http://ddcUrl");
        BigInteger nonce = BigInteger.valueOf(8);
        BigInteger deadline = BigInteger.valueOf(1671096761);
        byte[] data = Numeric.hexStringToByteArray("0x16");

        String digest = metaTransaction.getSafeMintBatchDigest(to, amounts, ddcURIs, data, nonce, deadline);
        log.info("SafeMintBatch digest: {}", digest);
        assertEquals("0x71b3764b8d3f7664dd9259589c0aee888139c52bba854acdcc36946b509e1398", digest);
    }

    @Test
    void getSafeTransferFromDigest() {
        String from = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        BigInteger amount = BigInteger.valueOf(1);
        BigInteger ddcId = BigInteger.valueOf(8525);
        byte[] data = Numeric.hexStringToByteArray("0x16");
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);

        String digest = metaTransaction.getSafeTransferFromDigest(from, to, ddcId, amount, data, nonce, deadline);
        log.info("SafeTransferFrom digest: {}", digest);
        assertEquals("0x68f339b2d8ec643e26cf5f853df477dc072e9f808a918bb997b0c47c8eabb320", digest);
    }

    @Test
    void getSafeBatchTransferFromDigest() {
        String from = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        List<BigInteger> amounts = Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(1));
        List<BigInteger> ddcIds = Arrays.asList(BigInteger.valueOf(8525),BigInteger.valueOf(8526));
        byte[] data = Numeric.hexStringToByteArray("0x16");
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);

        String digest = metaTransaction.getSafeBatchTransferFromDigest(from, to, ddcIds, amounts, data, nonce, deadline);
        log.info("SafeTransferFromBatch digest: {}", digest);
        assertEquals("0x3d2dfa4f68c0518812a40f1a8d445ebe14ec541408131596fefececdf3c582b6", digest);
    }


    @Test
    void getBurnDigest() {
        String owner = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        BigInteger ddcId = BigInteger.valueOf(8525);
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);

        String digest = metaTransaction.getBurnDigest(owner, ddcId, nonce, deadline);
        log.info("Burn digest: {}", digest);
        assertEquals("0x952d42d7e44be12479ae099aafbde8ba4e34d198c35e57d1572fefba1ab0f3db", digest);
    }

    @Test
    void getBurnBatchDigest() {
        String owner = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        List<BigInteger> ddcIds = Arrays.asList(BigInteger.valueOf(838),BigInteger.valueOf(839));
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);

        String digest = metaTransaction.getBurnBatchDigest(owner, ddcIds, nonce, deadline);
        log.info("BurnBatch digest: {}", digest);
        assertEquals("0x108dc4e71b3621fd4f6a12792d27d8e31094692777a1ba84efbc6692c0402239", digest);
    }
}