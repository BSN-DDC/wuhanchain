package com.reddate.wuhanddc.eip712;

import org.web3j.abi.DefaultFunctionEncoder;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class DDC1155MetaTransaction {

    private static final String EIP712_DOMAIN = "EIP712Domain(string name,string version,uint256 chainId,address verifyingContract)";
    private static final String META_SAFE_MINT = "metaSafeMint(address to,uint256 amount,string memory ddcURI,bytes memory data,uint256 nonce,uint256 deadline,bytes memory sign)";
    private static final String META_SAFE_MINT_BATCH = "metaSafeMintBatch(address to,uint256[] memory amounts,string[] memory ddcURIs,bytes memory data,uint256 nonce,uint256 deadline,bytes memory sign)";
    private static final String META_SAFE_TRANSFER_FROM = "metaSafeTransferFrom(address from,address to,uint256 ddcId,uint256 amount,bytes memory data,uint256 nonce,uint256 deadline,bytes memory sign)";
    private static final String META_SAFE_BATCH_TRANSFER_FROM = "metaSafeBatchTransferFrom(address from,address to,uint256[] memory ddcIds,uint256[] memory amounts,bytes memory data,uint256 nonce,uint256 deadline,bytes memory sign)";
    private static final String META_BURN = "metaBurn(address owner,uint256 ddcId,uint256 nonce,uint256 deadline,bytes memory sign)";
    private static final String META_BURN_BATCH = "metaBurnBatch(address owner,uint256[] memory ddcIds,uint256 nonce,uint256 deadline,bytes memory sign)";

    private String digestPartOne = "0x19";
    private String digestPartTwo = "0x01";
    private BigInteger chainId;
    private String contractAddress;

    private final DefaultFunctionEncoder functionEncoder = new DefaultFunctionEncoder();

    private DDC1155MetaTransaction(Builder builder) {
        chainId = builder.chainId;
        contractAddress = builder.contractAddress;
    }

    public static Builder builder() {
        return new Builder();
    }


    private String getDomainSeparator() {
        byte[] domain = Hash.sha3(EIP712_DOMAIN.getBytes(StandardCharsets.UTF_8));
        byte[] name = Hash.sha3("DDC1155".getBytes(StandardCharsets.UTF_8));
        byte[] version = Hash.sha3("2.0".getBytes(StandardCharsets.UTF_8));

        String result = TypeEncoder.encode(new Bytes32(domain)) +
                TypeEncoder.encode(new Bytes32(name)) +
                TypeEncoder.encode(new Bytes32(version)) +
                TypeEncoder.encode(new Uint256(chainId)) +
                TypeEncoder.encode(new Address(contractAddress));
        return Hash.sha3(result);
    }

    private byte[] getTypeHash(String func) {
        return Hash.sha3(func.getBytes(StandardCharsets.UTF_8));
    }

    private String getDigest(String parameters) {
        String parametersHash = Hash.sha3(parameters);

        ByteArrayOutputStream concatenatedArrayEncodingBuffer = new ByteArrayOutputStream();
        for (String hexStr : Arrays.asList(digestPartOne, digestPartTwo, getDomainSeparator(), parametersHash)) {
            byte[] bytes = Numeric.hexStringToByteArray(hexStr);
            concatenatedArrayEncodingBuffer.write(bytes, 0, bytes.length);
        }
        String concatenated = Numeric.toHexString(concatenatedArrayEncodingBuffer.toByteArray());
        return Hash.sha3(concatenated);
    }

    public String getSafeMintDigest(String to, BigInteger amount, String ddcURI, byte[] data, BigInteger nonce, BigInteger deadline) {
        String parameters = functionEncoder.encodeParameters(Arrays.<Type>asList(
                new Bytes32(getTypeHash(META_SAFE_MINT)),
                new Uint256(amount),
                new Address(to),
                new Utf8String(ddcURI),
                // new DynamicBytes(data), 合约方法中没有检查data参数
                new Uint256(nonce),
                new Uint256(deadline)
        ));
        return getDigest(parameters);
    }

    public String getSafeMintBatchDigest(String to, List<BigInteger> amounts, List<String> ddcURIs, byte[] data, BigInteger nonce, BigInteger deadline) {
        String parameters = functionEncoder.encodeParameters(Arrays.<Type>asList(
                new Bytes32(getTypeHash(META_SAFE_MINT_BATCH)),
                new Address(to),
                new DynamicArray<Uint256>(
                        Uint256.class,
                        Utils.typeMap(amounts, Uint256.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        Utils.typeMap(ddcURIs, Utf8String.class)),
                // new DynamicBytes(data), 合约方法中没有检查data参数
                new Uint256(nonce),
                new Uint256(deadline)
        ));
        return getDigest(parameters);
    }

    public String getSafeTransferFromDigest(String from, String to, BigInteger ddcId, BigInteger amount, byte[] data, BigInteger nonce, BigInteger deadline) {
        String parameters = functionEncoder.encodeParameters(Arrays.<Type>asList(
                new Bytes32(getTypeHash(META_SAFE_TRANSFER_FROM)),
                new Address(from),
                new Address(to),
                new Uint256(ddcId),
                new Uint256(amount),
                // new DynamicBytes(data),
                new Uint256(nonce),
                new Uint256(deadline)
        ));
        return getDigest(parameters);
    }

    public String getSafeBatchTransferFromDigest(String from, String to, List<BigInteger> ddcIds, List<BigInteger> amounts, byte[] data, BigInteger nonce, BigInteger deadline) {
        String parameters = functionEncoder.encodeParameters(Arrays.<Type>asList(
                new Bytes32(getTypeHash(META_SAFE_BATCH_TRANSFER_FROM)),
                new Address(from),
                new Address(to),
                new DynamicArray<Uint256>(
                        Uint256.class,
                        Utils.typeMap(ddcIds, Uint256.class)),
                new DynamicArray<Uint256>(
                        Uint256.class,
                        Utils.typeMap(amounts, Uint256.class)),
                // new DynamicBytes(data),
                new Uint256(nonce),
                new Uint256(deadline)
        ));
        return getDigest(parameters);
    }

    public String getBurnDigest(String owner, BigInteger ddcId, BigInteger nonce, BigInteger deadline) {
        String parameters = functionEncoder.encodeParameters(Arrays.<Type>asList(
                new Bytes32(getTypeHash(META_BURN)),
                new Address(owner),
                new Uint256(ddcId),
                new Uint256(nonce),
                new Uint256(deadline)
        ));
        return getDigest(parameters);
    }

    public String getBurnBatchDigest(String owner, List<BigInteger> ddcIds, BigInteger nonce, BigInteger deadline) {
        String parameters = functionEncoder.encodeParameters(Arrays.<Type>asList(
                new Bytes32(getTypeHash(META_BURN_BATCH)),
                new Address(owner),
                new DynamicArray<Uint256>(
                        Uint256.class,
                        Utils.typeMap(ddcIds, Uint256.class)),
                new Uint256(nonce),
                new Uint256(deadline)
        ));
        return getDigest(parameters);
    }

    public byte[] generateSignature(String hexPrivateKey, String digest) {
        Sign.SignatureData signatureData = Sign.signMessage(Numeric.hexStringToByteArray(digest), ECKeyPair.create(Numeric.toBigInt(hexPrivateKey)), false);

        ByteArrayOutputStream concatenatedArrayEncodingBuffer = new ByteArrayOutputStream();
        for (byte[] bytes : Arrays.asList(signatureData.getR(), signatureData.getS(), signatureData.getV())) {
            concatenatedArrayEncodingBuffer.write(bytes, 0, bytes.length);
        }
        return concatenatedArrayEncodingBuffer.toByteArray();
    }

    public static final class Builder {
        private BigInteger chainId;
        private String contractAddress;

        private Builder() {
        }

        public Builder setChainId(BigInteger chainId) {
            this.chainId = chainId;
            return this;
        }

        public Builder setContractAddress(String contractAddress) {
            this.contractAddress = contractAddress;
            return this;
        }

        public DDC1155MetaTransaction build() {
            return new DDC1155MetaTransaction(this);
        }
    }
}
