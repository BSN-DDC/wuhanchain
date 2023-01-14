package service;

import com.alibaba.fastjson.JSON;
import com.reddate.wuhanddc.DDCSdkClient;
import com.reddate.wuhanddc.dto.ddc.CrossChainTransferEventBean;
import com.reddate.wuhanddc.enums.CrossChainStateEnum;
import com.reddate.wuhanddc.enums.DDCTypeEnum;
import com.reddate.wuhanddc.listener.SignEventListener;
import com.reddate.wuhanddc.net.DDCWuhan;
import com.reddate.wuhanddc.param.CrossChainTransferParams;
import com.reddate.wuhanddc.param.UpdateCrossChainStatusParams;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author wxq
 * @create 2022/7/12 17:57
 * @description cross service test
 */
@Slf4j
public class CrossChainServiceTest {
    // sign event listener
    SignEventListener signEventListener = event -> transactionSignature(event.getSender(), event.getRawTransaction());

    // ddcSdkClient instantiation
    DDCSdkClient ddcSdkClient = DDCSdkClient.builder()
            .setSignEventListener(signEventListener)
            .setAuthorityAddress("0x466D5b0eA174a2DD595D40e0B30e433FCe6517F5")
            .setChargeAddress("0xCa97bF3a19403805d391102908665b16B4d0217C")
            .setDdc721Address("0xad3B52B4F4bd9198DC69dD9cE4aC9846667461a2")
            .setDdc1155Address("0x061e59c74815994DAb4226a0D344711F18E0F418")
            .setCrossChainAddress("0xc4E12bB845D9991ee26718E881C712B2c0cB2048")
            .setChainId(BigInteger.valueOf(5555))
            .build();


    // 签名账户地址
    public static String sender = "0xCd00A127C44E6E61070544e626ee5F9336D04e80";

    static {
        DDCWuhan.setGatewayUrl("https://opbningxia.bsngate.com:18602/api/[projectId]/rpc");
        DDCWuhan.setNonceManagerAddress(sender);
    }

    private static String transactionSignature(String sender, RawTransaction transaction) {
        // sender: Obtain the private key according to the sender and complete its signature


        String privateKey = "...";
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        return Numeric.toHexString(signedMessage);
    }


    @Test
    public void crossChainTransfer() throws Exception {
        // 构造跨链转移方法参数对象
        CrossChainTransferParams params = CrossChainTransferParams.builder()
                .setSender(sender)                                            // 起始链签名账户地址
                .setTo("0x6922D8af46d5e39c2a15cAa26eE692FCc118aDc5")          // 目标链接收者账户地址
                .setDdcId(BigInteger.valueOf(9231))                           // DDC唯一标识
                .setData("additional data".getBytes())                        // 附加数据
                .setDDCType(DDCTypeEnum.ERC721)                               // DDC类型
                .setToChainID(BigInteger.valueOf(100003))                     // 目标链chainId
                .setToCCAddr("0x44A175f7E830e4d66DC8BEdF8cfb9a9330B3F472")    // 目标链NFT合约地址
                .setSigner("0x9bde88224e7cf3ada6045fc0236d10b8cd5a94da")      // 目标链签名账户地址
                .setFuncName("crossChainMint")                                // 目标链NFT合约方法
                .build();
        // 调用SDK方法发起跨链交易
        String txHash = ddcSdkClient.crossChainService.crossChainTransfer(params);
        log.info(txHash);
        Thread.sleep(10000);
        // 查询跨链交易事件
        CrossChainTransferEventBean result = ddcSdkClient.crossChainService.getCrossChainTransferEvent(txHash);
        log.info(JSON.toJSONString(result));
    }

    @Test
    public void getCrossChainTransferEvent() throws Exception {
        String txHash = "0xa72a057d3f29e71f7c0a92764693330deaa95dbb9e0c43db532d4ff4e00768d4";
        CrossChainTransferEventBean result = ddcSdkClient.crossChainService.getCrossChainTransferEvent(txHash);
        log.info(JSON.toJSONString(result));
        assertNotNull(result);
    }

    @Test
    public void updateCrossChainStatus() throws Exception {
        UpdateCrossChainStatusParams params = UpdateCrossChainStatusParams.builder()
                .setState(CrossChainStateEnum.CROSS_CHAIN_FAILURE)
                .setCrossChainId(BigInteger.valueOf(7))
                .setSender(sender)
                .setRemark("no remark")
                .build();
        String txHash = ddcSdkClient.crossChainService.updateCrossChainStatus(params);
        log.info(txHash);
        assertNotNull(txHash);
    }
}
