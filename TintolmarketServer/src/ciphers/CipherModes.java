package ciphers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.Policy.Parameters;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherModes {

    private static final String PARAMS = "cipher.params";

    public static void encrypt(File inputFile, String cipherKeyPath) throws Exception {
        // Read the secret key from the file
        byte[] keyBytes = Files.readAllBytes(Paths.get(cipherKeyPath));
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
    
        // Initialize the cipher with the secret key
        Cipher c = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
        c.init(Cipher.ENCRYPT_MODE, key);
    
        // Write the cipher parameters to a file for later use
        byte[] params = c.getParameters().getEncoded();
        FileOutputStream paramsOutputStream = new FileOutputStream(PARAMS);
        paramsOutputStream.write(params);
        paramsOutputStream.close();
    
        // Read the input file into a byte array
        byte[] inputBytes = Files.readAllBytes(inputFile.toPath());
    
        // Encrypt the input file and write the encrypted data to a new file
        byte[] encryptedBytes = c.doFinal(inputBytes);
        File encryptedFile = new File(inputFile.getParent(), inputFile.getName() + ".enc");
        FileOutputStream outputStream = new FileOutputStream(encryptedFile);
        outputStream.write(encryptedBytes);
        outputStream.close();
    }
    
    
    public static void decrypt(File inputFile, String cipherKeyPath) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(cipherKeyPath));
        SecretKey key = new SecretKeySpec(keyBytes, "AES");

        byte[] params = Files.readAllBytes(new File(PARAMS).toPath());
        AlgorithmParameters p = AlgorithmParameters.getInstance("PBEWithHmacSHA256AndAES_128");
        p.init(params);
        Cipher d = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
        d.init(Cipher.DECRYPT_MODE, key, p);

         
        // Decrypt the encrypted data and write it to a new file
        byte[] encryptedBytes = Files.readAllBytes(inputFile.toPath());
        byte[] decryptedBytes = d.doFinal(encryptedBytes);
        File decryptedFile = new File(inputFile.getParent(), inputFile.getName().replace(".enc", ""));
        FileOutputStream outputStream = new FileOutputStream(decryptedFile);
        outputStream.write(decryptedBytes);
        outputStream.close();

    }
}
    

