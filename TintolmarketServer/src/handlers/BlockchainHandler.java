package handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import objects.Block;
import objects.Transaction;

public class BlockchainHandler {
    private static BlockchainHandler instance = null;

    private static final String BLOCKCHAIN_FOLDER = "blockchain";
    private static final long MAX_TRANSACTIONS = 5;

    private ArrayList<Block> blockchain;
    private int transactionsInBlock;
    private long blockCount;
    private String previousBlockHash;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    private BlockchainHandler(PrivateKey privateKey, PublicKey publicKey) {
        this.blockchain = new ArrayList<>();
        blockchain.add(new Block(0, "00000000000000000000000000000000"));
        this.transactionsInBlock = 0;
        this.blockCount = 1;
        this.previousBlockHash = "";
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public static BlockchainHandler startInstance(PrivateKey privateKey, PublicKey publicKey) {
        if (instance == null) {
            instance = new BlockchainHandler(privateKey, publicKey);
            instance.loadBlockchain();
        }
        return instance;
    }

    public static BlockchainHandler getInstance() {
        return instance;
    }

    private void loadBlockchain() {
        // check if blockchain folder exists
        File blockchainFolder = new File(BLOCKCHAIN_FOLDER);
        if (!blockchainFolder.exists() || !blockchainFolder.isDirectory()) {
            System.out.println("Blockchain folder does not exist.");
            return;
        }

        // get all the block files and sort them by name
        File[] blockFiles = blockchainFolder.listFiles();
        Arrays.sort(blockFiles, Comparator.comparing(File::getName));

        // check if there are any block files
        if (blockFiles.length == 0) {
            System.out.println("Blockchain folder exists but is empty.");
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
            previousBlockHash = blockchain.get(blockchain.size() - 2).calculateBlockHash();
        }

        System.out.println("Blockchain loaded successfully.");
        System.out.println("Blockchain size: " + blockchain.size());
        System.out.println("Block count: " + blockCount);
        System.out.println("Transactions in block: " + transactionsInBlock);
        System.out.println("Previous block hash: " + previousBlockHash);

        System.out.println("Blockchain: " + listBlockchain());
    }

    public String listBlockchain() {
        StringBuilder sb = new StringBuilder();
        for (Block block : blockchain) {
            for (Transaction transaction : block.getTransactions()) {
                sb.append(transaction.toString());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private void addBlockToBlockchain(Block block) {
        blockchain.add(block);
        blockCount++;
        transactionsInBlock = 0;
        writeBlockToFile(block);
    }

    private void writeBlockToFile(Block block) {
        try {
            File file = new File(BLOCKCHAIN_FOLDER + File.separator + "block_" + blockCount + ".blk");
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

    public void addTransaction(Transaction transaction, String signature, PublicKey userPublicKey) {
        // Verify transaction signature
        if (!transaction.verifyTransactionSignature(userPublicKey, signature)) {
            System.out.println("Transaction signature is not valid");
            return;
        }

        // Create a new block if the current block is full
        if (transactionsInBlock < MAX_TRANSACTIONS) {
            transactionsInBlock++;
            blockchain.get(((int) blockCount) - 1).addTransaction(transaction);
            writeBlockToFile(blockchain.get(((int) blockCount) - 1));
            System.out.println("Transaction added to block " + blockCount);
        } else {
            blockchain.get(((int) blockCount) - 1).signBlock(privateKey);
            previousBlockHash = blockchain.get(((int) blockCount) - 1).calculateBlockHash();
            System.out.println("Block " + blockCount + " signed , obtained hash " + previousBlockHash);

            addBlockToBlockchain(new Block(blockCount, previousBlockHash));
            System.out.println("Block " + blockCount + " created");
            addTransaction(transaction, signature, userPublicKey);
        }
    }

}
