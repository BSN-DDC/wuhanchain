package com.reddate.ddc.service;

import com.alibaba.fastjson.JSONObject;
import com.reddate.ddc.constant.*;
import com.reddate.ddc.dto.config.DDCContract;
import com.reddate.ddc.dto.ddc.*;
import com.reddate.ddc.dto.wuhanchain.BlockBean;
import com.reddate.ddc.dto.wuhanchain.RespJsonRpcBean;
import com.reddate.ddc.dto.wuhanchain.TransactionsBean;
import com.reddate.ddc.exception.DDCException;
import org.fisco.bcos.web3j.tx.txdecode.EventResultEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Strings;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.*;

import static com.reddate.ddc.constant.ContractConfig.DDCContracts;
import static com.reddate.ddc.constant.EventBeanMapConfig.eventBeanMapNew;
import static com.reddate.ddc.util.AnalyzeChainInfoUtils.analyzeEventLog;
import static com.reddate.ddc.util.AnalyzeChainInfoUtils.assembleBeanByReflect;

/**
 * @author wxq
 * @create 2021/12/23 11:22
 * @description BlockEventService
 */
public class BlockEventService extends BaseService {
    private final Logger logger = LoggerFactory.getLogger(BlockEventService.class);


    /**
     * get block event
     *
     * @param blockNum blockNumber
     * @param <T>
     * @return ddc official contract event data
     * @throws Exception
     */
    public <T extends BaseEventBean> ArrayList<T> getBlockEvent(BigInteger blockNum) throws Exception {
        // get block
        RespJsonRpcBean respJsonRpcBean = getBlockByNumber(blockNum);
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

            TransactionReceipt receipt = getTransactionReceipt(transaction.getHash());
            if (Objects.isNull(receipt)) {
                throw new DDCException(ErrorMessage.GET_TRANSACTION_RECEIPT_ERROR);
            }
            List<Log> logList = receipt.getLogs();
            for (Log log : logList) {
                // Get the contract for this event
                DDCContract contract = DDCContracts.stream().filter(t -> t.getContractAddress().equalsIgnoreCase(log.getAddress())).findAny().orElse(null);
                if (Objects.isNull(contract)) {
                    logger.info(String.format("BlockNum:%s,Contract:%s,Non-DDC official contracts do not have statistical data...", transaction.getBlockNumber(), log.getAddress()));
                    continue;
                }
                // contract info
                String contractAbi = contract.getContractAbi();
                String contractByteCode = contract.getContractBytecode();
                if (Strings.isEmpty(contractAbi) || Strings.isEmpty(contractByteCode)) {
                    throw new DDCException(ErrorMessage.CONTRACT_INFO_IS_EMPTY);
                }

                List<Log> logInfo = new ArrayList<>();
                logInfo.add(log);
                Map<String, List<List<EventResultEntity>>> map = analyzeEventLog(contractAbi, contractByteCode, JSONObject.toJSONString(logInfo));
                // Event to Object
                if (eventBeanMapNew.isEmpty()){
                    throw new DDCException(ErrorMessage.CONTRACT_INFO_IS_EMPTY);
                }
                for (Map.Entry<String, Class> entry : eventBeanMapNew.entrySet()) {
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
