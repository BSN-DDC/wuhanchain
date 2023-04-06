package com.reddate.wuhanddc.service;

import com.reddate.wuhanddc.DDCSdkClient;
import com.reddate.wuhanddc.constant.DDCCrossChainFunctions;
import com.reddate.wuhanddc.constant.DDCOpbCrossChainFunctions;
import com.reddate.wuhanddc.constant.ErrorMessage;
import com.reddate.wuhanddc.dto.config.DDCContract;
import com.reddate.wuhanddc.dto.wuhanchain.RespJsonRpcBean;
import com.reddate.wuhanddc.enums.CrossChainStateEnum;
import com.reddate.wuhanddc.exception.DDCException;
import com.reddate.wuhanddc.net.RequestOptions;
import com.reddate.wuhanddc.param.OpbCrossChainTransferParams;
import com.reddate.wuhanddc.param.UpdateOpbCrossChainStatusParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;

import static com.reddate.wuhanddc.constant.ContractConfig.DDCContracts;

public class OpbCrossChainService extends BaseService {

    private final Logger logger = LoggerFactory.getLogger(CrossChainService.class);

    public static DDCContract crossChainContract;

    public OpbCrossChainService() {
        crossChainContract = DDCContracts.stream().filter(t -> "opbCrossChain".equals(t.getConfigType())).findFirst().orElse(null);
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
    public String crossChainTransfer(OpbCrossChainTransferParams params) throws Exception {
        return crossChainTransfer(params, null);
    }

    public String crossChainTransfer(OpbCrossChainTransferParams params, RequestOptions options) throws Exception {
        //check contract address
        if (StringUtils.isEmpty(crossChainContract.getContractAddress())) {
            throw new DDCException(ErrorMessage.IS_EMPTY, "opbCrossChain address");
        }

        // check sender
        checkSender(params.getSender());

        // check ddcType
        if (Objects.isNull(params.getDdcType())) {
            throw new DDCException(ErrorMessage.IS_NULL, "DDCType");
        }

        // check to
        checkAccount(params.getTo(), "to account");

        // check ddc Id
        checkDdcId(params.getDdcId());

        // check toChainId
        if (BigInteger.ZERO.compareTo(params.getToChainID()) >= 0) {
            throw new DDCException(ErrorMessage.LESS_THAN_ZERO, "toChainID");
        }

        // check isLock
        if (Objects.isNull(params.getIsLock())) {
            throw new DDCException(ErrorMessage.IS_NULL, "isLock");
        }

        // input params
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(params.getDdcType().getType());
        arrayList.add(params.getDdcId());
        arrayList.add(params.getIsLock());
        arrayList.add(params.getToChainID());
        arrayList.add(params.getTo());
        arrayList.add(params.getData());

        // send transaction
        RespJsonRpcBean respJsonRpcBean = assembleTransactionAndSend(params.getSender(), options, arrayList, DDCOpbCrossChainFunctions.CROSS_CHAIN_TRANSFER, crossChainContract);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * Cross chain rollback
     * Though this method,the operator can call the DDC cross chain application contract to rollback the DDC cross chain.
     *
     * @param params
     * @return hash
     * @throws Exception
     */
    public String updateCrossChainStatus(UpdateOpbCrossChainStatusParams params) throws Exception {
        return updateCrossChainStatus(params, null);
    }

    public String updateCrossChainStatus(UpdateOpbCrossChainStatusParams params, RequestOptions options) throws Exception {

        //check contract address
        if (StringUtils.isEmpty(crossChainContract.getContractAddress())) {
            throw new DDCException(ErrorMessage.IS_EMPTY, "opbCrossChain address");
        }


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
