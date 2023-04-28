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

/**
 * Class that handles the blockchain.
 * 
 * 
 * @author Tomás Correia | fc57102
 * @author Miguel Pato | fc56372
 * @author João Vieira | fc45677
 */
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

    /**
     * Constructor for the BlockchainHandler class.
     * @param privateKey        Private key of the server.  
     * @param publicKey        Public key of the server.
     */
    private BlockchainHandler(PrivateKey privateKey, PublicKey publicKey) {
        this.blockchain = new ArrayList<>();
        blockchain.add(new Block(0, "00000000000000000000000000000000"));
        this.transactionsInBlock = 0;
        this.blockCount = 1;
        this.previousBlockHash = "";
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    /**
     * Method that starts the blockchain instance.
     * @param privateKey    Private key of the server.
     * @param publicKey       Public key of the server.
     * @return              The blockchain instance.
     */
    public static BlockchainHandler startInstance(PrivateKey privateKey, PublicKey publicKey) {
        if (instance == null) {
            instance = new BlockchainHandler(privateKey, publicKey);
            instance.loadBlockchain();
        }
        return instance;
    }

    /**
     * Method that returns the blockchain instance.
     * @return            The blockchain instance.
     */
    public static BlockchainHandler getInstance() {
        return instance;
    }

    /**
     * Method that loads the blockchain.
     */
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

        if (verifyBlockchainIntegrity())
            System.out.println("Blockchain integrity verified successfully.");
        else
            System.out.println("Blockchain integrity verification failed.");
    }

    /**
     * Method that lists the blockchain.
     * @return          The blockchain.
     */
    public String listBlockchain() {
        StringBuilder sb = new StringBuilder();
        for (Block block : blockchain) {
            sb.append("Block " + block.getBlockNumber() + "\n");
            for (Transaction transaction : block.getTransactions()) {
                sb.append(transaction.toString());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Method that adds a block to the blockchain.
     * @param block    The block to be added.
     */
    private void addBlockToBlockchain(Block block) {
        blockchain.add(block);
        blockCount++;
        transactionsInBlock = 0;
        writeBlockToFile(block);
    }

    /**
     * Method that writes a block to a file
     * @param block   The block to be written.
     */
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

    /**
     * Method that verifies the blockchain integrity.
     * @return        True if the blockchain is valid, false otherwise.
     */
    private boolean verifyBlockchainIntegrity() {
        // check if blockchain folder exist
        for (Block block : blockchain) {
            if (!(block.getBlockHash() == null)) {
                if (!block.verifyBlockSignature(publicKey))
                    return false;
            }
        }
        return true;
    }

    /**
     * Method that adds a transaction to the blockchain.
     * @param transaction   The transaction to be added.
     * @param signature    The signature of the transaction.
     * @param userPublicKey The public key of the user.
     */
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
