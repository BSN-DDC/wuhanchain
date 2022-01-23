package service;

import com.reddate.ddc.DDCSdkClient;
import com.reddate.ddc.listener.SignEventListener;
import com.reddate.ddc.net.DDCWuhan;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import static org.junit.Assert.assertNotNull;

@Slf4j
class DDC721ServiceTest {

    // sign event listener
    SignEventListener signEventListener = event -> transactionSignature(event.getSender(), event.getRawTransaction());

    // ddcSdkClient instantiation
    DDCSdkClient ddcSdkClient = new DDCSdkClient().instance(signEventListener);

    //  The address the transaction is send from.
    public String sender = "0x3a0427c496c7e9408885d132e9fec0b042beb399";

    static {
        DDCWuhan.setGatewayUrl("https://opbtest.bsngate.com:18602/api/4bbed86d890f42b6b70de34c9be425dd/rpc");
    }

    private static String transactionSignature(String sender, RawTransaction transaction) {
        // sender: Obtain the private key according to the sender and complete its signature

        //sender privateKey
        String privateKey = "0x8f9d8a1619e35892cd36acba0150fd3e2aac8a20865eff75b2500ea7661a1076";
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        return Numeric.toHexString(signedMessage);
    }

    @Test
    void mint() throws Exception {
        String tx = ddcSdkClient.ddc721Service.mint(sender, "0xb8988d0f53cca1c0e14c7bf591db7f9f0f2eb7ca", "wang-xxqq-9919");
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void safeMint() throws Exception {
        String tx = ddcSdkClient.ddc721Service.mint(sender, "0xb8988d0f53cca1c0e14c7bf591db7f9f0f2eb7ca", "wang-xxqq-9919");
        log.info(tx);
        assertNotNull(tx);

    }

    @Test
    void approve() throws Exception {
        String tx = ddcSdkClient.ddc721Service.approve(sender, "0x9d37d92d3bca605a49f21642c309e578b16040fd", new BigInteger("4"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void getApproved() throws Exception {
        String tx = ddcSdkClient.ddc721Service.getApproved(new BigInteger("4"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void setApprovalForAll() throws Exception {
        String tx = ddcSdkClient.ddc721Service.setApprovalForAll(sender, "0x9d37d92d3bca605a49f21642c309e578b16040fd", true);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void isApprovedForAll() throws Exception {
        Boolean tx = ddcSdkClient.ddc721Service.isApprovedForAll("0x019ba4600e117f06e3726c0b100a2f10ec52339e", "0x9d37d92d3bca605a49f21642c309e578b16040fd");
        log.info(String.valueOf(tx));
        assertNotNull(tx);
    }

    @Test
    void safeTransferFrom() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;
        String tx = ddcSdkClient.ddc721Service.safeTransferFrom(sender, "0x019ba4600e117f06e3726c0b100a2f10ec52339e", "0x9d37d92d3bca605a49f21642c309e578b16040fd", new BigInteger("4"), data);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void transferFrom() throws Exception {
        String tx = ddcSdkClient.ddc721Service.transferFrom(sender, "0x019ba4600e117f06e3726c0b100a2f10ec52339e", "0x9d37d92d3bca605a49f21642c309e578b16040fd", new BigInteger("5"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void freeze() throws Exception {
        String tx = ddcSdkClient.ddc721Service.freeze(sender, new BigInteger("6"));
        log.info(tx);
        assertNotNull(tx);
    }


    @Test
    void unFreeze() throws Exception {
        String tx = ddcSdkClient.ddc721Service.unFreeze(sender, new BigInteger("6"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void burn() throws Exception {
        String tx = ddcSdkClient.ddc721Service.burn(sender, new BigInteger("6"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void balanceOf() throws Exception {
        BigInteger bigInteger = ddcSdkClient.ddc721Service.balanceOf("0x019ba4600e117f06e3726c0b100a2f10ec52339e");
        log.info(bigInteger.toString());
        assertNotNull(bigInteger);
    }

    @Test
    void ownerOf() throws Exception {
        String account = ddcSdkClient.ddc721Service.ownerOf(new BigInteger("5"));
        log.info(account);
        assertNotNull(account);
    }

    @Test
    void name() throws Exception {
        String name = ddcSdkClient.ddc721Service.name();
        log.info(name);
        assertNotNull(name);
    }

    @Test
    void symbol() throws Exception {
        String symbol = ddcSdkClient.ddc721Service.symbol();
        log.info(symbol);
        assertNotNull(symbol);
    }

    @Test
    void ddcURI() throws Exception {
        String ddcURI = ddcSdkClient.ddc721Service.ddcURI(new BigInteger("35"));
        log.info(ddcURI);
    }

    @Test
    void setDDCURI() throws Exception {
        String tx = ddcSdkClient.ddc721Service.setURI(sender ,new BigInteger(""),"");
        log.info(tx);
        assertNotNull(tx);
    }
}