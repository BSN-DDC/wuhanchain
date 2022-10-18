package service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.reddate.wuhanddc.DDCSdkClient;
import com.reddate.wuhanddc.constant.DDC1155Functions;
import com.reddate.wuhanddc.constant.DDC721Functions;
import com.reddate.wuhanddc.constant.ErrorMessage;
import com.reddate.wuhanddc.exception.DDCException;
import com.reddate.wuhanddc.listener.SignEventListener;
import com.reddate.wuhanddc.net.DDCWuhan;
import com.reddate.wuhanddc.service.BaseService;
import com.reddate.wuhanddc.service.ChargeService;
import com.reddate.wuhanddc.service.DDC1155Service;
import com.reddate.wuhanddc.service.DDC721Service;
import com.reddate.wuhanddc.util.AnalyzeChainInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.tx.txdecode.EventResultEntity;
import org.fisco.bcos.web3j.utils.Strings;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@Slf4j
class DDC1155ServiceTest {

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

    // 平台方
    public static String sender = "0x238f4d9bfd16f422c16a692591d8f4b36a01bb35";

    public static String metaAccount = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
    public static BigInteger metaNonce = BigInteger.valueOf(1);
    public static BigInteger metaDeadline = BigInteger.valueOf(1671096761);

    static {
        DDCWuhan.setGatewayUrl("https://opbtest.bsngate.com:18602/api/4bbed86d890f42b6b70de34c9be425dd/rpc");
        DDCWuhan.setNonceManagerAddress(sender);
    }

    private static String transactionSignature(String sender, RawTransaction transaction) {
        // sender: Obtain the private key according to the sender and complete its signature

        String privateKey = "0xabc52d248c8056582d4626a71a4055fd4c5c818994e890b9ee906f303018fadd";
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        return Numeric.toHexString(signedMessage);
    }

/*    @Test
    void getDDCId() throws Exception {
        List<BigInteger> ddcIds = (List<BigInteger>)getDdcIdFromEventLog("0x98f4ad8d526e0819f2ca02be632225c0359496205be186eac5550142f476d2b5", false);
        log.info(ddcIds.toString());
        assertNotNull(ddcIds);
    }*/

