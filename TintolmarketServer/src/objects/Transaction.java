package objects;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

/**
 * Class that represents a transaction.
 * 
 * @author Tomás Correia | fc57102
 * @author Miguel Pato | fc56372
 * @author João Vieira | fc45677
 */
public class Transaction implements Serializable {
    private Type type;
    private String wineId;
    private int units;
    private double unitPrice;
    private String ownerId;

    /**
     * Enum that represents the type of the transaction.
     */
    public enum Type {
        BUY,
        SELL
    }

    /**
     * Constructor for the Transaction class.
     * @param type          Type of the transaction.
     * @param wineId        Id of the wine.
     * @param units         Units of the wine.
     * @param unitPrice     Price of the wine.
     * @param ownerId       Id of the owner.
     */
    public Transaction(Type type, String wineId, int units, double unitPrice, String ownerId) {
        this.type = type;
        this.wineId = wineId;
        this.units = units;
        this.unitPrice = unitPrice;
        this.ownerId = ownerId;
    }

    /**
     * Method that gets the type of the transaction.
     * @return  The type of the transaction.
     */
    public Type getType() {
        return type;
    }

    /**
     * Method that gets the id of the wine.
     * @return  The id of the wine.
     */
    public String getWineId() {
        return wineId;
    }

    /**
     *  Method that gets the units of the wine.
     * @return  The units of the wine.
     */
    public int getUnits() {
        return units;
    }

    /**
     * Method that gets the price of the wine.
     * @return  The price of the wine.
     */
    public double getUnitPrice() {
        return unitPrice;
    }

    /**
     * Method that gets the id of the owner.
     * @return
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * Method that signs the transaction.
     * @param privateKey    Private key of the owner.
     * @return  The signature of the transaction.
     */
    public String sign(PrivateKey privateKey) {
        String sign = null;
        try {
            Signature rsa = Signature.getInstance("SHA256withRSA");
            rsa.initSign(privateKey);
            rsa.update(this.toString().getBytes(StandardCharsets.UTF_8));
            byte[] signatureBytes = rsa.sign();
            sign = Base64.getEncoder().encodeToString(signatureBytes);
        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException e) {
            System.out.println("Error signing transaction: " + e.getMessage());
        }
        return sign;
    }

    /**
     * Method that verifies the signature of the transaction.
     * @param publicKey    Public key of the owner.
     * @param signatureText   Signature of the transaction.
     * @return  True if the signature is valid, false otherwise.
     */
    public boolean verifyTransactionSignature(PublicKey publicKey, String signatureText) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(this.toString().getBytes());
            byte[] signatureBytes = Base64.getDecoder().decode(signatureText);
            return signature.verify(signatureBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return "Transaction [type=" + type + ", wineId=" + wineId + ", units=" + units + ", unitPrice="
                + unitPrice + ", ownerId=" + ownerId + "]";
    }
}
