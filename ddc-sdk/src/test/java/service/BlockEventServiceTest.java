package service;

import com.alibaba.fastjson.JSON;
import com.reddate.ddc.DDCSdkClient;
import com.reddate.ddc.listener.SignEventListener;
import com.reddate.ddc.net.DDCWuhan;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;


@Slf4j
class BlockEventServiceTest {

    // sign event listener
    SignEventListener signEventListener = event -> null;

    // ddcSdkClient instantiation
    DDCSdkClient ddcSdkClient = new DDCSdkClient().instance(signEventListener);

    static {
        DDCWuhan.setGatewayUrl("https://opbtest.bsngate.com:18602/api/4bbed86d890f42b6b70de34c9be425dd/rpc");
    }

    @Test
    void getBlockEvent() throws Exception {
        ArrayList<Object> result = new ArrayList<>();
        result.addAll(ddcSdkClient.blockEventService.getBlockEvent(new BigInteger("881520")));
        log.info(JSON.toJSONString(result));

        result.forEach(t -> {
            System.out.println(t.getClass());
        });
    }
}