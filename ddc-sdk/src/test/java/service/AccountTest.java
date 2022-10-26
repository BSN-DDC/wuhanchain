package service;

import com.alibaba.fastjson.JSONObject;
import com.reddate.wuhanddc.DDCSdkClient;
import com.reddate.wuhanddc.dto.ddc.Account;
import com.reddate.wuhanddc.listener.SignEventListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;


/**
 * @author wxq
 * @create 2021/12/25 15:49
 * @description accountTest
 */
@Slf4j
public class AccountTest {

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
    public void createAccount() {
        Account account = ddcSdkClient.accountService.createAccount();
        log.info(JSONObject.toJSONString(account));
    }

}
