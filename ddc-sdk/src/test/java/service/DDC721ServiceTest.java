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
import com.reddate.wuhanddc.net.RequestOptions;
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
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class DDC721ServiceTest {

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
    //  The address the transaction is send from.
    public static String sender = "0x238f4d9bfd16f422c16a692591d8f4b36a01bb35";

    public static BigInteger metaNonce = BigInteger.valueOf(1);
    public static BigInteger metaDeadline = BigInteger.valueOf(1671096761);

    static {
        DDCWuhan.setGatewayUrl("https://opbningxia.bsngate.com:18602/api/[projectId]/rpc");
        DDCWuhan.setNonceManagerAddress(sender);

//        metaNonce = getNonce(metaAccount);
//        metaDeadline = BigInteger.valueOf(LocalDateTime.now().plusYears(1).toEpochSecond(ZoneOffset.ofHours(8)));
    }

    private static String transactionSignature(String sender, RawTransaction transaction) {
        // sender: Obtain the private key according to the sender and complete its signature

        //sender privateKey
        String privateKey = "...";
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        return Numeric.toHexString(signedMessage);
    }

    @Test
    void mint() throws Exception {
        String tx = ddcSdkClient.ddc721Service.mint(sender, sender, "http://token/n");
        log.info(tx);
        assertNotNull(tx);
/*        Thread.sleep(10000);
        BigInteger ddcId = getDdcIdFromEventLog(tx, true);
        log.info(ddcId.toString(10));
        assertNotNull(ddcId);*/

    }


/*    @Test
    void getDdcId() {
        String txHash = "0xe85a43d7fda2da240df9f3e01d9550ececc42d17c00008e9527226ef6f4ca12c";
        boolean ddc721 = true;

        BigInteger ddcId;
        try {
            ddcId = getDdcIdFromEventLog(txHash, ddc721);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("ddcId: {}", ddcId);
        assertNotNull(ddcId);
    }*/

    @Test
    void safeMint() throws Exception {

        byte[] data = new byte[1];
        data[0] = 1;
        String tx = ddcSdkClient.ddc721Service.safeMint(sender, "0xb8988d0f53cca1c0e14c7bf591db7f9f0f2eb7ca", "Token-R88821", data);
        log.info(tx);
        assertNotNull(tx);

    }

    @Test
    void safeMintBatch() throws Exception {
        try {
            byte[] data = new byte[1];
            data[0] = 1;
            List<String> ddcURIs = new ArrayList<>();
            ddcURIs.add("1");
            ddcURIs.add("2");

            String tx = ddcSdkClient.ddc721Service.safeMintBatch(sender, "0xd8373630b07314dce1b0aec4ac72dd42a0fc1f83", ddcURIs, data);
            log.info(tx);
            assertNotNull(tx);
        } catch (DDCException e) {
            System.out.println(e.getMsg());
        }
    }

    @Test
    void approve() throws Exception {
        String tx = ddcSdkClient.ddc721Service.approve(sender, "0xbf38a3803d352cd0567ee7d1da61844308ff1693", new BigInteger("426443"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void getApproved() throws Exception {
        String tx = ddcSdkClient.ddc721Service.getApproved(new BigInteger("4"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void setApprovalForAll() throws Exception {
        String tx = ddcSdkClient.ddc721Service.setApprovalForAll(sender, "0x9d37d92d3bca605a49f21642c309e578b16040fd", true);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void isApprovedForAll() throws Exception {
        Boolean tx = ddcSdkClient.ddc721Service.isApprovedForAll("0x019ba4600e117f06e3726c0b100a2f10ec52339e", "0x9d37d92d3bca605a49f21642c309e578b16040fd");
        log.info(String.valueOf(tx));
        assertNotNull(tx);
    }

    @Test
    void safeTransferFrom() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;
        String tx = ddcSdkClient.ddc721Service.safeTransferFrom(sender, "0x4199137a1ffdb01829f9447d5620ac9fc160ca3b", "0x4199137a1ffdb01829f9447d5620ac9fc160ca3b", new BigInteger("8503"), data);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void transferFrom() throws Exception {
        String tx = ddcSdkClient.ddc721Service.transferFrom(sender, "0x019ba4600e117f06e3726c0b100a2f10ec52339e", "0x9d37d92d3bca605a49f21642c309e578b16040fd", new BigInteger("5"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void freeze() throws Exception {
        String tx = ddcSdkClient.ddc721Service.freeze(sender, new BigInteger("6"));
        log.info(tx);
        assertNotNull(tx);
    }


    @Test
    void unFreeze() throws Exception {
        String tx = ddcSdkClient.ddc721Service.unFreeze(sender, new BigInteger("6"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void burn() throws Exception {
        String tx = ddcSdkClient.ddc721Service.burn(sender, new BigInteger("6"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void balanceOf() throws Exception {
        BigInteger bigInteger = ddcSdkClient.ddc721Service.balanceOf("0x8ef7027cf422a42432ffea5936d0929449408b43");
        log.info(bigInteger.toString());
        assertNotNull(bigInteger);
    }

    @Test
    void ownerOf() throws Exception {
        String account = ddcSdkClient.ddc721Service.ownerOf(new BigInteger("8510"));
        log.info(account);
        assertNotNull(account);
    }

    @Test
    void name() throws Exception {
        String name = ddcSdkClient.ddc721Service.name();
        log.info(name);
        assertNotNull(name);
    }

    @Test
    void symbol() throws Exception {
        String symbol = ddcSdkClient.ddc721Service.symbol();
        log.info(symbol);
        assertNotNull(symbol);
    }

    @Test
    void ddcURI() throws Exception {
        String ddcURI = ddcSdkClient.ddc721Service.ddcURI(new BigInteger("7262"));
        log.info(ddcURI);
    }

    @Test
    void setDDCURI() throws Exception {
        String tx = ddcSdkClient.ddc721Service.setURI(sender ,new BigInteger("71"),"http://reddatetech.com/token/n");
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void setNameAndSymbol() throws Exception {
        String tx = ddcSdkClient.ddc721Service.setNameAndSymbol(sender ,"DDC721","DDC721");
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void getLatestDDCId() throws Exception {
        BigInteger latestDDCId = ddcSdkClient.ddc721Service.getLatestDDCId();
        log.info(latestDDCId.toString(10));
    }

    @Test
    void getNonce() throws Exception {
        String from = "0x6da7e501dc26d8aa0d5a8bdec6deecd0c5f18343";
        BigInteger metaTransactionNonce = ddcSdkClient.ddc721Service.getNonce(from);
        log.info(metaTransactionNonce.toString());
    }

    @Test
    void metaMint() throws Exception {
        String sign = "0x565eb10269596ef7909404fdd8be58b3d660504c8e63ec5a2ba9f714b5faa11e5b8fc496cb6383324861f2c7d5b072ab177cf224a02b04f6f430be7ef40bd69e1b";
        byte[] metaSign = Numeric.hexStringToByteArray(sign);
        String tx = ddcSdkClient.ddc721Service.metaMint(
                sender,
                "0x4199137a1ffdb01829f9447d5620ac9fc160ca3b",
                "http://ddcUrl",
                metaNonce,
                metaDeadline,
                metaSign);
        log.info(tx);
        assertNotNull(tx);
        BigInteger ddcId = getDdcIdFromEventLog(tx, true);
        log.info(ddcId.toString(10));
        assertNotNull(ddcId);
    }

    @Test
    void metaSafeMint() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;
        String sign = "0x565eb10269596ef7909404fdd8be58b3d660504c8e63ec5a2ba9f714b5faa11e5b8fc496cb6383324861f2c7d5b072ab177cf224a02b04f6f430be7ef40bd69e1b";
        byte[] metaSign = Numeric.hexStringToByteArray(sign);
        String tx = ddcSdkClient.ddc721Service.metaSafeMint(
                sender,
                "0x4199137a1ffdb01829f9447d5620ac9fc160ca3b",
                "http://ddcUrl",
                data,
                metaNonce,
                metaDeadline,
                metaSign);
        log.info(tx);
        assertNotNull(tx);
        BigInteger ddcId = getDdcIdFromEventLog(tx, true);
        log.info(ddcId.toString(10));
        assertNotNull(ddcId);

    }

    @Test
    void metaSafeTransferFrom() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;
        String sign = "0x565eb10269596ef7909404fdd8be58b3d660504c8e63ec5a2ba9f714b5faa11e5b8fc496cb6383324861f2c7d5b072ab177cf224a02b04f6f430be7ef40bd69e1b";
        byte[] metaSign = Numeric.hexStringToByteArray(sign);
        String tx = ddcSdkClient.ddc721Service.metaSafeTransferFrom(
                sender,
                "0xb8988d0f53cca1c0e14c7bf591db7f9f0f2eb7ca",
                "0xd9d11fc3c762526595e14970d895c13d982003d1",
                new BigInteger("7244"),
                data,
                metaNonce,
                metaDeadline,
                metaSign);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void metaTransferFrom() throws Exception {
        String sign = "0x565eb10269596ef7909404fdd8be58b3d660504c8e63ec5a2ba9f714b5faa11e5b8fc496cb6383324861f2c7d5b072ab177cf224a02b04f6f430be7ef40bd69e1b";
        byte[] metaSign = Numeric.hexStringToByteArray(sign);
        String tx = ddcSdkClient.ddc721Service.metaTransferFrom(
                sender,
                "0x019ba4600e117f06e3726c0b100a2f10ec52339e",
                "0x9d37d92d3bca605a49f21642c309e578b16040fd",
                new BigInteger("5"),
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
        String tx = ddcSdkClient.ddc721Service.metaBurn(
                sender,
                new BigInteger("6"),
                metaNonce,
                metaDeadline,
                metaSign);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void syncDDCOwners() throws Exception {
        List<BigInteger> ddcIds = new ArrayList<>();
        ddcIds.add(BigInteger.valueOf(1));
        ddcIds.add(BigInteger.valueOf(2));

        List<String> ownerList1 = new ArrayList<>();
        ownerList1.add("0x");
        List<String> ownerList2 = new ArrayList<>();
        ownerList2.add("0x");

        List<List<String>> owners = new ArrayList<>();
        owners.add(ownerList1);
        owners.add(ownerList2);

        String tx = ddcSdkClient.ddc1155Service.syncDDCOwners(sender, ddcIds, owners);
        log.info(tx);
        assertNotNull(tx);
    }

    private BigInteger getDdcIdFromEventLog(String txHash, boolean ddc721) throws Exception {
        BigInteger ddcId = null;
        String abi = null;
        String bin = null;
        if (ddc721) {
            abi = DDC721Service.DDC721Contract.getContractAbi();
            bin = DDC721Service.DDC721Contract.getContractBytecode();
        } else {
            abi = DDC1155Service.DDC1155Contract.getContractAbi();
            bin = DDC1155Service.DDC1155Contract.getContractBytecode();
        }

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
            }
            for (List<EventResultEntity> eventResultEntityList : eventResultEntityLists) {
                for (EventResultEntity resultEntity : eventResultEntityList) {
                    if ("ddcId".equals(resultEntity.getName())) {
                        ddcId = (BigInteger) resultEntity.getData();
                    }
                }
            }
            if (ddcId != null) {
                break;
            }
        }
        return ddcId;
    }
}