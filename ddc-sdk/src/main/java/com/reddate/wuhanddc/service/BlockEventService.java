package com.reddate.wuhanddc.service;

import com.reddate.wuhanddc.constant.ErrorMessage;
import com.reddate.wuhanddc.dto.config.DDCContract;
import com.reddate.wuhanddc.dto.ddc.BaseEventBean;
import com.reddate.wuhanddc.exception.DDCException;
import com.reddate.wuhanddc.net.DDCWuhan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Strings;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.reddate.wuhanddc.constant.ContractConfig.DDCContracts;

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
     * @return wuhanddc official contract event data
     * @throws Exception
     */
    public <T extends BaseEventBean> ArrayList<T> getBlockEvent(BigInteger blockNum) throws Exception {
        if (Strings.isEmpty(DDCWuhan.getGatewayUrl())) {
            throw new DDCException(ErrorMessage.IS_EMPTY, "gateWayUrl");
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
            throw new DDCException(ErrorMessage.CUSTOM_ERROR, "getBlockByNumber failed");
        }
        if (Objects.nonNull(ethBlock.getError())) {
            throw new DDCException(ErrorMessage.ETH_PROXY_ERROR);
        }

        // get tx time
        BigInteger txTimestamp = ethBlock.getBlock().getTimestamp();
        List<EthBlock.TransactionResult> transactions = ethBlock.getBlock().getTransactions();

        // response
        ArrayList<T> arrayList = new ArrayList<>();
        if (transactions.size() > 0) {
            transactionData(txTimestamp, transactions, arrayList);
        }
        return arrayList;
    }


}
