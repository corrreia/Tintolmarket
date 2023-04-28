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

public class Transaction implements Serializable {
    private Type type;
    private String wineId;
    private int units;
    private double unitPrice;
    private String ownerId;

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
