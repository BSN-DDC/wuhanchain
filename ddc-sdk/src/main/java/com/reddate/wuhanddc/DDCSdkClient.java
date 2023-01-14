package com.reddate.wuhanddc;

import com.reddate.wuhanddc.constant.ContractConfig;
import com.reddate.wuhanddc.constant.ErrorMessage;
import com.reddate.wuhanddc.constant.EventBeanMapConfig;
import com.reddate.wuhanddc.dto.config.DDCContract;
import com.reddate.wuhanddc.eip712.DDC1155MetaTransaction;
import com.reddate.wuhanddc.eip712.DDC721MetaTransaction;
import com.reddate.wuhanddc.exception.DDCException;
import com.reddate.wuhanddc.listener.SignEventListener;
import com.reddate.wuhanddc.service.*;
import org.fisco.bcos.web3j.utils.Strings;

import java.math.BigInteger;
import java.util.Objects;

import static com.reddate.wuhanddc.constant.ContractConfig.DDCContracts;

/**
 * @author skj
 * @create 2022/08/20 16:12
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
    public CrossChainService crossChainService;
    public BaseService baseService;

    public SignEventListener signEventListener;

    public DDC721MetaTransaction ddc721MetaTransaction;
    public DDC1155MetaTransaction ddc1155MetaTransaction;

    private DDCSdkClient(Builder builder) {
        if (null == ddcSdkClient) {
            synchronized (DDCSdkClient.class) {
                if (null == ddcSdkClient) {
                    init(builder);
                }
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private DDCSdkClient init(Builder builder) {
        String authorityAddress = builder.authorityAddress;
        String chargeAddress = builder.chargeAddress;
        String ddc721Address = builder.ddc721Address;
        String ddc1155Address = builder.ddc1155Address;
        String crossChainAddress = builder.crossChainAddress;
        BigInteger chainId = builder.chainId;

        // Set contract address for event resolution
        EventBeanMapConfig.setEventBeanMap(authorityAddress, chargeAddress, ddc721Address, ddc1155Address, crossChainAddress);

        // Set contract address for contract call
        DDCContracts.add(new DDCContract("authority", ContractConfig.AUTHORITY_ABI, ContractConfig.AUTHORITY_BIN, authorityAddress));
        DDCContracts.add(new DDCContract("charge", ContractConfig.CHARGE_ABI, ContractConfig.CHARGE_BIN, chargeAddress));
        DDCContracts.add(new DDCContract("721", ContractConfig.DDC_721_ABI, ContractConfig.DDC_721_BIN, ddc721Address));
        DDCContracts.add(new DDCContract("1155", ContractConfig.DDC_1155_ABI, ContractConfig.DDC_1155_BIN, ddc1155Address));
        DDCContracts.add(new DDCContract("crossChain", ContractConfig.DDC_CROSS_CHAIN_ABI, ContractConfig.DDC_CROSS_CHAIN_BIN, crossChainAddress));

        if (Objects.isNull(builder.signEventListener)) {
            throw new DDCException(ErrorMessage.CUSTOM_ERROR, "not register sign event listener");
        }

        ddc1155Service = new DDC1155Service();
        ddc721Service = new DDC721Service();
        chargeService = new ChargeService();
        authorityService = new AuthorityService();
        blockEventService = new BlockEventService();
        accountService = new AccountService();
        crossChainService = new CrossChainService();
        baseService = new BaseService();

        ddc1155MetaTransaction = DDC1155MetaTransaction.builder()
                .setChainId(chainId)
                .setContractAddress(ddc1155Address)
                .build();
        ddc721MetaTransaction = DDC721MetaTransaction.builder()
                .setChainId(chainId)
                .setContractAddress(ddc721Address)
                .build();

        BaseService.signEventListener = builder.signEventListener;

        ddcSdkClient = this;

        return ddcSdkClient;
    }

    public static final class Builder {
        private SignEventListener signEventListener;
        private String authorityAddress;
        private String chargeAddress;
        private String ddc721Address;
        private String ddc1155Address;
        private String crossChainAddress;
        private BigInteger chainId;

        public Builder() {
        }

        public static Builder aDDCSdkClient() {
            return new Builder();
        }

        public Builder setSignEventListener(SignEventListener signEventListener) {
            this.signEventListener = signEventListener;
            return this;
        }

        public Builder setAuthorityAddress(String authorityAddress) {
            this.authorityAddress = authorityAddress;
            return this;
        }

        public Builder setChargeAddress(String chargeAddress) {
            this.chargeAddress = chargeAddress;
            return this;
        }

        public Builder setDdc721Address(String ddc721Address) {
            this.ddc721Address = ddc721Address;
            return this;
        }

        public Builder setDdc1155Address(String ddc1155Address) {
            this.ddc1155Address = ddc1155Address;
            return this;
        }

        public Builder setCrossChainAddress(String crossChainAddress) {
            this.crossChainAddress = crossChainAddress;
            return this;
        }

        public Builder setChainId(BigInteger chainId) {
            this.chainId = chainId;
            return this;
        }

        public DDCSdkClient build() {
            if (Strings.isEmpty(authorityAddress) ||
                Strings.isEmpty(chargeAddress) ||
                Strings.isEmpty(ddc721Address) ||
                Strings.isEmpty(ddc1155Address) ||
                Strings.isEmpty(crossChainAddress)) {
                throw new DDCException(ErrorMessage.CUSTOM_ERROR, "contract address cannot be empty!");
            }
            if (chainId == null) {
                throw new DDCException(ErrorMessage.CUSTOM_ERROR, "chainId cannot be null!");
            }
            return new DDCSdkClient(this);
        }
    }
}
