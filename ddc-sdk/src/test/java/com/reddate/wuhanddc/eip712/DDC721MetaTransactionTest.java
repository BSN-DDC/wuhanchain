package com.reddate.wuhanddc.eip712;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class DDC721MetaTransactionTest {

    String privateKey = "...";
    DDC721MetaTransaction metaTransaction = DDC721MetaTransaction.builder()
            .setChainId(BigInteger.valueOf(5555))
            .setContractAddress("0xad3B52B4F4bd9198DC69dD9cE4aC9846667461a2")
            .build();


//    @Test
//    void getDomainSeparator() {
//        log.info("DDC721 Domain: {}", metaTransaction.getDomainSeparator());
//    }

    @Test
    void generateSignature() {
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        String ddcURI = "http://ddcUrl";
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);

        String digest = metaTransaction.getMintDigest(to, ddcURI, nonce, deadline);
        byte[] signature = metaTransaction.generateSignature(privateKey, digest);
        log.info("MetaMint sign: {}", Numeric.toHexString(signature));
        assertEquals("0x32db49cfcc2e0fc79f688c22a8cbde6eeb5def59bcc9c06182ec4db76033c9d13e2bb89cd7f694cf7773708769328b9bf5402eba8695c32e6fa9de57238016a31c", Numeric.toHexString(signature));
    }

    @Test
    void getMintDigest() {
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        String ddcURI = "http://ddcUrl";
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);

        String digest = metaTransaction.getMintDigest(to, ddcURI, nonce, deadline);
        log.info("MetaMint digest: {}", digest);
        assertEquals("0xad7ca1256032300f3142e583fd91142c6395307de1c1dd4c93aac2a3b34c3dd3", digest);
    }

    @Test
    void getSafeMintDigest() {
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        String ddcURI = "http://ddcUrl";
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);
        byte[] data = Numeric.hexStringToByteArray("0x16");

        String digest = metaTransaction.getSafeMintDigest(to, ddcURI, data, nonce, deadline);
        log.info("MetaSafeMint digest: {}", digest);
        assertEquals("0x01fbee9805afd35abf23560939e9bd5b9d8e55f7237dea584d2e48e4c5b4e5d1", digest);
    }

    @Test
    void getMintBatchDigest() {
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        List<String> ddcURIs = Arrays.asList("http://ddcUrl", "http://ddcUrl");
        BigInteger nonce = BigInteger.valueOf(7);
        BigInteger deadline = BigInteger.valueOf(1671096761);

        String digest = metaTransaction.getMintBatchDigest(to, ddcURIs, nonce, deadline);
        log.info("MetaMintBatch digest: {}", digest);
        assertEquals("0x883f5ba3b7fc4ba06031bf39f1fb4fab96fe5eea9535fe793c8cccc15ff3e1e4", digest);
    }

    @Test
    void getSafeMintBatchDigest() {
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        List<String> ddcURIs = Arrays.asList("http://ddcUrl", "http://ddcUrl");
        BigInteger nonce = BigInteger.valueOf(8);
        BigInteger deadline = BigInteger.valueOf(1671096761);

        String digest = metaTransaction.getSafeMintBatchDigest(to, ddcURIs, nonce, deadline);
        log.info("MetaSafeMintBatch digest: {}", digest);
        assertEquals("0x4bea54779d62965cc80e3d8195c366f731b42eaae2553ef8fd28eba133a07e7b", digest);
    }

    @Test
    void getTransferFromDigest() {
        String from = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        BigInteger ddcId = BigInteger.valueOf(8525);
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);

        String digest = metaTransaction.getTransferFromDigest(from, to, ddcId, nonce, deadline);
        log.info("TransferFrom digest: {}", digest);
        assertEquals("0xe07b5aa0cb2ec8791526913f9d8a8250cf1102f2b48182c8ef03088821cab3fa", digest);
    }

    @Test
    void getSafeTransferFromDigest() {
        String from = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        BigInteger ddcId = BigInteger.valueOf(8525);
        byte[] data = Numeric.hexStringToByteArray("0x16");
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);

        String digest = metaTransaction.getSafeTransferFromDigest(from, to, ddcId, data, nonce, deadline);
        log.info("SafeTransferFrom digest: {}", digest);
        assertEquals("0xfd962e767910247ffc3a0b7fd2fbf100571cb89ced9c61ceb14f8304985bc3d9", digest);
    }

    @Test
    void getBurnDigest() {
        BigInteger ddcId = BigInteger.valueOf(8525);
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);

        String digest = metaTransaction.getBurnDigest(ddcId, nonce, deadline);
        log.info("Burn digest: {}", digest);
        assertEquals("0x4b560fc6744d5ff63a0576e8a49047aaabba4ca454602ae85cb7f9e0e17afb6f", digest);
    }
}