    @Test
    void safeMint() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;
        String tx = ddcSdkClient.ddc1155Service.safeMint(sender, "0x81072375a506581CADBd90734Bd00A20CdDbE48b", new BigInteger("9223372036854775807888888"), "Token-R88821", "".getBytes(StandardCharsets.UTF_8));
        log.info(tx);
        assertNotNull(tx);
/*        List<BigInteger> ddcIds = (List<BigInteger>)getDdcIdFromEventLog(tx, false);
        log.info(ddcIds.toArray().toString());
        assertNotNull(ddcIds);*/
    }

    @Test
    void safeMintBatch() throws Exception {
        try {
            byte[] data = new byte[1];
            data[0] = 1;
            Multimap<BigInteger, String> map = ArrayListMultimap.create();
            map.put(new BigInteger("11"), "11");
            map.put(new BigInteger("22"), "12");

            String tx = ddcSdkClient.ddc1155Service.safeMintBatch(sender, "0xd8373630b07314dce1b0aec4ac72dd42a0fc1f83", map, data);
            log.info(tx);
            assertNotNull(tx);
        } catch (DDCException e) {
            System.out.println(e.getMsg());
        }
    }


    @Test
    void setApprovalForAll() throws Exception {
        String tx = ddcSdkClient.ddc1155Service.setApprovalForAll(sender, "0x4655399c9c082304fe7a0af145c490f52d87d732", true);
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
        String tx = ddcSdkClient.ddc1155Service.safeTransferFrom(sender, sender, sender, new BigInteger("158"), new BigInteger("1"), data);
        log.info(tx);
        assertNotNull(tx);
    }


    @Test
    void safeBatchTransferFrom() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;

        Map<BigInteger, BigInteger> ddcInfos = new HashMap<>();
        ddcInfos.put(new BigInteger("7"), new BigInteger("1"));
        String tx = ddcSdkClient.ddc1155Service.safeBatchTransferFrom(sender, "0x019ba4600e117f06e3726c0b100a2f10ec52339e", "0x4655399c9c082304fe7a0af145c490f52d87d732", ddcInfos, data);
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
        String tx = ddcSdkClient.ddc1155Service.burn(sender, "0x24a95d34dcbc74f714031a70b077e0abb3306066", new BigInteger("203"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void burnBatch() throws Exception {
        ArrayList<BigInteger> arrayList = new ArrayList();
        arrayList.add(new BigInteger("6"));
        arrayList.add(new BigInteger("7"));

        String tx = ddcSdkClient.ddc1155Service.burnBatch(sender, "0x019ba4600e117f06e3726c0b100a2f10ec52339e", arrayList);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void balanceOf() throws Exception {
        BigInteger bigInteger = ddcSdkClient.ddc1155Service.balanceOf("0x151BC000a850C4BB4Af14e7bA6846329A6666666", new BigInteger("16"));
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

    @Test
    void setURI() throws Exception {
        String tx = ddcSdkClient.ddc1155Service.setURI(sender ,new BigInteger("158"),"abc");
        log.info(tx);
        assertNotNull(tx);

    }

    @Test
    void getLatestDDCId() throws Exception {
        BigInteger latestDDCId = ddcSdkClient.ddc1155Service.getLatestDDCId();
        log.info(latestDDCId.toString(10));    }

    @Test
    void getNonce() throws Exception {
        String from = "0x6da7e501dc26d8aa0d5a8bdec6deecd0c5f18343";
        BigInteger metaTransactionNonce = ddcSdkClient.ddc1155Service.getNonce(from);
        log.info(metaTransactionNonce.toString());
    }

    @Test
    void metaSafeMint() throws Exception {
        String sign = "0x565eb10269596ef7909404fdd8be58b3d660504c8e63ec5a2ba9f714b5faa11e5b8fc496cb6383324861f2c7d5b072ab177cf224a02b04f6f430be7ef40bd69e1b";
        byte[] metaSign = Numeric.hexStringToByteArray(sign);
        byte[] data = new byte[1];
        data[0] = 1;
        String tx = ddcSdkClient.ddc1155Service.metaSafeMint(
                sender,
                "0x6922d8af46d5e39c2a15caa26ee692fcc118adc5",
                new BigInteger("9223372036854775807888888"),
                "Token-R88821", "".getBytes(StandardCharsets.UTF_8),
                metaNonce,
                metaDeadline,
                metaSign);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void metaSafeMintBatch() throws Exception {
        String sign = "0x565eb10269596ef7909404fdd8be58b3d660504c8e63ec5a2ba9f714b5faa11e5b8fc496cb6383324861f2c7d5b072ab177cf224a02b04f6f430be7ef40bd69e1b";
        byte[] metaSign = Numeric.hexStringToByteArray(sign);
        try {
            byte[] data = new byte[1];
            data[0] = 1;
            Multimap<BigInteger, String> map = ArrayListMultimap.create();
            map.put(new BigInteger("11"), "11");
            map.put(new BigInteger("22"), "12");

            String tx = ddcSdkClient.ddc1155Service.metaSafeMintBatch(
                    sender,
                    "0xb8988d0f53cca1c0e14c7bf591db7f9f0f2eb7ca",
                    map,
                    data,
                    metaNonce,
                    metaDeadline,
                    metaSign);
            log.info(tx);
            assertNotNull(tx);
        } catch (DDCException e) {
            System.out.println(e.getMsg());
        }
    }

    @Test
    void metaSafeTransferFrom() throws Exception {
        String sign = "0x565eb10269596ef7909404fdd8be58b3d660504c8e63ec5a2ba9f714b5faa11e5b8fc496cb6383324861f2c7d5b072ab177cf224a02b04f6f430be7ef40bd69e1b";
        byte[] metaSign = Numeric.hexStringToByteArray(sign);
        byte[] data = new byte[1];
        data[0] = 1;
        String tx = ddcSdkClient.ddc1155Service.metaSafeTransferFrom(
                sender,
                "0x019ba4600e117f06e3726c0b100a2f10ec52339e",
                "0x4655399c9c082304fe7a0af145c490f52d87d732",
                new BigInteger("8"),
                new BigInteger("1"),
                data,
                metaNonce,
                metaDeadline,
                metaSign);
        log.info(tx);
        assertNotNull(tx);
    }


    @Test
    void metaSafeBatchTransferFrom() throws Exception {
        String sign = "0x565eb10269596ef7909404fdd8be58b3d660504c8e63ec5a2ba9f714b5faa11e5b8fc496cb6383324861f2c7d5b072ab177cf224a02b04f6f430be7ef40bd69e1b";
        byte[] metaSign = Numeric.hexStringToByteArray(sign);
        byte[] data = new byte[1];
        data[0] = 1;

        Map<BigInteger, BigInteger> ddcInfos = new HashMap<>();
        ddcInfos.put(new BigInteger("7"), new BigInteger("1"));
        String tx = ddcSdkClient.ddc1155Service.metaSafeBatchTransferFrom(
                sender,
                "0x019ba4600e117f06e3726c0b100a2f10ec52339e",
                "0x4655399c9c082304fe7a0af145c490f52d87d732",
                ddcInfos,
                data,
                metaNonce,
                metaDeadline,
                metaSign);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void metaBurn() throws Exception {
        String sign = "0x565eb10269596ef7909404fdd8be58b3d660504c8e63ec5a2ba9f714b5faa11e5b8fc496cb6383324861f2c7d5b072ab177cf224a02b04f6f430be7ef40bd69e1b";
        byte[] metaSign = Numeric.hexStringToByteArray(sign);
        String tx = ddcSdkClient.ddc1155Service.metaBurn(
                sender,
                "0x24a95d34dcbc74f714031a70b077e0abb3306066",
                new BigInteger("203"),
                metaNonce,
                metaDeadline,
                metaSign);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void metaBurnBatch() throws Exception {
        String sign = "0x565eb10269596ef7909404fdd8be58b3d660504c8e63ec5a2ba9f714b5faa11e5b8fc496cb6383324861f2c7d5b072ab177cf224a02b04f6f430be7ef40bd69e1b";
        byte[] metaSign = Numeric.hexStringToByteArray(sign);
        ArrayList<BigInteger> arrayList = new ArrayList();
        arrayList.add(new BigInteger("6"));
        arrayList.add(new BigInteger("7"));

        String tx = ddcSdkClient.ddc1155Service.metaBurnBatch(
                sender,
                "0x019ba4600e117f06e3726c0b100a2f10ec52339e",
                arrayList,
                metaNonce,
                metaDeadline,
                metaSign);
        log.info(tx);
        assertNotNull(tx);
    }

    private Object getDdcIdFromEventLog(String txHash, boolean ddc721) throws Exception {
        Object ddcId = null;
        String abi = null;
        String bin = null;
        if (ddc721) {
            abi = DDC721Service.DDC721Contract.getContractAbi();
            bin = DDC721Service.DDC721Contract.getContractBytecode();
        } else {
            abi = DDC1155Service.DDC1155Contract.getContractAbi();
            bin = DDC1155Service.DDC1155Contract.getContractBytecode();
        }

        Thread.sleep(5000);
        BaseService ddcService = new BaseService();
        TransactionReceipt transactionRecepitBean = ddcService.getTransactionReceipt(txHash);
        if (transactionRecepitBean == null) {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(2000);
                transactionRecepitBean = ddcService.getTransactionReceipt(txHash);
                if (transactionRecepitBean != null) {
                    break;
                }
            }
        }

        if (transactionRecepitBean.getStatus().equals("0x0")) {
            log.error("analyzeTransactionRecepitOutput,txHash is {}", txHash);
            throw new DDCException(ErrorMessage.CUSTOM_ERROR, "transaction failed!");
        }

        for (int i = 0; i < transactionRecepitBean.getLogs().size(); i++) {
            // 根据交易日志匹配需要解析的信息
            Log log = transactionRecepitBean.getLogs().get(i);
            if (Strings.isEmpty(log.getAddress()) && !ChargeService.chargeContract.getContractAddress().equals(log.getAddress().toLowerCase())) {
                continue;
            }

            Map<String, List<List<EventResultEntity>>> map = AnalyzeChainInfoUtils.analyzeEventLog(
                    abi,
                    bin,
                    JSON.toJSONString(Arrays.asList(log))
            );

            if (map.isEmpty()) {
                continue;
            }

            List<List<EventResultEntity>> eventResultEntityLists = null;
            if (ddc721) {
                eventResultEntityLists = map.get(DDC721Functions.TRANSFER_EVENT);
            } else {
                eventResultEntityLists = map.get(DDC1155Functions.TRANSFER_SINGLE_EVENT);
                if (eventResultEntityLists == null) {
                    eventResultEntityLists = map.get(DDC1155Functions.TRANSFER_BATCH_EVENT);
                }
            }
            for (List<EventResultEntity> eventResultEntityList : eventResultEntityLists) {
                for (EventResultEntity resultEntity : eventResultEntityList) {
                    if ("ddcId".equals(resultEntity.getName()) || "ddcIds".equals(resultEntity.getName())) {
                        ddcId = resultEntity.getData();
                    }
                }
            }
        }
        return ddcId;
    }

}