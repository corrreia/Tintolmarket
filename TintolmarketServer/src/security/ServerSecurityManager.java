package security;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLServerSocket;

import handlers.BlockchainHandler;
import handlers.UserHandler;
import security.sslserverconnection.SSLServerConnection;

public class ServerSecurityManager {

    private final static String SECURITY_DIRECTORY = "security" + File.separator;
    public final static String CERTIFICATES_DIRECTORY = SECURITY_DIRECTORY + "certificates" + File.separator;

    private static PrivateKey serverPrivateKey;
    private static PublicKey serverPublicKey;

    public static SSLServerSocket connect(int port, String keyStoreName, String keyStorePassword) throws IOException {
        try {
            loadKeystore(keyStoreName, keyStorePassword, "server", keyStorePassword);
        } catch (Exception e) {
            System.out.println("Error loading keystore: " + e);
            System.exit(-1);
        }

        BlockchainHandler.startInstance(serverPrivateKey, serverPublicKey);

        return SSLServerConnection.getServerSSLSocket(port, keyStoreName, keyStorePassword);
    }

    public static void loadKeystore(String keystorePath, String keystorePassword, String alias, String keyPassword)
            throws Exception {
        FileInputStream fis = new FileInputStream(keystorePath);
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(fis, keystorePassword.toCharArray());

        Key key = keystore.getKey(alias, keyPassword.toCharArray());
        if (key instanceof PrivateKey) {
            serverPrivateKey = (PrivateKey) key;
        }

        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keystore.getEntry(alias,
                new KeyStore.PasswordProtection(keyPassword.toCharArray()));
        serverPublicKey = privateKeyEntry.getCertificate().getPublicKey();
    }

    private static long generateNonce() {
        SecureRandom random = new SecureRandom();
        return Math.abs(random.nextLong());
    }

    public static boolean verifyNonce(byte[] signedNonce, long nonceFromUser, String userID)
            throws NoSuchAlgorithmException, FileNotFoundException, CertificateException, InvalidKeyException,
            SignatureException {
        PublicKey pubK = getPublicKeyFromCertificate(userID);

        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(pubK);
        signature.update(Long.toString(nonceFromUser).getBytes());
        return signature.verify(signedNonce);
    }

    public static PublicKey getPublicKeyFromCertificate(String certificateName)
            throws CertificateException, FileNotFoundException {

        String certificatePath;

        if (certificateName.contains(".cer")) {
            certificatePath = "TintolmarketServer" + File.separator + CERTIFICATES_DIRECTORY + certificateName;
        } else {
            String userId = certificateName;
            certificatePath = "TintolmarketServer" + File.separator + CERTIFICATES_DIRECTORY + userId + ".cer";
        }

        FileInputStream fis = new FileInputStream(certificatePath);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
        Certificate certificate = certificateFactory.generateCertificate(fis);
        return certificate.getPublicKey();
    }

    public static int serverReceiveCertificate(ObjectInputStream in)
            throws ClassNotFoundException, IOException {

        String fileName = (String) in.readObject();
        int fileSize = (int) in.readObject();

        String dest = "TintolmarketServer" + File.separator + CERTIFICATES_DIRECTORY + fileName;

        File file = new File(dest);
        if (file.exists()) {
            return 0;
        }

        OutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

        byte[] buffer = new byte[2048];
        int nbytes = 0;
        int temp = fileSize;

        while (temp > 0) {
            nbytes = in.read(buffer, 0, temp > 2048 ? 2048 : temp);
            bos.write(buffer, 0, nbytes);
            temp -= nbytes;
        }

        bos.close();
        file.createNewFile();

        return 1;
    }

    public static void authenticate(ObjectOutputStream outStream, ObjectInputStream inStream, String userID)
            throws IOException, InterruptedException, ClassNotFoundException, InvalidKeyException,
            NoSuchAlgorithmException, CertificateException, SignatureException {
        long nonce = generateNonce();

        UserHandler userHandler = new UserHandler();

        if (userHandler.isRegistered(userID)) {
            outStream.writeLong(nonce);
            outStream.writeInt(1);
            outStream.flush();

            Thread.sleep(100);
            byte[] signedNonce = (byte[]) inStream.readObject();
            long nonceFromUser = inStream.readLong();

            if (nonce == nonceFromUser) {
                if (verifyNonce(signedNonce, nonceFromUser, userID)) {
                    outStream.writeObject("login");
                    outStream.flush();
                    System.out.println("User " + userID + " authenticated\n");
                } else {
                    outStream.writeObject("loginError");
                    outStream.flush();
                    System.out.println("User " + userID + " authentication error\n");
                }
            }
        } else {
            outStream.writeLong(nonce);
            outStream.writeInt(0); // user not registered
            outStream.flush();

            Thread.sleep(100);
            byte[] signedNonce = (byte[]) inStream.readObject();
            long nonceFromUser = inStream.readLong();

            String cert = (String) inStream.readObject();
            int i = serverReceiveCertificate(inStream);
            System.out.println("Certificate received from user " + userID + "\n");

            if (nonce == nonceFromUser) {
                if (verifyNonce(signedNonce, nonceFromUser, userID)) {
                    userHandler.registerUser(userID);

                    outStream.writeObject("resgistered");
                    outStream.flush();
                    System.out.println("User " + userID + " registered\n");
                } else {
                    outStream.writeObject("registrationError");
                    outStream.flush();
                    System.out.println("User " + userID + " registration error\n");
                }
            }
        }
    }
}
