package objects;

import java.io.Serializable;
import java.security.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Class that represents a block of the blockchain.
 * 
 * @author Tomás Correia | fc57102
 * @author Miguel Pato | fc56372
 * @author João Vieira | fc45677
 */
public class Block implements Serializable {
    private String previousBlockHash;
    private long blockNumber;
    private List<Transaction> transactions;
    private String blockHash;
    private byte[] blockSignature;

    /**
     * Constructor for the Block class.
     * @param blockNumber    Number of the block.
     * @param previousBlockHash   Hash of the previous block.
     */
    public Block(long blockNumber, String previousBlockHash) {
        this.blockNumber = blockNumber;
        this.previousBlockHash = previousBlockHash;
        this.transactions = new ArrayList<>();
    }

    /**
     * Method that gets the number of the block.
     * @return  The number of the block.
     */
    public long getBlockNumber() {
        return blockNumber;
    }

    /**
     * Method that gets the hash of the previous block.
     * @return  The hash of the previous block.
     */
    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    /**
     * Method that gets the transactions of the block.
     * @return  The transactions of the block.
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Method that gets the hash of the block.
     * @return  The hash of the block.
     */
    public String getBlockHash() {
        return blockHash;
    }

    /**
     * Method that gets the signature of the block.
     * @return  The signature of the block.
     */
    public byte[] getBlockSignature() {
        return blockSignature;
    }

    /**
     * Method that adds a transaction to the block.
     * @param transaction   Transaction to be added.
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * Method that calculates the hash of the block.
     * @return  The hash of the block.
     */
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

    /**
     * Method that converts a byte array to a hexadecimal string.
     * @param bytes    Byte array to be converted.
     * @return  The hexadecimal string.
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * Method that signs the block with the private key of the server.
     * @param privateKey    Private key of the server.
     */
    public void signBlock(PrivateKey privateKey) { // sign the block with the private key of the server
        String data = blockNumber + previousBlockHash + transactions.toString();
        Signature signature = null;
        try {
            signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes());
            this.blockSignature = signature.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that verifies the signature of the block.
     * @param publicKey Public key of the server.
     * @return  True if the signature is valid, false otherwise.
     */
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
        return "Block [blockHash=" + blockHash + ", blockNumber=" + blockNumber + ", previousBlockHash="
                + previousBlockHash + ", transactions=" + transactions + ", blockSignature=" + blockSignature + "]";
    }
}