package com.reddate.ddc.service;

import com.alibaba.fastjson.JSONObject;
import com.reddate.ddc.constant.*;
import com.reddate.ddc.dto.config.BasicConfiguration;
import com.reddate.ddc.dto.config.DDCContract;
import com.reddate.ddc.dto.ddc.*;
import com.reddate.ddc.dto.wuhanchain.BlockBean;
import com.reddate.ddc.dto.wuhanchain.RespJsonRpcBean;
import com.reddate.ddc.dto.wuhanchain.TransactionsBean;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.net.RequestOptions;
import org.fisco.bcos.web3j.tx.txdecode.EventResultEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.*;

import static com.reddate.ddc.util.AnalyzeChainInfoUtils.analyzeEventLog;
import static com.reddate.ddc.util.AnalyzeChainInfoUtils.assembleBeanByReflect;

/**
 * @author wxq
 * @create 2021/12/23 11:22
 * @description BlockEventService
 */
public class BlockEventService extends BaseService {
    private final Logger logger = LoggerFactory.getLogger(BlockEventService.class);

    public volatile static BasicConfiguration configuration;

    /**
     * eventBeanMap
     */
    private HashMap<String, Class> eventBeanMap = new HashMap<>();

    public BlockEventService(BasicConfiguration basicConfiguration) {
        if (Objects.isNull(basicConfiguration)) {
            throw new DDCException(ErrorMessage.BASIC_CONFIGURATION_IS_EMPTY);
        }
        configuration = basicConfiguration;
        // event entity binding
        eventBeanMap.put(AuthorityFunctions.ADD_ACCOUNT_EVENT, AddAccountEventBean.class);
        eventBeanMap.put(AuthorityFunctions.UPDATE_ACCOUNT_STATE_EVENT, UpdateAccountStateEventBean.class);

        eventBeanMap.put(ChargeFunctions.RECHARGE_EVENT, ReChargeEventBean.class);
        eventBeanMap.put(ChargeFunctions.PAY_EVENT, PayEventBean.class);
        eventBeanMap.put(ChargeFunctions.SET_FEE_EVENT, SetFeeEventBean.class);
        eventBeanMap.put(ChargeFunctions.DELETE_FEE_EVENT, DeleteFeeEventBean.class);
        eventBeanMap.put(ChargeFunctions.DELETE_DDC_EVENT, DeleteDDCEventBean.class);

        eventBeanMap.put(DDC721Functions.DDC_721_TRANSFER_EVENT, DDC721TransferEventBean.class);
        eventBeanMap.put(DDC721Functions.DDC_721_FREEZE_EVENT, DDC721FreezeEventBean.class);
        eventBeanMap.put(DDC721Functions.DDC_721_UNFREEZE_EVENT, DDC721UnFreezeEventBean.class);

        eventBeanMap.put(DDC1155Functions.DDC_1155_TRANSFER_SINGLE_EVENT, DDC1155TransferSingleEventBean.class);
        eventBeanMap.put(DDC1155Functions.DDC_1155_TRANSFER_BATCH_EVENT, DDC1155TransferBatchEventBean.class);
        eventBeanMap.put(DDC1155Functions.DDC_1155_FREEZE_EVENT, DDC1155FreezeEventBean.class);
        eventBeanMap.put(DDC1155Functions.DDC_1155_UNFREEZE_EVENT, DDC1155UnFreezeEventBean.class);

    }


    public <T extends BaseEventBean> ArrayList<T> getBlockEvent(BigInteger blockNum) throws Exception {
        return getBlockEvent(blockNum, RequestOptions.getDefault(gatewayConfig));
    }

    public <T extends BaseEventBean> ArrayList<T> getBlockEvent(BigInteger blockNum, RequestOptions options) throws Exception {
        // get block
        RespJsonRpcBean respJsonRpcBean = getBlockByNumber(blockNum, options);
        BlockBean ethBlock = JSONObject.parseObject(JSONObject.toJSONString(respJsonRpcBean.getResult()), BlockBean.class);
        if (Objects.isNull(ethBlock)) {
            throw new DDCException(ErrorMessage.GET_BLOCK_BY_NUMBER_ERROR);
        }

        // get tx time
        String txTimestamp = ethBlock.getTimestamp();
        List<TransactionsBean> transactions = ethBlock.getTransactions();

        // response
        ArrayList<T> arrayList = new ArrayList<>();
        for (TransactionsBean transaction : transactions) {

            TransactionReceipt receipt = getTransactionReceipt(transaction.getHash(), options);
            if (Objects.isNull(receipt)) {
                throw new DDCException(ErrorMessage.GET_TRANSACTION_RECEIPT_ERROR);
            }
            List<Log> logList = receipt.getLogs();
            for (Log log : logList) {
                // Get the contract for this event
                DDCContract contract = configuration.getContracts().stream().filter(t -> t.getContractAddress().equalsIgnoreCase(log.getAddress())).findAny().orElse(null);
                if (Objects.isNull(contract)) {
                    logger.info(String.format("BlockNum:%s,Contract:%s,非DDC官方合约不统计数据...", blockNum, log.getAddress()));
                    continue;
                }
                String contractAbi = contract.getContractAbi();
                String contractByteCode = contract.getContractBytecode();

                List<Log> logInfo = new ArrayList<>();
                logInfo.add(log);
                Map<String, List<List<EventResultEntity>>> map = analyzeEventLog(contractAbi, contractByteCode, JSONObject.toJSONString(logInfo));
                // Event to Object
                for (Map.Entry<String, Class> entry : eventBeanMap.entrySet()) {
                    if (!map.containsKey(entry.getKey())) {
                        continue;
                    }

                    List<List<EventResultEntity>> eventLists = map.get(entry.getKey());
                    for (List<EventResultEntity> eventList : eventLists) {
                        try {
                            T eventBean = (T) assembleBeanByReflect(eventList, entry.getValue());
                            eventBean.setBlockHash(transaction.getHash());
                            eventBean.setTransactionInfoBean(transaction);
                            eventBean.setBlockNumber(transaction.getBlockNumber());
                            eventBean.setTimestamp(txTimestamp);
                            arrayList.add(eventBean);
                        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
                            e.printStackTrace();
                            try {
                                throw e;
                            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return arrayList;
    }
}
