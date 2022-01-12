package service;


import com.alibaba.fastjson.JSONObject;
import com.reddate.ddc.DDCSdkClient;
import com.reddate.ddc.dto.wuhanchain.RespJsonRpcBean;
import com.reddate.ddc.dto.wuhanchain.TransactionsBean;
import com.reddate.ddc.listener.SignEventListener;
import com.reddate.ddc.net.RequestOptions;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

@Slf4j
class BaseServiceTest {

    SignEventListener signEventListener = event -> "";

    DDCSdkClient  ddcSdkClient = new DDCSdkClient().instance("src/main/resources/contractConfig.json", signEventListener);

    RequestOptions options = RequestOptions.builder()
            .setGateWayUrl("https://opbningxia.bsngate.com:18602/api/02acc30c5dd44d2486a3aa481881eae7/rpc")
            .build();


    @Test
    void assembleTransaction() {

    }

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
        RespJsonRpcBean receipt = ddcSdkClient.baseService.getBlockByNumber(new BigInteger("1794559"), options);
        log.info(JSONObject.toJSONString(receipt));
    }

    @Test
    void getTransactionReceipt() throws Exception {
        TransactionReceipt receipt = ddcSdkClient.baseService.getTransactionReceipt("0x31b09b1e618bcb8a513f4970300179f38583a881af4d03953bff79f1ca0e53d0", options);
        log.info(JSONObject.toJSONString(receipt));
    }

    @Test
    void getTransactionByHash() throws Exception {
        TransactionsBean ethTransaction = ddcSdkClient.baseService.getTransactionByHash("0x31b09b1e618bcb8a513f4970300179f38583a881af4d03953bff79f1ca0e53d0", options);
        log.info(JSONObject.toJSONString(ethTransaction));
    }

    @Test
    void getTransactionCount() throws Exception {
        BigInteger nonce = ddcSdkClient.baseService.getTransactionCount("0x019ba4600e117f06e3726c0b100a2f10ec52339e", options);
        log.info(String.valueOf(nonce));
    }

    @Test
    void getGasPrice() throws Exception {
        BigInteger gasPrice = ddcSdkClient.baseService.getGasPrice(options);
        log.info(String.valueOf(gasPrice));
    }
}