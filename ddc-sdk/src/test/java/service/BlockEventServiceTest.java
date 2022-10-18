package service;

import com.alibaba.fastjson.JSON;
import com.reddate.wuhanddc.DDCSdkClient;
import com.reddate.wuhanddc.listener.SignEventListener;
import com.reddate.wuhanddc.net.DDCWuhan;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;


@Slf4j
class BlockEventServiceTest {

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

    static {
        DDCWuhan.setGatewayUrl("https://opbtest.bsngate.com:18602/api/4bbed86d890f42b6b70de34c9be425dd/rpc");
    }

    @Test
    void getBlockEvent() throws Exception {
        ArrayList<Object> result = new ArrayList<>();
        result.addAll(ddcSdkClient.blockEventService.getBlockEvent(new BigInteger("4359665")));
        log.info(JSON.toJSONString(result));

        result.forEach(t -> {
            System.out.println(t.getClass());
        });
    }
}