package blockchain;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

public class Transaction {
    private long id;
    private String wineId;
    private int units;
    private double unitPrice;
    private String ownerId;
    private String buyerId;
    private byte[] signature;

    public Transaction(long id, String wineId, int units, double unitPrice, String ownerId) {
        this.id = id;
        this.wineId = wineId;
        this.units = units;
        this.unitPrice = unitPrice;
        this.ownerId = ownerId;
    }

    public long getTransactionId() {
        return id;
    }

    public String getWineId() {
        return wineId;
    }

    public int getUnits() {
        return units;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void sign(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initSign(privateKey);
        rsa.update(this.toString().getBytes(StandardCharsets.UTF_8));
        this.signature = rsa.sign();
    }

    public boolean verify(PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initVerify(publicKey);
        rsa.update(this.toString().getBytes(StandardCharsets.UTF_8));
        return rsa.verify(this.signature);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", wineId='" + wineId + '\'' +
                ", units=" + units +
                ", unitPrice=" + unitPrice +
                ", ownerId='" + ownerId + '\'' +
                ", buyerId='" + buyerId + '\'' +
                ", signature=" + Base64.getEncoder().encodeToString(signature) +
                '}';
    }
}
