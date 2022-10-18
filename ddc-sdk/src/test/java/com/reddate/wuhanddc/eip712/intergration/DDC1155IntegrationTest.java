package com.reddate.wuhanddc.eip712.intergration;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.reddate.wuhanddc.DDCSdkClient;
import com.reddate.wuhanddc.constant.ErrorMessage;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class DDC1155IntegrationTest {

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
    public static String originPrivateKey = "0xc14a69010906a26f5d55a902f5ea6b8bdcfb4e22eb83816e76669e42d892cca7";
    public static String sender = "0xf92a573e14744243080fb274f22b49d707d6c51e"; // 运营方账户

    private static String transactionSignature(String sender, RawTransaction transaction) {
        // sender: Obtain the private key according to the sender and complete its signature

        //sender privateKey
        String privateKey = "0xc14a69010906a26f5d55a902f5ea6b8bdcfb4e22eb83816e76669e42d892cca7";
        Credentials credentials = Credentials.create(privateKey);

        if (!credentials.getAddress().equalsIgnoreCase(sender)) {
            throw new DDCException(ErrorMessage.ILLEGAL_PARAMETER, "sender and privateKey do not match");
        }

        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        return Numeric.toHexString(signedMessage);
    }

    static {
        DDCWuhan.setGatewayUrl("https://opbtest.bsngate.com:18602/api/4bbed86d890f42b6b70de34c9be425dd/rpc");
        DDCWuhan.setNonceManagerAddress(sender);
    }

    @Test
    void metaSafeMint() throws Exception {
        String to = "0xf92a573e14744243080fb274f22b49d707d6c51e";
        String ddcURI = "http://ddcUrl";
        BigInteger amount = BigInteger.valueOf(1);
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);
        byte[] data = Numeric.hexStringToByteArray("0x16");

        BigInteger lastNonce = ddcSdkClient.ddc1155Service.getNonce(to);
        nonce = lastNonce.add(BigInteger.ONE);

        String digest = ddcSdkClient.ddc1155MetaTransaction.getSafeMintDigest(to, amount, ddcURI, data, nonce, deadline);
        byte[] signature = ddcSdkClient.ddc1155MetaTransaction.generateSignature(originPrivateKey, digest);
        log.info("MetaSafeMint sign: {}", Numeric.toHexString(signature));

        String txHash = ddcSdkClient.ddc1155Service.metaSafeMint(
                sender,
                to,
                amount,
                ddcURI,
                data,
                nonce,
                deadline,
                signature);
        log.info("txHash: {}", txHash);
        assertNotNull(txHash);
    }

    @Test
    void metaSafeMintBatch() throws Exception {
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        List<BigInteger> amounts = Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(1));
        List<String> ddcURIs = Arrays.asList("http://ddcUrl", "http://ddcUrl");
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);
        byte[] data = Numeric.hexStringToByteArray("0x16");

        BigInteger lastNonce = ddcSdkClient.ddc1155Service.getNonce(to);
        nonce = lastNonce.add(BigInteger.ONE);

        String digest = ddcSdkClient.ddc1155MetaTransaction.getSafeMintBatchDigest(to, amounts, ddcURIs, data, nonce, deadline);
        byte[] signature = ddcSdkClient.ddc1155MetaTransaction.generateSignature(originPrivateKey, digest);
        log.info("MetaMintBatch sign: {}", Numeric.toHexString(signature));

        Multimap<BigInteger, String> ddcInfo = ArrayListMultimap.create();
        ddcInfo.put(new BigInteger("1"), "http://ddcUrl");
        ddcInfo.put(new BigInteger("1"), "http://ddcUrl");

        String txHash = ddcSdkClient.ddc1155Service.metaSafeMintBatch(
                sender,
                to,
                ddcInfo,
                data,
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
        BigInteger ddcId = BigInteger.valueOf(916);
        BigInteger amount = BigInteger.valueOf(1);
        byte[] data = Numeric.hexStringToByteArray("0x16");
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);

        BigInteger lastNonce = ddcSdkClient.ddc1155Service.getNonce(from);
        nonce = lastNonce.add(BigInteger.ONE);

        String digest = ddcSdkClient.ddc1155MetaTransaction.getSafeTransferFromDigest(from, to, ddcId, amount, data, nonce, deadline);
        byte[] signature = ddcSdkClient.ddc1155MetaTransaction.generateSignature(originPrivateKey, digest);
        log.info("SafeTransferFrom sign: {}", Numeric.toHexString(signature));

        String txHash = ddcSdkClient.ddc1155Service.metaSafeTransferFrom(
                sender,
                from,
                to,
                ddcId,
                amount,
                data,
                nonce,
                deadline,
                signature);
        log.info("txHash: {}", txHash);
        assertNotNull(txHash);
    }

    @Test
    void safeBatchTransferFrom() throws Exception {
        String from = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        List<BigInteger> amounts = Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(1));
        List<BigInteger> ddcIds = Arrays.asList(BigInteger.valueOf(917),BigInteger.valueOf(916));
        byte[] data = Numeric.hexStringToByteArray("0x16");
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);

        BigInteger lastNonce = ddcSdkClient.ddc1155Service.getNonce(from);
        nonce = lastNonce.add(BigInteger.ONE);

        String digest = ddcSdkClient.ddc1155MetaTransaction.getSafeBatchTransferFromDigest(from, to, ddcIds, amounts, data, nonce, deadline);
        byte[] signature = ddcSdkClient.ddc1155MetaTransaction.generateSignature(originPrivateKey, digest);
        log.info("SafeTransferFrom sign: {}", Numeric.toHexString(signature));

        Map<BigInteger, BigInteger> ddcInfos = new LinkedHashMap<>();
        ddcInfos.put(new BigInteger("917"), new BigInteger("1"));
        ddcInfos.put(new BigInteger("916"), new BigInteger("1"));

        String txHash = ddcSdkClient.ddc1155Service.metaSafeBatchTransferFrom(
                sender,
                from,
                to,
                ddcInfos,
                data,
                nonce,
                deadline,
                signature);
        log.info("txHash: {}", txHash);
        assertNotNull(txHash);
    }


    @Test
    void metaBurn() throws Exception {
        String owner = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        BigInteger ddcId = BigInteger.valueOf(841);
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);

        BigInteger lastNonce = ddcSdkClient.ddc1155Service.getNonce(owner);
        nonce = lastNonce.add(BigInteger.ONE);

        String digest = ddcSdkClient.ddc1155MetaTransaction.getBurnDigest(owner, ddcId, nonce, deadline);
        byte[] signature = ddcSdkClient.ddc1155MetaTransaction.generateSignature(originPrivateKey, digest);
        log.info("Burn sign: {}", Numeric.toHexString(signature));

        String txHash = ddcSdkClient.ddc1155Service.metaBurn(
                sender,
                owner,
                ddcId,
                nonce,
                deadline,
                signature);
        log.info("txHash: {}", txHash);
        assertNotNull(txHash);
    }

    @Test
    void metaBurnBatch() throws Exception {
        String owner = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        List<BigInteger> ddcIds = Arrays.asList(BigInteger.valueOf(842), BigInteger.valueOf(843));
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = BigInteger.valueOf(1671096761);

        BigInteger lastNonce = ddcSdkClient.ddc1155Service.getNonce(owner);
        nonce = lastNonce.add(BigInteger.ONE);

        String digest = ddcSdkClient.ddc1155MetaTransaction.getBurnBatchDigest(owner, ddcIds, nonce, deadline);
        byte[] signature = ddcSdkClient.ddc1155MetaTransaction.generateSignature(originPrivateKey, digest);
        log.info("BurnBatch sign: {}", Numeric.toHexString(signature));

        String txHash = ddcSdkClient.ddc1155Service.metaBurnBatch(
                sender,
                owner,
                ddcIds,
                nonce,
                deadline,
                signature);
        log.info("txHash: {}", txHash);
        assertNotNull(txHash);
    }

}
