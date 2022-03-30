package com.reddate.wuhanddc.service;

import com.reddate.wuhanddc.constant.ErrorMessage;
import com.reddate.wuhanddc.dto.ddc.BaseEventBean;
import com.reddate.wuhanddc.exception.DDCException;
import com.reddate.wuhanddc.net.DDCWuhan;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Strings;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wxq
 * @create 2021/12/23 11:22
 * @description BlockEventService
 */
public class BlockEventService extends BaseService {
    /**
     * web3j
     */
    private static Web3j web3j = null;

    /**
     * get block event
     *
     * @param blockNum blockNumber
     * @param <T>
     * @return ddc official contract event data
     * @throws Exception
     */
    public <T extends BaseEventBean> ArrayList<T> getBlockEvent(BigInteger blockNum) throws Exception {
        if (Strings.isEmpty(DDCWuhan.getGatewayUrl())) {
            throw new DDCException(ErrorMessage.EMPTY_GATEWAY_URL_SPECIFIED);
        }
        HttpService httpService = new HttpService(DDCWuhan.getGatewayUrl());
        String apiKey = DDCWuhan.getGatewayApiKey();
        if (!Strings.isEmpty(apiKey)) {
            httpService.addHeader("x-api-key", apiKey);
        }
        web3j = Web3j.build(httpService);
        if (Objects.isNull(web3j)) {
            throw new DDCException(ErrorMessage.REQUEST_FAILED);
        }
        // get block
        EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNum), true).send();
        if (Objects.isNull(ethBlock)) {
            throw new DDCException(ErrorMessage.GET_BLOCK_BY_NUMBER_ERROR);
        }
        if (Objects.nonNull(ethBlock.getError())) {
            throw new DDCException(ErrorMessage.ETH_PROXY_ERROR);
        }

        // get tx time
        BigInteger txTimestamp = ethBlock.getBlock().getTimestamp();
        List<EthBlock.TransactionResult> transactions = ethBlock.getBlock().getTransactions();

        // response
        ArrayList<T> arrayList = new ArrayList<>();
        transactionData(txTimestamp, transactions, arrayList);
        return arrayList;
    }


}
