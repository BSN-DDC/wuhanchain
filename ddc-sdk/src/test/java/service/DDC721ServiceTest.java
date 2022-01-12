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

    /*
     * isApprovedForAll            0xe985e9c5
     * safeTransferFrom            0xb88d4fde
     * getApproved                 0x081812fc
     * initialize                  0x4cd88b76
     * mint                        0xd0def521
     * setApprovalForAll           0xa22cb465
     * supportsInterface           0x01ffc9a7
     * symbol                      0x95d89b41
     * approve                     0x095ea7b3
     * freeze                      0xd7a78db8
     * ownerOf                     0x6352211e
     * renounceOwnership           0x715018a6
     * setAuthorityLogic           0x41044052
     * unFreeze                    0xd302b0dc
     * balanceOf                   0x70a08231
     * name                        0x06fdde03
     * owner                       0x8da5cb5b
     * setChargeLogic              0xa419a333
     * transferFrom                0x23b872dd
     * transferOwnership           0xf2fde38b
     * burn                        0x42966c68
     * ddcURI                      0x293ec97c
     *
     * */

    SignEventListener signEventListener = event -> transactionSignature(event.getRawTransaction());

    DDCSdkClient  service = new DDCSdkClient().instance("src/main/resources/contractConfig.json", signEventListener);


    private static String transactionSignature(RawTransaction transaction) {
        String privateKey = "583b92bc8b210a639f07daac25b4631560d0907b4706ff5abfe0c363ffb3b424";
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction, 5555, credentials);
        return Numeric.toHexString(signedMessage);
    }

    @Test
    void mint() throws Exception {
        String tx = service.ddc721Service.mint("0x019ba4600e117f06e3726c0b100a2f10ec52339e", "wang-xxqq-9919");
        log.info(tx);
        assertNotNull(tx);

    }

    @Test
    void safeMint() throws Exception {

        String tx = service.ddc721Service.mint("0x019ba4600e117f06e3726c0b100a2f10ec52339e", "wang-xxqq-9919");
        log.info(tx);
        assertNotNull(tx);

    }

    @Test
    void approve() throws Exception {
        String tx = service.ddc721Service.approve("0x9d37d92d3bca605a49f21642c309e578b16040fd", new BigInteger("4"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void getApproved() throws Exception {
        String tx = service.ddc721Service.getApproved(new BigInteger("4"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void setApprovalForAll() throws Exception {
        String tx = service.ddc721Service.setApprovalForAll("0x9d37d92d3bca605a49f21642c309e578b16040fd", true);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void isApprovedForAll() throws Exception {
        Boolean tx = service.ddc721Service.isApprovedForAll("0x019ba4600e117f06e3726c0b100a2f10ec52339e", "0x9d37d92d3bca605a49f21642c309e578b16040fd");
        log.info(String.valueOf(tx));
        assertNotNull(tx);
    }

    @Test
    void safeTransferFrom() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;
        String tx = service.ddc721Service.safeTransferFrom("0x019ba4600e117f06e3726c0b100a2f10ec52339e", "0x9d37d92d3bca605a49f21642c309e578b16040fd", new BigInteger("4"), data);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void transferFrom() throws Exception {
        String tx = service.ddc721Service.transferFrom("0x019ba4600e117f06e3726c0b100a2f10ec52339e", "0x9d37d92d3bca605a49f21642c309e578b16040fd", new BigInteger("5"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void freeze() throws Exception {
        String tx = service.ddc721Service.freeze(new BigInteger("6"));
        log.info(tx);
        assertNotNull(tx);
    }


    @Test
    void unFreeze() throws Exception {
        String tx = service.ddc721Service.unFreeze(new BigInteger("6"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void burn() throws Exception {
        String tx = service.ddc721Service.burn(new BigInteger("6"));
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void balanceOf() throws Exception {
        BigInteger bigInteger = service.ddc721Service.balanceOf("0x019ba4600e117f06e3726c0b100a2f10ec52339e");
        log.info(bigInteger.toString());
        assertNotNull(bigInteger);
    }

    @Test
    void ownerOf() throws Exception {
        String account = service.ddc721Service.ownerOf(new BigInteger("5"));
        log.info(account);
        assertNotNull(account);
    }

    @Test
    void name() throws Exception {
        String name = service.ddc721Service.name();
        log.info(name);
        assertNotNull(name);
    }

    @Test
    void symbol() throws Exception {
        String symbol = service.ddc721Service.symbol();
        log.info(symbol);
        assertNotNull(symbol);
    }

    @Test
    void ddcURI() throws Exception {
        String ddcURI = service.ddc721Service.ddcURI(new BigInteger("5"));
        log.info(ddcURI);
    }

}