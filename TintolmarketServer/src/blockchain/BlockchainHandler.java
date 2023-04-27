package blockchain;

import java.io.*;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class BlockchainHandler {
    private static final String BLOCKCHAIN_FOLDER = null;
    private static final long MAX_TRANSACTIONS = 5;
    private int transactionIdCounter = 0;

    private ArrayList<Block> blockchain;
    private int transactionsInBlock;
    private long blockCount;
    private String previousBlockHash;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public BlockchainHandler(PrivateKey privateKey, PublicKey publicKey) {
        blockchain = new ArrayList<>();
        transactionsInBlock = 0;
        blockCount = 0;
        previousBlockHash = "00000000000000000000000000000000"; // 32 zeros (256 bits)
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public void loadBlockchain() {
        // check if blockchain folder exists
        File blockchainFolder = new File(BLOCKCHAIN_FOLDER);
        if (!blockchainFolder.exists() || !blockchainFolder.isDirectory()) {
            return;
        }

        // get all the block files and sort them by name
        File[] blockFiles = blockchainFolder.listFiles();
        Arrays.sort(blockFiles, Comparator.comparing(File::getName));

        // check if there are any block files
        if (blockFiles.length == 0) {
            return;
        }

        // load each block file
        for (int i = 0; i < blockFiles.length; i++) {
            // read the block file and deserialize it
            Block block = null;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(blockFiles[i]))) {
                block = (Block) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }

            // add the block to the blockchain
            blockchain.add(block);
            blockCount++;
            transactionsInBlock = block.getTransactions().size();
            previousBlockHash = block.getBlockHash();
        }
    }

    private void addBlockToBlockchain(Block block) {
        blockchain.add(block);
        blockCount++;
        transactionsInBlock = 0;
        previousBlockHash = block.calculateBlockHash();
        writeBlockToFile(block);
    }

    private void writeBlockToFile(Block block) {
        try {
            File file = new File("block_" + blockCount + ".blk");
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(block);
            objectOut.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyBlockchainIntegrity() {
        // check if blockchain folder exists
        File blockchainFolder = new File(BLOCKCHAIN_FOLDER);
        if (!blockchainFolder.exists() || !blockchainFolder.isDirectory()) {
            return false;
        }

        // get all the block files and sort them by name
        File[] blockFiles = blockchainFolder.listFiles();
        Arrays.sort(blockFiles, Comparator.comparing(File::getName));

        // check if there are any block files
        if (blockFiles.length == 0) {
            return false;
        }

        // verify each block file
        for (int i = 0; i < blockFiles.length; i++) {
            // read the block file and deserialize it
            Block block = null;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(blockFiles[i]))) {
                block = (Block) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            // verify the block hash
            if (!block.calculateBlockHash().equals(block.getBlockHash())) {
                return false;
            }

            // verify the block signature
            if (!block.verifyBlockSignature(publicKey)) {
                return false;
            }

        }

        // all blocks are valid
        return true;

    }

    public void addTransaction(String wineId, int units, double unitPrice, String ownerId, PrivateKey userPrivateKey)
            throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        // sign the transaction data
        Transaction transaction = new Transaction(transactionIdCounter++, wineId, units, unitPrice, ownerId);

        transaction.sign(userPrivateKey);

        if (transactionsInBlock < MAX_TRANSACTIONS) {
            // add the transaction to the current block
            blockchain.get(blockchain.size() - 1).addTransaction(transaction);
            transactionsInBlock++;
        } else {
            // create a new block and add the transaction to it
            Block block = new Block(blockCount, previousBlockHash);
            block.addTransaction(transaction);
            block.signBlock(privateKey);
            addBlockToBlockchain(block);
        }
    }
}
