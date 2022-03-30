package com.reddate.wuhanddc.net;

/**
 * @author wxq
 * @create 2021/12/20 10:50
 * @description ddc sdk request config
 */
public abstract class DDCWuhan {
    public static final int DEFAULT_CONNECT_TIMEOUT = 30 * 1000;
    public static final int DEFAULT_READ_TIMEOUT = 80 * 1000;

    public static final int CONNECTION_KEEP_ALIVE_TIME = 30 * 1000;
    public static final int POOLING_CONNECTION_MANAGER_MAX_TOTAL = 1000;
    public static final int POOLING_CONNECTION_MANAGER_MAX_PER_ROUTE = 100;
    public static final int POOLING_CONNECTION_MANAGER_VALIDATE_AFTER_INACTIVITY = 30 * 1000;
    public static final int POOLING_CONNECTION_MANAGER_CLOSE_IDLE_CONNECTIONS = 30 * 1000;

    private static volatile String gatewayApiKey = null;
    private static volatile String gatewayUrl = null;
    private static volatile int connectTimeout = -1;
    private static volatile int readTimeout = -1;
    private static volatile int maxNetworkRetries = 0;


    /**
     * Returns the gateway headers api-key
     *
     * @return api-key
     */
    public static String getGatewayApiKey() {
        return gatewayApiKey;
    }

    /**
     * If key is enabled, this value needs to be set
     *
     * @param apiKey
     */
    public static void setGatewayApiKey(final String apiKey) {
        gatewayApiKey = apiKey;
    }

    /**
     * Returns the gateway gatewayUrl
     *
     * @return gatewayUrl
     */
    public static String getGatewayUrl() {
        return gatewayUrl;
    }

    /**
     * Set gateway URL
     *
     * @param url
     */
    public static void setGatewayUrl(String url) {
        gatewayUrl = url;
    }


    /**
     * Returns the connection timeout.
     *
     * @return timeout value in milliseconds
     */
    public static int getConnectTimeout() {
        if (connectTimeout == -1) {
            return DEFAULT_CONNECT_TIMEOUT;
        }
        return connectTimeout;
    }

    /**
     * Sets the timeout value that will be used for making new connections to the Stripe API (in
     * milliseconds).
     *
     * @param timeout timeout value in milliseconds
     */
    public static void setConnectTimeout(final int timeout) {
        connectTimeout = timeout;
    }

    /**
     * Returns the read timeout.
     *
     * @return timeout value in milliseconds
     */
    public static int getReadTimeout() {
        if (readTimeout == -1) {
            return DEFAULT_READ_TIMEOUT;
        }
        return readTimeout;
    }

    /**
     * Sets the timeout value that will be used when reading data from an established connection to
     * the Stripe API (in milliseconds).
     *
     * <p>Note that this value should be set conservatively because some API requests can take time
     * and a short timeout increases the likelihood of causing a problem in the backend.
     *
     * @param timeout timeout value in milliseconds
     */
    public static void setReadTimeout(final int timeout) {
        readTimeout = timeout;
    }

    /**
     * Returns the maximum number of times requests will be retried.
     *
     * @return the maximum number of times requests will be retried
     */
    public static int getMaxNetworkRetries() {
        return maxNetworkRetries;
    }

    /**
     * Sets the maximum number of times requests will be retried.
     *
     * @param numRetries the maximum number of times requests will be retried
     */
    public static void setMaxNetworkRetries(final int numRetries) {
        maxNetworkRetries = numRetries;
    }
}
