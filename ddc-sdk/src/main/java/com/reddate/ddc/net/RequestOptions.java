package com.reddate.ddc.net;

import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.dto.config.DDCContract;
import com.reddate.ddc.dto.config.Gateway;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.listener.SignEventListener;
import com.reddate.ddc.service.*;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.Objects;

/**
 * @author wxq
 * @create 2021/12/15 17:49
 * @description RequestOptions
 */

@EqualsAndHashCode(callSuper = false)
public class RequestOptions {
    private final String gateWayUrl;
    private final String userAddress;
    private final String contractAbi;
    private final String contractBytecode;
    private final String contractAddress;
    private final BigInteger gasPrice;
    private final BigInteger gasLimit;
    private final int connectTimeout;
    private final int readTimeout;
    private final int networkRetries;
    private SignEventListener signEventListener;
    private static DDCContract ddcContractConfig;
    private static Gateway gatewayConfig;
    private static SignEventListener requestSignEventListener;


    private BigInteger nonce;

    public RequestOptions(String gateWayUrl, String userAddress, String contractAbi, String contractBytecode, String contractAddress, BigInteger gasPrice, BigInteger gasLimit, int connectTimeout, int readTimeout, int networkRetries, SignEventListener signEventListener) {
        this.gateWayUrl = gateWayUrl;
        this.userAddress = userAddress;
        this.contractAbi = contractAbi;
        this.contractBytecode = contractBytecode;
        this.contractAddress = contractAddress;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.networkRetries = networkRetries;
        this.signEventListener = signEventListener;
    }

    public static RequestOptions getDefault(Gateway gatewayConfig) {
        return new RequestOptions(
                gatewayConfig.getGateWayUrl(),
                null,
                null,
                null,
                null,
                gatewayConfig.getGasPrice(),
                gatewayConfig.getGasLimit(),
                AbstractDDC.getConnectTimeout(),
                AbstractDDC.getReadTimeout(),
                AbstractDDC.getMaxNetworkRetries(),
                null);
    }

    public static RequestOptions getDefault() {
        return new RequestOptions(
                BaseService.gatewayConfig.getGateWayUrl(),
                null,
                null,
                null,
                null,
                BaseService.gatewayConfig.getGasPrice(),
                BaseService.gatewayConfig.getGasLimit(),
                AbstractDDC.getConnectTimeout(),
                AbstractDDC.getReadTimeout(),
                AbstractDDC.getMaxNetworkRetries(),
                null);
    }

    public String getGateWayUrl() {
        return gateWayUrl;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public String getContractAbi() {
        return contractAbi;
    }

    public String getContractBytecode() {
        return contractBytecode;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public SignEventListener getSignEventListener() {
        return signEventListener;
    }


    public BigInteger getNonce() {
        return nonce;
    }
    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getNetworkRetries() {
        return networkRetries;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public static <T> RequestOptionsBuilder builder(T t) {
        if (t.equals(DDC721Service.class)) {
            requestSignEventListener = DDC721Service.signEventListener;
            ddcContractConfig = DDC721Service.ddcContract;
        }
        if (t.equals(DDC1155Service.class)) {
            requestSignEventListener = DDC1155Service.signEventListener;
            ddcContractConfig = DDC1155Service.ddcContract;
        }
        if (t.equals(ChargeService.class)) {
            requestSignEventListener = ChargeService.signEventListener;
            ddcContractConfig = ChargeService.ddcContract;
        }
        if (t.equals(AuthorityService.class)) {
            requestSignEventListener = AuthorityService.signEventListener;
            ddcContractConfig = AuthorityService.ddcContract;
        }
        gatewayConfig = BaseService.gatewayConfig;
        return new RequestOptionsBuilder(ddcContractConfig, gatewayConfig, requestSignEventListener);
    }

    public static <T> RequestOptionsBuilder builder() {
        return new RequestOptionsBuilder();
    }

    public static final class RequestOptionsBuilder {

        private String gateWayUrl;
        private String userAddress;
        private String contractAbi;
        private String contractBytecode;
        private String contractAddress;
        private String privateKey;
        private BigInteger gasPrice;
        private BigInteger gasLimit;
        private int connectTimeout;
        private int readTimeout;
        private int networkRetries;
        private SignEventListener signEventListener;

        public RequestOptionsBuilder(DDCContract ddcContractConfig, Gateway gatewayConfig, SignEventListener signEventListener) {
            if (Objects.isNull(ddcContractConfig)) {
                throw new DDCException(ErrorMessage.REQUEST_OPTIONS_INIT_FAILED);
            }
            this.gateWayUrl = gatewayConfig.getGateWayUrl();
            this.userAddress = ddcContractConfig.getSignUserAddress();
            this.contractAbi = ddcContractConfig.getContractAbi();
            this.contractBytecode = ddcContractConfig.getContractBytecode();
            this.contractAddress = ddcContractConfig.getContractAddress();
            this.gasPrice = gatewayConfig.getGasPrice();
            this.gasLimit = gatewayConfig.getGasLimit();
            this.connectTimeout = AbstractDDC.DEFAULT_CONNECT_TIMEOUT;
            this.readTimeout = AbstractDDC.getReadTimeout();
            this.networkRetries = AbstractDDC.getMaxNetworkRetries();
            this.signEventListener = signEventListener;
        }

        public RequestOptionsBuilder() {
            this.gateWayUrl = null;
            this.userAddress = null;
            this.contractAbi = null;
            this.contractBytecode = null;
            this.contractAddress = null;
            this.gasPrice = null;
            this.gasLimit = null;
            this.connectTimeout = AbstractDDC.getConnectTimeout();
            this.readTimeout = AbstractDDC.getReadTimeout();
            this.networkRetries = AbstractDDC.getMaxNetworkRetries();
            this.signEventListener = null;
        }


        public String getGateWayUrl() {
            return gateWayUrl;
        }

        public RequestOptionsBuilder setGateWayUrl(String gateWayUrl) {
            normalizeApiKey(gateWayUrl);
            this.gateWayUrl = gateWayUrl;
            return this;
        }

        public RequestOptionsBuilder setSignEventListener(SignEventListener signEventListener) {
            this.signEventListener = signEventListener;
            return this;
        }

        public BigInteger getGasPrice() {
            return gasPrice;
        }

        public RequestOptionsBuilder setGasPrice(BigInteger gasPrice) {
            this.gasPrice = gasPrice;
            return this;
        }

        public BigInteger getGasLimit() {
            return gasLimit;
        }

        public RequestOptionsBuilder setGasLimit(BigInteger gasLimit) {
            this.gasLimit = gasLimit;
            return this;
        }

        public RequestOptionsBuilder setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public RequestOptionsBuilder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public RequestOptionsBuilder setNetworkRetries(int networkRetries) {
            this.networkRetries = networkRetries;
            return this;
        }

        /**
         * Constructs a {@link RequestOptions} with the specified values.
         */
        public RequestOptions build() {
            return new RequestOptions(
                    this.gateWayUrl,
                    this.userAddress,
                    this.contractAbi,
                    this.contractBytecode,
                    this.contractAddress,
                    this.gasPrice,
                    this.gasLimit,
                    connectTimeout,
                    readTimeout,
                    networkRetries,
                    this.signEventListener);
        }
    }

    private static String normalizeApiKey(String gateWayUrl) {
        // null gateWayUrl are considered "valid"
        if (gateWayUrl == null) {
            return null;
        }
        String normalized = gateWayUrl.trim();
        if (normalized.isEmpty()) {
            throw new DDCException(ErrorMessage.EMPTY_GATEWAY_URL_SPECIFIED);
        }
        return normalized;
    }

}
