package objects;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class Transaction implements Serializable {
    private Type type;
    private String wineId;
    private int units;
    private double unitPrice;
    private String ownerId;
    private String signature;

    public enum Type {
        BUY,
        SELL
    }

    public Transaction(Type type, String wineId, int units, double unitPrice, String ownerId) {
        this.type = type;
        this.wineId = wineId;
        this.units = units;
        this.unitPrice = unitPrice;
        this.ownerId = ownerId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getWineId() {
        return wineId;
    }

    public void setWineId(String wineId) {
        this.wineId = wineId;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void sign(PrivateKey privateKey) {
        try {
            Signature rsa = Signature.getInstance("SHA256withRSA");
            rsa.initSign(privateKey);
            rsa.update(this.toString().getBytes(StandardCharsets.UTF_8));
            this.signature = rsa.sign().toString();
        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException e) {
            System.out.println("Error signing transaction: " + e.getMessage());
        }
    }

    public boolean verify(PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initVerify(publicKey);
        rsa.update(this.toString().getBytes(StandardCharsets.UTF_8));
        return rsa.verify(this.signature.getBytes(StandardCharsets.UTF_8));
    }

    public boolean verifyTransactionSignature(PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(this.toString().getBytes());
            return signature.verify(this.signature.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return "Transaction [type=" + type + ", wineId=" + wineId + ", units=" + units + ", unitPrice="
                + unitPrice + ", ownerId=" + ownerId + ", signature=" + signature + "]";
    }
}
