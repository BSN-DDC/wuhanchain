package com.reddate.ddc.service;

import com.alibaba.fastjson.JSONObject;
import com.reddate.ddc.dto.ddc.BaseEventBean;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.websocket.WebSocketClient;
import org.web3j.protocol.websocket.WebSocketService;

import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wxq
 * @create 2022/1/24 13:32
 * @description event listener
 */
public class EventListenerByBlock extends BaseService {

    private final static Logger logger = LoggerFactory.getLogger(EventListenerByBlock.class);

    /**
     * thread pool
     */
    private static ThreadPoolExecutor executorService;
    /**
     * web3j
     */
    private static Web3j web3j = null;
    /**
     * subscribe
     */
    private Disposable subscribe;

    private boolean subscriptions = false;


    /**
     * get block event
     *
     * @param startBlockNum blockNumber
     * @return ddc official contract event data
     * @throws Exception
     */
    public void getBlockEventByListener(BigInteger startBlockNum, String wsUrl, Map<String, String> httpHeaders) {
        try {
            // webSocketClient
            WebSocketClient webSocketClient = new WebSocketClient(new URI(wsUrl),httpHeaders);
            WebSocketService webSocketService = new WebSocketService(webSocketClient, false);
            webSocketService.connect();
            web3j = Web3j.build(webSocketService);

            // ThreadPool
            executorService = new ThreadPoolExecutor(8, 8, 0L,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100000),
                    new ThreadPoolExecutor.CallerRunsPolicy());

            // startBlock, endBlock, fullTransactionObjects
            subscribe = web3j.replayPastAndFutureBlocksFlowable(DefaultBlockParameter.valueOf(startBlockNum), true)
                    .doOnError(e -> logger.error("doOnError:" + e.getMessage()))
                    .subscribe(this::executeBlock, ex -> logger.error("subscribe:" + ex.getMessage()), () -> onCompleted());

            while (true) {
                if (subscriptions) {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("analysisResult:" + e);
        }
    }

    /**
     * Subscription processing complete callback
     */
    public void onCompleted() {
        subscriptions = true;
    }

    /**
     * execute block
     *
     * @param block
     */
    public void executeBlock(EthBlock block) {
        executorService.submit(() -> {
            EthBlock.Block ethBlock = block.getBlock();
            // txTimestamp
            BigInteger txTimestamp = ethBlock.getTimestamp();
            executeTransaction(txTimestamp, ethBlock.getTransactions());
        });
    }

    /**
     * 解析交易信息
     *
     * @param transactions
     */
    public ArrayList<BaseEventBean> executeTransaction(BigInteger txTimestamp, List<EthBlock.TransactionResult> transactions) {
        ArrayList<BaseEventBean> arrayList = new ArrayList<>();
        if (!transactions.isEmpty()) {
            try {
                transactionData(txTimestamp, transactions, arrayList);

            } catch (Exception e) {
                logger.error("transaction getResult error, msg:{" + e + "}");
                throw new RuntimeException("executeTransaction getResult error, msg:{" + e.getMessage() + "}");
            }
            if (!CollectionUtils.isEmpty(arrayList)) {
                // business processing
                logger.info("block event data:" + JSONObject.toJSONString(arrayList));
            }
        } else {
            logger.info("Block noon transactions are not counted");
        }

        return arrayList;
    }


}
