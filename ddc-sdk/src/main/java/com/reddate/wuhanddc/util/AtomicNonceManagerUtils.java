package com.reddate.wuhanddc.util;

import com.alibaba.fastjson.JSON;
import com.reddate.wuhanddc.constant.ErrorMessage;
import com.reddate.wuhanddc.exception.DDCException;
import com.reddate.wuhanddc.net.DDCWuhan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wxq
 * @create 2022/6/4 11:35
 * @description nonce manager
 */
public class AtomicNonceManagerUtils {
    private final Logger logger = LoggerFactory.getLogger(AtomicNonceManagerUtils.class);
    /**
     * 异常时 和 getNonce 共用obj锁
     */
    private Object obj = new Object();

    public static AtomicNonceManagerUtils atomicNonceManagerUtils = null;

    private static String TX_POLL_METHOD = "txpool_inspect";
    private static HttpService httpService;
    private static Web3j web3j;

    /**
     * 本地维护 nonce
     */
    private volatile AtomicLong addressNonce = new AtomicLong(-1);
    /**
     * 交易池队列中的 nonce
     */
    private volatile List<Long> queuedNonce = new LinkedList<>();


    private void AtomicNonceManagerUtils() {
    }

    public AtomicNonceManagerUtils instance() {
        if (null == atomicNonceManagerUtils) {
            synchronized (AtomicNonceManagerUtils.class) {
                if (null == atomicNonceManagerUtils) {
                    httpService = new HttpService(DDCWuhan.getGatewayUrl());
                    web3j = Web3j.build(httpService);
                    atomicNonceManagerUtils = new AtomicNonceManagerUtils();
                }
            }
        }
        return atomicNonceManagerUtils;
    }


    /**
     * 获取节点上 pending 状态的 nonce
     * @return
     * @throws IOException
     */
    protected BigInteger getNonceByNode() throws IOException {
        EthGetTransactionCount ethGetTransactionCount =
                web3j.ethGetTransactionCount(
                        DDCWuhan.getNonceManagerAddress(), DefaultBlockParameterName.PENDING)
                        .send();
        if (Objects.nonNull(ethGetTransactionCount.getError())) {
            throw new DDCException(ErrorMessage.REQUEST_FAILED.getCode(), ethGetTransactionCount.getError().getMessage());
        }
        // account's current nonce(pending).
        BigInteger accountNonce = ethGetTransactionCount.getTransactionCount();
        logger.info("pending nonce：" + accountNonce);
        return accountNonce;
    }

    /**
     * 获取本地维护的 nonce，并处理交易池中的交易
     * @return
     * @throws IOException
     */
    public Long getNonce() throws IOException {
        synchronized (obj) {
            if (addressNonce.get() == -1) {
                // 方法加锁，防止异常重置后，交易继续发送
                // 触发异常后，需要获取交易池中的数据，线程暂停 500毫秒：保证异常之前的所有的交易发送出去，进入队列中
                try {
                    Thread.sleep(DDCWuhan.getTxPoolSleepTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 获取链上最新pending状态的nonce值
                addressNonce.set(Long.valueOf(getNonceByNode().toString()));

                // 获取当前节点队列中的数据：nonce值
                queuedNonce = getTxPoolQueued();

                // 检查当前的nonce是否已存在队列中
                if (!queuedNonce.isEmpty()) {
                    Long getQueuedNonce = checkQueuedNonce(Long.valueOf(addressNonce.get()));
                    addressNonce.set(getQueuedNonce);
                    queuedNonce = getTxPoolQueued();
                    logger.info("重置后 队列不为空 Nonce:" + addressNonce.get());
                }
            } else {
                // 本地nonce +1
                addressNonce.incrementAndGet();
                // 检查当前的nonce是否已存在队列中
                if (!queuedNonce.isEmpty()) {
                    Long getQueuedNonce = checkQueuedNonce(Long.valueOf(addressNonce.get()));

                    addressNonce.set(getQueuedNonce);
                    // 重新获取交易池中的数据，防止有网络原因刚发送到链上的交易
                    queuedNonce = getTxPoolQueued();

                    Long nodePendingNonce = Long.valueOf(getNonceByNode().toString());

                    if (addressNonce.get() <= nodePendingNonce) {
                        checkQueuedNonce(Long.valueOf(addressNonce.get()));
                    }
                    logger.info("队列不为空 Nonce:" + addressNonce.get());
                }
            }
            return addressNonce.get();
        }
    }

    public Long getCurrentNonce() {
        return addressNonce.get();
    }

    /**
     * 异常发生时必须调用
     *
     * @throws IOException
     */
    public synchronized void resetNonce() {
        synchronized (obj) {
            logger.info("触发异常时的nonce：" + addressNonce.get());

            addressNonce.set(Long.valueOf("-1"));
        }

    }

    public synchronized void setNonce(Long value) {
        addressNonce.set(value);
    }

    /**
     * 检查addressNonce是否存在交易池的队列中，如果存在则从队列中移除同时（addressNonce +1）
     *
     * @param nonce
     * @return
     */
    public Long checkQueuedNonce(Long nonce) {
        logger.info("交易池队列中的nonce：" + JSON.toJSONString(queuedNonce));
        if (queuedNonce.size() > 0) {
            Iterator<Long> it = queuedNonce.iterator();
            while (it.hasNext()) {
                Long iteratorNonce = Long.valueOf(String.valueOf(it.next()));
                if (iteratorNonce.equals(nonce)) {
                    it.remove();
                    //checkQueuedNonce(nonce);
                    nonce = nonce + 1L;
                }
            }
        }
        return nonce;
    }

    /**
     * 获取交易池中的数据
     * 列出当前待包含在下一个块中的所有事务的文本摘要，以及仅计划在未来执行的事务。
     * 这是一种专门为开发人员定制的方法，用于快速查看池中的事务并发现任何潜在问题。
     */
    public List<Long> getTxPoolQueued() throws IOException {
        Response response = new Request<>(TX_POLL_METHOD, Arrays.asList(), httpService, Response.class).send();
        if (Objects.isNull(response)) {
            throw new DDCException(ErrorMessage.GET_TX_POOL_INSPECT_ERROR);
        }
        if (Objects.nonNull(response.getError())) {
            throw new DDCException(ErrorMessage.REQUEST_FAILED.getCode(), response.getError().getMessage());
        }

        LinkedHashMap result = (LinkedHashMap) response.getResult();
        LinkedHashMap<String, LinkedHashMap<Long, LinkedHashMap>> queued = (LinkedHashMap) result.getOrDefault("queued", null);

        // 找出 fromAddress 队列中的交易
        List<Long> accountNonce = new LinkedList<>();
        for (Map.Entry<String, LinkedHashMap<Long, LinkedHashMap>> entry : queued.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(DDCWuhan.getNonceManagerAddress())) {
                Iterator<Long> iterator = entry.getValue().keySet().iterator();
                while (iterator.hasNext()) {
                    Long iteratorNonce = Long.valueOf(String.valueOf(iterator.next()));
                    accountNonce.add(iteratorNonce);
                }
            }
        }
        Collections.sort(accountNonce, Long::compareTo);
        return accountNonce;
    }
}
