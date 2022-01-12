package service;

import com.alibaba.fastjson.JSON;
import com.reddate.ddc.DDCSdkClient;
import com.reddate.ddc.listener.SignEventListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;


@Slf4j
class BlockEventServiceTest {

    SignEventListener signEventListener = event -> "";

    DDCSdkClient service = new DDCSdkClient().instance("src/main/resources/contractConfig.json", signEventListener);


    @Test
    void getBlockEvent() throws Exception {
        ArrayList<Object> result = new ArrayList<>();
        result.addAll(service.blockEventService.getBlockEvent(new BigInteger("767448")));
        log.info(JSON.toJSONString(result));

        result.forEach(t -> {
            System.out.println(t.getClass());
        });
    }
}