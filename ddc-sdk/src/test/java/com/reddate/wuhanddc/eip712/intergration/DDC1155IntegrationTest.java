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
            .setAuthorityAddress("0x466D5b0eA174a2DD595D40e0B30e433FCe6517F5")
            .setChargeAddress("0xCa97bF3a19403805d391102908665b16B4d0217C")
            .setDdc721Address("0xad3B52B4F4bd9198DC69dD9cE4aC9846667461a2")
            .setDdc1155Address("0x061e59c74815994DAb4226a0D344711F18E0F418")
            .setCrossChainAddress("0xc4E12bB845D9991ee26718E881C712B2c0cB2048")
            .setChainId(BigInteger.valueOf(5555))
            .build();

    // 元交易签名私钥
    public static String originPrivateKey = "0xb1e104c4c3d74dc4a9131cde8f3619c1c2d68a442da19dc146159eae0311d8b1";
    // 签名账户地址
    public static String sender = "0x4199137a1ffdb01829f9447d5620ac9fc160ca3b";

    private static String transactionSignature(String sender, RawTransaction transaction) {
        // sender: Obtain the private key according to the sender and complete its signature

        // sender 对应的Hex格式私钥
        String privateKey = "0xb0740f6265b97f766bf2d109204bb320861e99a3fbdb67d0a9a84891ef280c97";
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        return Numeric.toHexString(signedMessage);
    }

    static {
        DDCWuhan.setGatewayUrl("https://opbningxia.bsngate.com:18602/api/[projectId]/rpc");
        DDCWuhan.setNonceManagerAddress(sender);
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
