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
            .setAuthorityAddress("0x466D5b0eA174a2DD595D40e0B30e433FCe6517F5")
            .setChargeAddress("0xCa97bF3a19403805d391102908665b16B4d0217C")
            .setDdc721Address("0xad3B52B4F4bd9198DC69dD9cE4aC9846667461a2")
            .setDdc1155Address("0x061e59c74815994DAb4226a0D344711F18E0F418")
            .setCrossChainAddress("0xc4E12bB845D9991ee26718E881C712B2c0cB2048")
            .setChainId(BigInteger.valueOf(5555))
            .build();

    static {
        DDCWuhan.setGatewayUrl("https://opbningxia.bsngate.com:18602/api/[projectId]/rpc");
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