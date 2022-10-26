package com.reddate.wuhanddc.service;

import com.alibaba.fastjson.JSONObject;
import com.reddate.wuhanddc.constant.DDCCrossChainFunctions;
import com.reddate.wuhanddc.constant.ErrorMessage;
import com.reddate.wuhanddc.dto.config.DDCContract;
import com.reddate.wuhanddc.dto.ddc.CrossChainTransferEventBean;
import com.reddate.wuhanddc.dto.ddc.UpdateCrossChainStatusEventBean;
import com.reddate.wuhanddc.dto.wuhanchain.RespJsonRpcBean;
import com.reddate.wuhanddc.enums.CrossChainStateEnum;
import com.reddate.wuhanddc.enums.DDCTypeEnum;
import com.reddate.wuhanddc.exception.DDCException;
import com.reddate.wuhanddc.net.RequestOptions;
import com.reddate.wuhanddc.param.CrossChainTransferParams;
import com.reddate.wuhanddc.param.UpdateCrossChainStatusParams;
import org.fisco.bcos.web3j.tx.txdecode.EventResultEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Strings;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.*;

import static com.reddate.wuhanddc.constant.ContractConfig.DDCContracts;
import static com.reddate.wuhanddc.util.AnalyzeChainInfoUtils.assembleBeanByReflect;
import static com.reddate.wuhanddc.util.AnalyzeChainInfoUtils.analyzeEventLog;

/**
 * @author wxq
 * @create 2022/7/11 17:56
 * @description cross chain service
 */
public class CrossChainService extends BaseService {

    private final Logger logger = LoggerFactory.getLogger(CrossChainService.class);

    public static DDCContract crossChainContract;

    public CrossChainService() {
        crossChainContract = DDCContracts.stream().filter(t -> "crossChain".equals(t.getConfigType())).findFirst().orElse(null);
    }


    /**
     * DDC cross chain transfer.
     * The DDC owner or authorizer can call the DDC cross chain application contract to
     * conduct the cross chain flow of DDC through this method.
     *
     * @param params
     * @return hash
     * @throws Exception
     */
    public String crossChainTransfer(CrossChainTransferParams params) throws Exception {
        return crossChainTransfer(params, null);
    }

    public String crossChainTransfer(CrossChainTransferParams params, RequestOptions options) throws Exception {
        // check sender
        checkSender(params.getSender());

        // check ddcType
        if (Objects.isNull(params.getDdcType())) {
            throw new DDCException(ErrorMessage.IS_NULL, "DDCType");
        }

        // check singer
        checkAccount(params.getSigner(), "singer account");

        // check to
        checkAccount(params.getTo(), "to account");

        // check ddc Id
        checkDdcId(params.getDdcId());

        // check toChainId
        if (BigInteger.ZERO.compareTo(params.getToChainID()) >= 0) {
            throw new DDCException(ErrorMessage.LESS_THAN_ZERO, "toChainID");
        }

        // check to contract
        checkAccount(params.getToCCAddr(), "toCCAddr");

        // check funcName
        if (Strings.isEmpty(params.getFuncName())) {
            throw new DDCException(ErrorMessage.IS_EMPTY, "funcName");
        }


        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(params.getDdcType().getType());
        arrayList.add(params.getSigner());
        arrayList.add(params.getTo());
        arrayList.add(params.getDdcId());
        arrayList.add(params.getData());
        arrayList.add(params.getToChainID());
        arrayList.add(params.getToCCAddr());
        arrayList.add(params.getFuncName());

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(params.getSender(), options, arrayList, DDCCrossChainFunctions.CROSS_CHAIN_TRANSFER, crossChainContract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * Get DDC cross chain transfer result.
     *
     * @param txHash
     * @return hash
     * @throws Exception
     */
    public CrossChainTransferEventBean getCrossChainTransferEvent(String txHash) throws Exception {
        TransactionReceipt receipt = getTransactionReceipt(txHash);
        if (Objects.isNull(receipt)) {
            throw new DDCException(ErrorMessage.IS_EMPTY, "transactionReceipt");
        }
        List<Log> logList = receipt.getLogs();
        Map<String, List<List<EventResultEntity>>> map = analyzeEventLog(crossChainContract.getContractAbi(), crossChainContract.getContractBytecode(), JSONObject.toJSONString(logList));
        logger.info("event data: {}", map);

        ArrayList<CrossChainTransferEventBean> beanArrayList = new ArrayList<>();

        List<List<EventResultEntity>> eventLists = map.get(DDCCrossChainFunctions.CROSS_CHAIN_TRANSFER_EVENT);
        if (eventLists == null) {
            throw new DDCException(ErrorMessage.IS_EMPTY, "crossChainTransferEvent");
        }
        for (List<EventResultEntity> eventList : eventLists) {
            try {
                CrossChainTransferEventBean eventBean = assembleBeanByReflect(eventList, CrossChainTransferEventBean.class);
                beanArrayList.add(eventBean);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
                try {
                    throw e;
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return beanArrayList.get(0);
    }

    /**
     * Cross chain rollback
     * Though this method,the operator can call the DDC cross chain application contract to rollback the DDC cross chain.
     *
     * @param params
     * @return hash
     * @throws Exception
     */
    public String updateCrossChainStatus(UpdateCrossChainStatusParams params) throws Exception {
        return updateCrossChainStatus(params, null);
    }

    public String updateCrossChainStatus(UpdateCrossChainStatusParams params, RequestOptions options) throws Exception {
        // check sender
        checkSender(params.getSender());

        // check state
        if (Objects.isNull(params.getState())) {
            throw new DDCException(ErrorMessage.IS_NULL, "cross chain state");
        }

        if (params.getState().equals(CrossChainStateEnum.CROSS_CHAIN_PENDING)) {
            throw new DDCException(ErrorMessage.ILLEGAL_PARAMETER, "state");
        }

        // check remark
        checkNonEmpty(params.getRemark(), "remark");

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(params.getCrossChainId());
        arrayList.add(params.getState().getState());
        arrayList.add(params.getRemark());

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(params.getSender(), options, arrayList, DDCCrossChainFunctions.UPDATE_CROSS_CHAIN_STATUS, crossChainContract);
        return (String) respJsonRpcBean.getResult();
    }
}
