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
            .setAuthorityAddress("0xB746e96bC24bc9bC11515b6F39Cbe135d1b67a59")
            .setChargeAddress("0xf1b4db42b9a96CA2943C8e047552Fd6E05D55396")
            .setDdc721Address("0xb4B46D6B2C7BC4389759f9EBE141cFE086771561")
            .setDdc1155Address("0x5Bf9e07aBBF0cFbF21d02065529AE10e2Ef0a375")
            .setCrossChainAddress("0x6ca34e1bFcC9A36113DdCE0D76d35E71dBbdd770")
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