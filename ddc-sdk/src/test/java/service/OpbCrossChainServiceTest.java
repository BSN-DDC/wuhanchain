package service;

import com.alibaba.fastjson.JSON;
import com.reddate.wuhanddc.DDCSdkClient;
import com.reddate.wuhanddc.dto.ddc.CrossChainTransferEventBean;
import com.reddate.wuhanddc.enums.CrossChainStateEnum;
import com.reddate.wuhanddc.enums.DDCTypeEnum;
import com.reddate.wuhanddc.listener.SignEventListener;
import com.reddate.wuhanddc.net.DDCWuhan;
import com.reddate.wuhanddc.param.CrossChainTransferParams;
import com.reddate.wuhanddc.param.OpbCrossChainTransferParams;
import com.reddate.wuhanddc.param.UpdateCrossChainStatusParams;
import com.reddate.wuhanddc.param.UpdateOpbCrossChainStatusParams;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class OpbCrossChainServiceTest {
    // sign event listener
    SignEventListener signEventListener = event -> transactionSignature(event.getSender(), event.getRawTransaction());

    // ddcSdkClient instantiation
    DDCSdkClient ddcSdkClient = DDCSdkClient.builder()
            .setSignEventListener(signEventListener)
            .setAuthorityAddress("0x466D5b0eA174a2DD595D40e0B30e433FCe6517F5")
            .setChargeAddress("0xCa97bF3a19403805d391102908665b16B4d0217C")
            .setDdc721Address("0xad3B52B4F4bd9198DC69dD9cE4aC9846667461a2")
            .setDdc1155Address("0x061e59c74815994DAb4226a0D344711F18E0F418")
            .setOpbCrossChainAddress("0xF2FFC996D612d35F3e86DF3179906E780749845D")
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
        OpbCrossChainTransferParams params = OpbCrossChainTransferParams.builder()
                .setSender(sender)                                            // 起始链签名账户地址
                .setTo("0x88d9a495d9c4b70a0d78b43a99b201bb314c8fd5")          // 目标链接收者账户地址
                .setIsLock(true)                                              //是否锁定
                .setDdcId(BigInteger.valueOf(138))                            // DDC唯一标识
                .setData("0x".getBytes(StandardCharsets.UTF_8))               // 附加数据
                .setDDCType(DDCTypeEnum.ERC721)                               // DDC类型
                .setToChainID(BigInteger.valueOf(4))                          // 目标链chainId
                .build();
        // 调用SDK方法发起跨链交易
        String txHash = ddcSdkClient.opbCrossChainService.crossChainTransfer(params);
        log.info(txHash);
    }

    @Test
    public void updateCrossChainStatus() throws Exception {
        UpdateOpbCrossChainStatusParams params = UpdateOpbCrossChainStatusParams.builder()
                .setState(CrossChainStateEnum.CROSS_CHAIN_FAILURE)
                .setCrossChainId(BigInteger.valueOf(3))
                .setSender(sender)
                .setRemark("no remark")
                .build();
        String txHash = ddcSdkClient.opbCrossChainService.updateCrossChainStatus(params);
        log.info(txHash);
        assertNotNull(txHash);
    }

}
