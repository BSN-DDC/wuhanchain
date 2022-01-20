package service;

import com.reddate.ddc.DDCSdkClient;
import com.reddate.ddc.listener.SignEventListener;
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
    DDCSdkClient ddcSdkClient = new DDCSdkClient().instance("src/main/resources/contractConfig.json", signEventListener);

    //  The address the transaction is send from.
    public String sender = "0x24a95d34dcbc74f714031a70b077e0abb3306066";

    private static String transactionSignature(String sender, RawTransaction transaction) {
        // sender: Obtain the private key according to the sender and complete its signature

        //sender privateKey
        String privateKey = "0x20bd77e9c6c920cba10f4ef3fdd10e0cfbf8a4781292d8c8d61e37458445888";
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        return Numeric.toHexString(signedMessage);
    }

    @Test
    void mint() throws Exception {
        String tx = ddcSdkClient.ddc721Service.mint(sender, "0x88e58851815d6dfd28586d1d9a9785a838f3b63c", "wang-xxqq-9919");
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void safeMint() throws Exception {

        String tx = ddcSdkClient.ddc721Service.mint(sender, "0x019ba4600e117f06e3726c0b100a2f10ec52339e", "wang-xxqq-9919");
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
        String ddcURI = ddcSdkClient.ddc721Service.ddcURI(new BigInteger("5"));
        log.info(ddcURI);
    }

}