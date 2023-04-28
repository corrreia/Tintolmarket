package ciphers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class that handles the cipher modes.
 * 
 * @author Tomás Correia | fc57102
 * @author Miguel Pato | fc56372
 * @author João Vieira | fc45677
 */
public class CipherModes {

    private static final String PARAMS = "cipher.params";
    static final String CIPHER_KEY = "cipher.key";

    private static final String ALGORITHM = "PBEWithHmacSHA256AndAES_128";

    /**
     * Method that encrypts a file.
     * @param inputFile    File to encrypt.
     * @param password    Password to encrypt the file.
     * @throws Exception
     */
    public static void encrypt(File inputFile, String password) throws Exception {
        File f = new File(inputFile.getAbsolutePath());
        if (!f.exists()) {
            f.createNewFile();
        }

        // genrateKey
        byte[] salt = { (byte) 0xc9, (byte) 0x36, (byte) 0x78, (byte) 0x99, (byte) 0x52, (byte) 0x3e,
                (byte) 0xea,
                (byte) 0xf2 };
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 20); // pass, salt, iterations
        SecretKeyFactory kf = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey key = kf.generateSecret(keySpec);
        // encrypt

        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);

        FileInputStream fis = new FileInputStream(inputFile);
        FileOutputStream fos = new FileOutputStream( "users.cif");
        CipherOutputStream cos = new CipherOutputStream(fos, c);

        byte[] b = new byte[16];
        int i = fis.read(b);
        while (i != -1) {
            cos.write(b, 0, i);
            i = fis.read(b);
        }
        byte[] keyEncoded = key.getEncoded();
        FileOutputStream kos = new FileOutputStream(CIPHER_KEY);
        ObjectOutputStream oos = new ObjectOutputStream(kos);
        oos.writeObject(keyEncoded);
        kos = new FileOutputStream(PARAMS);
        oos = new ObjectOutputStream(kos);
        oos.writeObject(c.getParameters().getEncoded());

        kos.close();
        oos.close();
        kos.close();
        fis.close();
        cos.close();
        Files.delete(Paths.get(inputFile.getAbsolutePath()));
    }
    
    /**
     * Method that decrypts a file.
     * @param inputFile   File to decrypt.
     * @throws Exception
     */
    public static void decrypt(File inputFile) throws Exception {
        File file1 = new File(CIPHER_KEY);
        File file2 = new File("users.cif");
        File file3 = new File(PARAMS);
        if (!file1.exists() || !file2.exists() || !file3.exists()) {
            File f = new File(inputFile.getAbsolutePath());
            f.createNewFile();
            return;
        }

        FileInputStream kos = new FileInputStream(CIPHER_KEY);
        ObjectInputStream oos = new ObjectInputStream(kos);
        byte[] keyEncoded2 = (byte[]) oos.readObject();
        oos.close();
        kos.close();

        SecretKeySpec keySpec2 = new SecretKeySpec(keyEncoded2, ALGORITHM);

        AlgorithmParameters p = AlgorithmParameters.getInstance(ALGORITHM);
        FileInputStream in = new FileInputStream(PARAMS);
        ObjectInputStream oin = new ObjectInputStream(in);
        byte[] param = (byte[]) oin.readObject();
        p.init(param);
        Cipher c = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
        c.init(Cipher.DECRYPT_MODE, keySpec2, p);
        FileOutputStream fis = new FileOutputStream(inputFile.getAbsolutePath());
        FileInputStream fos = new FileInputStream("users.cif");
        CipherInputStream cos = new CipherInputStream(fos, c);

        byte[] b = new byte[16];
        int i = cos.read(b);
        while (i != -1) {
            fis.write(b, 0, i);
            i = cos.read(b);
        }

        oin.close();
        cos.close();
        fis.close();
        fos.close();
    }
}
    

