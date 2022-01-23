package com.reddate.ddc;

import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.listener.SignEventListener;
import com.reddate.ddc.service.*;

import java.util.Objects;

/**
 * @author wxq
 * @create 2021/12/13 16:08
 * @description DdcClient
 */
public class DDCSdkClient {

    public static volatile DDCSdkClient ddcSdkClient = null;

    public AuthorityService authorityService;
    public ChargeService chargeService;
    public DDC1155Service ddc1155Service;
    public DDC721Service ddc721Service;
    public BlockEventService blockEventService;
    public AccountService accountService;
    public BaseService baseService;

    private void DDCSdkClient() {
    }

    private DDCSdkClient init(SignEventListener signEventListener) {
        if (Objects.isNull(signEventListener)) {
            throw new DDCException(ErrorMessage.SIGN_EVENT_LISTENER_IS_EMPTY);
        }
        ddc1155Service = new DDC1155Service();
        ddc721Service = new DDC721Service();
        chargeService = new ChargeService();
        authorityService = new AuthorityService();
        blockEventService = new BlockEventService();
        accountService = new AccountService();
        baseService = new BaseService();

        BaseService.signEventListener = signEventListener;
        ddcSdkClient = this;
        return ddcSdkClient;
    }

    public DDCSdkClient instance(SignEventListener signEventListener) {
        if (null == ddcSdkClient) {
            synchronized (DDCSdkClient.class) {
                if (null == ddcSdkClient) {
                    return init(signEventListener);
                }
            }
        }
        return ddcSdkClient;
    }
}
