package com.reddate.wuhanddc.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.reddate.wuhanddc.dto.ddc.BaseEventBean;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.websocket.WebSocketClient;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.protocol.websocket.events.LogNotification;
import org.web3j.protocol.websocket.events.NewHeadsNotification;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wxq
 * @create 2022/6/2 13:32
 * @description EventListenerByBlock
 */
public class EventListenerByBlock extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(EventListenerByBlock.class);
    private static ThreadPoolExecutor executorService;
    private static Web3j web3j = null;
    private Disposable subscribe;
    private boolean subscriptions = false;

    public EventListenerByBlock() {
    }

    /**
     * Creates a Flowable instance that emits all blocks from the requested block number to the most current. Once it has emitted the most current block, it starts emitting new blocks as they are created.
     * Params:
     * startBlock – the block number we wish to request from
     * fullTransactionObjects – if we require full Transaction objects to be provided in the EthBlock responses
     * Returns:
     * a Flowable instance to emit all requested blocks and future
     * @param startBlockNum
     * @param wsUrl
     */
    public void getBlockEventByListener(BigInteger startBlockNum, String wsUrl) {
        try {
            getSocketClient(wsUrl);
            this.subscribe = web3j.replayPastAndFutureBlocksFlowable(DefaultBlockParameter.valueOf(startBlockNum), true).doOnError((e) -> {
                logger.error("doOnError:" + e.getMessage());
            }).subscribe(this::executeBlock, (ex) -> {
                logger.error("subscribe:" + ex.getMessage());
            }, () -> {
                this.onCompleted();
            });

            while (!this.subscriptions) {
            }
        } catch (Exception var5) {
            logger.error("analysisResult:" + var5);
        }

    }

    public void onCompleted() {
        this.subscriptions = true;
    }

    public void executeBlock(EthBlock block) {
        executorService.submit(() -> {
            EthBlock.Block ethBlock = block.getBlock();
            BigInteger txTimestamp = ethBlock.getTimestamp();
            System.out.println("blockNub:" + ethBlock.getNumber());
            this.executeTransaction(txTimestamp, ethBlock.getTransactions());
        });
    }

    public ArrayList<BaseEventBean> executeTransaction(BigInteger txTimestamp, List<EthBlock.TransactionResult> transactions) {
        ArrayList<BaseEventBean> arrayList = new ArrayList();
        if (!transactions.isEmpty()) {
            try {
                System.out.println(JSONObject.toJSONString(transactions));
                this.transactionData(txTimestamp, transactions, arrayList);
            } catch (Exception var5) {
                logger.error("transaction getResult error, msg:{" + var5 + "}");
                throw new RuntimeException("executeTransaction getResult error, msg:{" + var5.getMessage() + "}");
            }
            if (!CollectionUtils.isEmpty(arrayList)) {
                logger.info("block event data:" + JSONObject.toJSONString(arrayList));
            }
        } else {
            logger.info("Block noon transactions are not counted");
        }

        return arrayList;
    }

    /**
     * Creates a Flowable instance that emits a notification when a new header is appended to a chain, including chain reorganizations.
     * Returns:
     * a Flowable instance that emits a notification for every new header
     *
     * @param wsUrl
     */
    public void newHeadsNotifications(String wsUrl) {
        try {
            getSocketClient(wsUrl);
            this.subscribe = web3j.newHeadsNotifications().subscribe(this::executeNewHeadsNotification, (ex) -> {
                logger.error("executeNewHeadsNotification error:" + ex.getMessage());
            }, () -> {
                this.onCompleted();
            });
            while (!this.subscriptions) {
            }
        } catch (Exception var5) {
            logger.error("executeNewHeadsNotification error:" + var5);
        }

    }

    public void executeNewHeadsNotification(NewHeadsNotification newHeads) {
        executorService.submit(() -> {
            // heads 处理 System.out.println(Numeric.toBigInt(newHeads.getParams().getResult().getNumber()) );

        });
    }

    /**
     * Creates aa Flowable instance that emits notifications for logs included in new imported blocks.
     * Params:
     * addresses – only return logs from this list of address. Return logs from all addresses if the list is empty
     * topics – only return logs that match specified topics. Returns logs for all topics if the list is empty
     * Returns:
     * a Flowable instance that emits logs included in new blocks
     *
     * @param wsUrl
     */
    public void logsNotifications(String wsUrl) {
        try {
            getSocketClient(wsUrl);
            List<String> addresses = Lists.newArrayList();
            List<String> topics = Lists.newArrayList();
            this.subscribe = web3j.logsNotifications(addresses, topics).subscribe(this::executeLogsNotifications, (ex) -> {
                logger.error("executeLogsNotifications error: " + ex.getMessage());
            }, () -> this.onCompleted());
            while (!this.subscriptions) {
            }
        } catch (Exception var5) {
            logger.error("executeLogsNotifications error:" + var5);
        }

    }

    public void executeLogsNotifications(LogNotification logs) {
        executorService.submit(() -> {
            // 日志处理 System.out.println(JSONObject.toJSONString(logs));
        });
    }

    /**
     * Create an flowable to filter for specific log events on the blockchain.
     * Params:
     * ethFilter – filter criteria
     * Returns:
     * a Flowable instance that emits all Log events matching the filter
     *
     * @param wsUrl
     */
    public void ethLogFlowable(String wsUrl) {
        try {
            getSocketClient(wsUrl);

            EthFilter ethFilter = new EthFilter();
            ethFilter.addSingleTopic("0xc3d58168c5ae7397731d063d5bbf3d657854427343f4c083240f7aacaa2d0f62");
            this.subscribe = web3j.ethLogFlowable(ethFilter).subscribe(this::executeLog, (ex) -> {
                logger.error("executeLog error: " + ex.getMessage());
            }, () -> this.onCompleted());
            while (!this.subscriptions) {
            }
        } catch (Exception var5) {
            logger.error("executeLog error:" + var5);
        }

    }

    public void executeLog(Log logs) {
        executorService.submit(() -> {
            // 日志处理 System.out.println(JSONObject.toJSONString(logs));
        });
    }

    /**
     * getSocketClient
     *
     * @param wsUrl
     * @throws URISyntaxException
     * @throws ConnectException
     */
    private void getSocketClient(String wsUrl) throws URISyntaxException, ConnectException {
        WebSocketClient webSocketClient = new WebSocketClient(new URI(wsUrl));
        WebSocketService webSocketService = new WebSocketService(webSocketClient, false);
        webSocketService.connect();
        if (null == web3j) {
            web3j = Web3j.build(webSocketService);
        }
        if (null == executorService) {
            executorService = new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(100), new ThreadPoolExecutor.CallerRunsPolicy());
        }
    }
}
