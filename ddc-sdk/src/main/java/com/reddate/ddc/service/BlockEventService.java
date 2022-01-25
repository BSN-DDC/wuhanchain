package com.reddate.ddc.service;

import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.dto.ddc.BaseEventBean;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.net.DDCWuhan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(BlockEventService.class);

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
        web3j = Web3j.build(new HttpService(DDCWuhan.getGatewayUrl()));
        if (Objects.isNull(web3j)) {
            throw new DDCException(ErrorMessage.REQUEST_FAILED);
        }
        // get block
        EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNum), true).send();
        if (Objects.isNull(ethBlock)) {
            throw new DDCException(ErrorMessage.GET_BLOCK_BY_NUMBER_ERROR);
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
