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
            .setAuthorityAddress("0xB746e96bC24bc9bC11515b6F39Cbe135d1b67a59")
            .setChargeAddress("0xf1b4db42b9a96CA2943C8e047552Fd6E05D55396")
            .setDdc721Address("0xb4B46D6B2C7BC4389759f9EBE141cFE086771561")
            .setDdc1155Address("0x5Bf9e07aBBF0cFbF21d02065529AE10e2Ef0a375")
            .setCrossChainAddress("0x4592F3833Bd666AC7647a90C07b4dC6a77ed30CF")
            .setChainId(BigInteger.valueOf(5555))
            .build();


    @Test
    public void createAccount() {
        Account account = ddcSdkClient.accountService.createAccount();
        log.info(JSONObject.toJSONString(account));
    }

}
