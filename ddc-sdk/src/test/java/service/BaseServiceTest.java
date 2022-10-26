package service;


import com.alibaba.fastjson.JSONObject;
import com.reddate.wuhanddc.DDCSdkClient;
import com.reddate.wuhanddc.dto.wuhanchain.RespJsonRpcBean;
import com.reddate.wuhanddc.dto.wuhanchain.TransactionsBean;
import com.reddate.wuhanddc.listener.SignEventListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

@Slf4j
class BaseServiceTest {

    // sign event listener
    SignEventListener signEventListener = event -> null;

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


    @Test
    void getBlockNumber() throws Exception {
        BigInteger blockNumber = ddcSdkClient.baseService.getBlockNumber();
        log.info(String.valueOf(blockNumber));
    }

    @Test
    void getBlockByNumber() throws Exception {
        RespJsonRpcBean receipt = ddcSdkClient.baseService.getBlockByNumber(new BigInteger("1794559"));
        log.info(JSONObject.toJSONString(receipt));
    }

    @Test
    void getBlockByNumberByOptions() throws Exception {
        RespJsonRpcBean receipt = ddcSdkClient.baseService.getBlockByNumber(new BigInteger("1794559"));
        log.info(JSONObject.toJSONString(receipt));
    }

    @Test
    void getTransactionReceipt() throws Exception {
        TransactionReceipt receipt = ddcSdkClient.baseService.getTransactionReceipt("0x31b09b1e618bcb8a513f4970300179f38583a881af4d03953bff79f1ca0e53d0");
        log.info(JSONObject.toJSONString(receipt));
    }

    @Test
    void getTransactionByHash() throws Exception {
        TransactionsBean ethTransaction = ddcSdkClient.baseService.getTransactionByHash("0x31b09b1e618bcb8a513f4970300179f38583a881af4d03953bff79f1ca0e53d0");
        log.info(JSONObject.toJSONString(ethTransaction));
    }

    @Test
    void getTransactionCount() throws Exception {
        BigInteger nonce = ddcSdkClient.baseService.getTransactionCount("0x019ba4600e117f06e3726c0b100a2f10ec52339e");
        log.info(String.valueOf(nonce));
    }

    @Test
    void getGasPrice() throws Exception {
        BigInteger gasPrice = ddcSdkClient.baseService.getGasPrice();
        log.info(String.valueOf(gasPrice));
    }

    @Test
    void getTransByStatus() throws Exception {
        boolean status = ddcSdkClient.baseService.getTransByStatus("0xb602c3187c41bf9041141c95ce373b5455662768eaf16338713f113d742b7147");
        log.info(String.valueOf(status));
    }
}