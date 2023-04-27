package blockchain;

import java.io.Serializable;
import java.security.*;
import java.util.ArrayList;
import java.util.List;

public class Block implements Serializable {
    private String previousBlockHash;
    private long blockNumber;
    private List<Transaction> transactions;
    private String blockHash;
    private byte[] blockSignature;

    public Block(long blockNumber, String previousBlockHash) {
        this.blockNumber = blockNumber;
        this.previousBlockHash = previousBlockHash;
        this.transactions = new ArrayList<>();
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public byte[] getBlockSignature() {
        return blockSignature;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public String calculateBlockHash() {
        String data = previousBlockHash + blockNumber + transactions.size() + transactions.toString();
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256"); // hashing using SHA-256
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = digest.digest(data.getBytes());
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public void signBlock(PrivateKey privateKey) { // sign the block with the private key of the server
        String data = blockNumber + previousBlockHash + transactions.toString();
        Signature signature = null;
        try {
            signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes());
            blockSignature = signature.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyBlockSignature(PublicKey publicKey) {
        String data = blockNumber + previousBlockHash + transactions.toString();
        Signature signature = null;
        try {
            signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(data.getBytes());
            return signature.verify(blockSignature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String toString() {
        return "Block{" +
                "blockNumber=" + blockNumber +
                ", previousBlockHash='" + previousBlockHash + '\'' +
                ", transactions=" + transactions +
                ", blockHash='" + blockHash + '\'' +
                ", blockSignature=" + bytesToHex(blockSignature) +
                '}';
    }
}
