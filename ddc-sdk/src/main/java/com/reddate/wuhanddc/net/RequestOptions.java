package com.reddate.wuhanddc.net;

import com.reddate.wuhanddc.constant.ErrorMessage;
import com.reddate.wuhanddc.dto.config.DDCContract;
import com.reddate.wuhanddc.exception.DDCException;
import com.reddate.wuhanddc.listener.SignEventListener;
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
    private final BigInteger gasPrice;
    private final BigInteger gasLimit;
    private final int connectTimeout;
    private final int networkRetries;


    private BigInteger nonce;

    public RequestOptions(BigInteger gasPrice, BigInteger gasLimit, int connectTimeout, int networkRetries) {
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.connectTimeout = connectTimeout;
        this.networkRetries = networkRetries;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
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


    public static <T> RequestOptionsBuilder builder() {

        return new RequestOptionsBuilder();
    }

    public static final class RequestOptionsBuilder {
        private BigInteger gasPrice;
        private BigInteger gasLimit;
        private int connectTimeout;
        private int networkRetries;
        private SignEventListener signEventListener;

        public RequestOptionsBuilder(DDCContract ddcContractConfig) {
            if (Objects.isNull(ddcContractConfig)) {
                throw new DDCException(ErrorMessage.CUSTOM_ERROR, "requestOptions init failed");
            }
            this.connectTimeout = DDCWuhan.getConnectTimeout();
            this.networkRetries = DDCWuhan.getMaxNetworkRetries();
        }

        public RequestOptionsBuilder() {
            this.gasPrice = null;
            this.gasLimit = null;
            this.connectTimeout = DDCWuhan.getConnectTimeout();
            this.networkRetries = DDCWuhan.getMaxNetworkRetries();
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
                    this.gasPrice,
                    this.gasLimit,
                    connectTimeout,
                    networkRetries);
        }
    }
}
