package com.reddate.ddc.service;

import com.google.common.collect.ImmutableList;
import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.dto.ddc.Account;
import com.reddate.ddc.exception.DDCException;
import org.bitcoinj.crypto.*;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;
import sun.security.provider.SecureRandom;

import java.util.List;

/**
 * @author wxq
 * @create 2021/12/25 15:35
 * @description account service
 */
public class AccountService {


    private final static ImmutableList<ChildNumber> BIP44_ETH_ACCOUNT_ZERO_PATH =
            ImmutableList.of(new ChildNumber(44, true), new ChildNumber(60, true),
                    ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);

    /**
     * 生成地址
     *
     * @return
     */
    public Account createAccount() {
        try {
            // mnemonic
            SecureRandom secureRandom = new SecureRandom();
            byte[] entropy = new byte[DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS / 8];
            secureRandom.engineNextBytes(entropy);
            List<String> str = MnemonicCode.INSTANCE.toMnemonic(entropy);

            // seed
            byte[] seed = MnemonicCode.toSeed(str, "");

            // master key
            DeterministicKey masterPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed);
            DeterministicHierarchy deterministicHierarchy = new DeterministicHierarchy(masterPrivateKey);

            // child key
            DeterministicKey deterministicKey = deterministicHierarchy.deriveChild(BIP44_ETH_ACCOUNT_ZERO_PATH, false, true, new ChildNumber(0));
            byte[] bytes = deterministicKey.getPrivKeyBytes();
            ECKeyPair keyPair = ECKeyPair.create(bytes);

            Account accountInfo = new Account();
            accountInfo.setAddress(String.format("0x%s", Keys.getAddress(keyPair.getPublicKey())));
            accountInfo.setMnemonic(str.toString());
            accountInfo.setPublicKey(String.format("0x%s", keyPair.getPublicKey().toString(16)));
            accountInfo.setPrivateKey(String.format("0x%s", keyPair.getPrivateKey().toString(16)));
            return accountInfo;
        } catch (Exception e) {
            throw new DDCException(ErrorMessage.FAILED_TO_CREATE_ACCOUNT);
        }
    }
}
