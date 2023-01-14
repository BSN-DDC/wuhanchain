package com.reddate.wuhanddc.eip712.intergration;

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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class DDC721IntegrationTest {

    // sign event listener
    SignEventListener signEventListener = event -> transactionSignature(event.getSender(), event.getRawTransaction());

    // ddcSdkClient instantiation
    DDCSdkClient ddcSdkClient = DDCSdkClient.builder()
            .setSignEventListener(signEventListener)
            .setAuthorityAddress("0xB746e96bC24bc9bC11515b6F39Cbe135d1b67a59")
            .setChargeAddress("0xf1b4db42b9a96CA2943C8e047552Fd6E05D55396")
            .setDdc721Address("0xb4B46D6B2C7BC4389759f9EBE141cFE086771561")
            .setDdc1155Address("0x5Bf9e07aBBF0cFbF21d02065529AE10e2Ef0a375")
            .setCrossChainAddress("0x983d935D626a97eD219D95b11d36082b9D1A4A2d")
            .setChainId(BigInteger.valueOf(5555))
            .build();

    // 0x81072375a506581CADBd90734Bd00A20CdDbE48b
    public static String originPrivateKey = "...";
    public static String sender = "0x4199137a1ffdb01829f9447d5620ac9fc160ca3b"; // 运营方账户

    private static String transactionSignature(String sender, RawTransaction transaction) {
        // sender: Obtain the private key according to the sender and complete its signature

        //sender privateKey
        String privateKey = "...";
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        return Numeric.toHexString(signedMessage);
    }

    static {
        DDCWuhan.setGatewayUrl("https://opbtest.bsngate.com:18602/api/4bbed86d890f42b6b70de34c9be425dd/rpc");
        DDCWuhan.setNonceManagerAddress(sender);
    }

    @Test
    void metaMint() throws Exception {
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        String ddcURI = "http://ddcUrl";
        BigInteger deadline = BigInteger.valueOf(1671096761);
        BigInteger nonce = ddcSdkClient.ddc721Service.getNonce(to).add(BigInteger.ONE);

        String digest = ddcSdkClient.ddc721MetaTransaction.getMintDigest(to, ddcURI, nonce, deadline);
        byte[] signature = ddcSdkClient.ddc721MetaTransaction.generateSignature(originPrivateKey, digest);
        log.info("MetaMint sign: {}", Numeric.toHexString(signature));

        String txHash = ddcSdkClient.ddc721Service.metaMint(
                sender,
                to,
                ddcURI,
                nonce,
                deadline,
                signature);
        log.info("txHash: {}", txHash);
        assertNotNull(txHash);
    }

    @Test
    void metaSafeMint() throws Exception {
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        String ddcURI = "http://ddcUrl";
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);
        byte[] data = Numeric.hexStringToByteArray("0x16");

        BigInteger lastNonce = ddcSdkClient.ddc721Service.getNonce(to);
        nonce = lastNonce.add(BigInteger.ONE);

        String digest = ddcSdkClient.ddc721MetaTransaction.getSafeMintDigest(to, ddcURI, data, nonce, deadline);
        byte[] signature = ddcSdkClient.ddc721MetaTransaction.generateSignature(originPrivateKey, digest);
        log.info("MetaSafeMint sign: {}", Numeric.toHexString(signature));

        String txHash = ddcSdkClient.ddc721Service.metaSafeMint(
                sender,
                to,
                ddcURI,
                data,
                nonce,
                deadline,
                signature);
        log.info("txHash: {}", txHash);
        assertNotNull(txHash);
    }

    @Test
    void metaMintBatch() throws Exception {
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        List<String> ddcURIs = Arrays.asList("http://ddcUrl", "http://ddcUrl");
        BigInteger deadline = BigInteger.valueOf(1671096761);
        BigInteger nonce = ddcSdkClient.ddc721Service.getNonce(to).add(BigInteger.ONE);

        String digest = ddcSdkClient.ddc721MetaTransaction.getMintBatchDigest(to, ddcURIs, nonce, deadline);
        byte[] signature = ddcSdkClient.ddc721MetaTransaction.generateSignature(originPrivateKey, digest);
        log.info("MetaMintBatch sign: {}", Numeric.toHexString(signature));

        String txHash = ddcSdkClient.ddc721Service.metaMintBatch(
                sender,
                to,
                ddcURIs,
                nonce,
                deadline,
                signature);
        log.info("txHash: {}", txHash);
        assertNotNull(txHash);
    }

    @Test
    void metaSafeMintBatch() throws Exception {
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        List<String> ddcURIs = Arrays.asList("http://ddcUrl", "http://ddcUrl");
        BigInteger deadline = BigInteger.valueOf(1671096761);
        byte[] data = Numeric.hexStringToByteArray("0x16");
        BigInteger nonce = ddcSdkClient.ddc721Service.getNonce(to).add(BigInteger.ONE);


        String digest = ddcSdkClient.ddc721MetaTransaction.getSafeMintBatchDigest(to, ddcURIs, nonce, deadline);
        byte[] signature = ddcSdkClient.ddc721MetaTransaction.generateSignature(originPrivateKey, digest);
        log.info("MetaSafeMintBatch sign: {}", Numeric.toHexString(signature));

        String txHash = ddcSdkClient.ddc721Service.metaSafeMintBatch(
                sender,
                to,
                ddcURIs,
                data,
                nonce,
                deadline,
                signature);
        log.info("txHash: {}", txHash);
        assertNotNull(txHash);
    }

    @Test
    void metaTransferFrom() throws Exception {
        String from = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        BigInteger ddcId = BigInteger.valueOf(8525);
        BigInteger deadline = BigInteger.valueOf(1671096761);
        BigInteger nonce = ddcSdkClient.ddc721Service.getNonce(to).add(BigInteger.ONE);

        String digest = ddcSdkClient.ddc721MetaTransaction.getTransferFromDigest(from, to, ddcId, nonce, deadline);
        byte[] signature = ddcSdkClient.ddc721MetaTransaction.generateSignature(originPrivateKey, digest);
        log.info("TransferFrom sign: {}", Numeric.toHexString(signature));

        String txHash = ddcSdkClient.ddc721Service.metaTransferFrom(
                sender,
                from,
                to,
                ddcId,
                nonce,
                deadline,
                signature);
        log.info("txHash: {}", txHash);
        assertNotNull(txHash);
    }

    @Test
    void metaSafeTransferFrom() throws Exception {
        String from = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        BigInteger ddcId = BigInteger.valueOf(8525);
        byte[] data = Numeric.hexStringToByteArray("0x16");
        BigInteger deadline = BigInteger.valueOf(1671096761);
        BigInteger nonce = ddcSdkClient.ddc721Service.getNonce(to).add(BigInteger.ONE);

        String digest = ddcSdkClient.ddc721MetaTransaction.getSafeTransferFromDigest(from, to, ddcId, data, nonce, deadline);
        byte[] signature = ddcSdkClient.ddc721MetaTransaction.generateSignature(originPrivateKey, digest);
        log.info("SafeTransferFrom sign: {}", Numeric.toHexString(signature));

        String txHash = ddcSdkClient.ddc721Service.metaSafeTransferFrom(
                sender,
                from,
                to,
                ddcId,
                data,
                nonce,
                deadline,
                signature);
        log.info("txHash: {}", txHash);
        assertNotNull(txHash);
    }

    @Test
    void metaBurn() throws Exception {
        String from = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        BigInteger ddcId = BigInteger.valueOf(8526);
        BigInteger deadline = BigInteger.valueOf(1671096761);
        BigInteger nonce = ddcSdkClient.ddc721Service.getNonce(from).add(BigInteger.ONE);

        String digest = ddcSdkClient.ddc721MetaTransaction.getBurnDigest(ddcId, nonce, deadline);
        byte[] signature = ddcSdkClient.ddc721MetaTransaction.generateSignature(originPrivateKey, digest);
        log.info("Burn sign: {}", Numeric.toHexString(signature));

        String txHash = ddcSdkClient.ddc721Service.metaBurn(
                sender,
                ddcId,
                nonce,
                deadline,
                signature);
        log.info("txHash: {}", txHash);
        assertNotNull(txHash);
    }

}
