package security;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLSocket;

import security.sslclientconnection.SSLClientConnection;

public class ClientSecurityManager {

    private final static String CLIENT_KEYS = "Clients" + File.separator;

    private static final String KEY_STORE_TYPE = "PKCS12";

    private static PrivateKey serverPrivateKey;
    private static PublicKey serverPublicKey;

    public static PrivateKey getPrivateKey() {
        return serverPrivateKey;
    }

    public static PublicKey getPublicKey() {
        return serverPublicKey;
    }

    public static SSLSocket connect(String serverAddress, int port, String trustStore)
            throws UnknownHostException, IOException {
        return SSLClientConnection.getClientSSLSocket(serverAddress, port, trustStore);
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

    public static void authenticate(ObjectOutputStream outStream, ObjectInputStream inStream, String keyStore,
            String keyStorePassword, String userID)
            throws IOException, ClassNotFoundException, InvalidKeyException, UnrecoverableKeyException,
            NoSuchAlgorithmException, SignatureException, KeyStoreException, CertificateException {

        outStream.writeObject(userID);
        outStream.flush();

        // get servers response
        long nonceFromServer = inStream.readLong();
        int flag = inStream.readInt(); // 1 for registered user, 0 for new user

        if (flag == 1) {
            System.out.println("User already registered. Logging in...");

            // send signed nonce to server
            byte[] signedNonce = signNonce(nonceFromServer, keyStore, keyStorePassword, userID);

            outStream.writeObject(signedNonce);
            outStream.writeLong(nonceFromServer);
            outStream.flush();

            // get servers response
            String response = (String) inStream.readObject();
            if (response.equals("login")) {
                System.out.println("Login successful!\n" + "Welcome back " + userID + "!");

                try {
                    loadKeystore(keyStore, keyStorePassword, userID, keyStorePassword);
                } catch (Exception e) {
                    System.out.println("Error loading keystore");
                    e.printStackTrace();
                }

            } else {
                System.out.println("Login failed for user " + userID + "!");
                System.exit(-1);
            }
        } else {
            System.out.println("Registering...");

            // send signed nonce to server
            byte[] signedNonce = signNonce(nonceFromServer, keyStore, keyStorePassword, userID);

            outStream.writeObject(signedNonce);
            outStream.writeLong(nonceFromServer);

            String certificate = getCertificateFormat(userID);
            outStream.writeObject(CLIENT_KEYS + userID + File.separator + certificate);

            sendCertificateToServer(outStream, userID);
            outStream.flush();

            // get servers response
            String response = (String) inStream.readObject();
            if (response.equals("resgistered")) {
                System.out.println("Registration successful!\n" + "Welcome " + userID + "!");

                try {
                    loadKeystore(keyStore, keyStorePassword, userID, keyStorePassword);
                } catch (Exception e) {
                    System.out.println("Error loading keystore");
                    e.printStackTrace();
                }

            } else {
                System.out.println("Registration failed for user " + userID + "!");
                System.exit(-1);
            }
        }
    }

    private static void sendCertificateToServer(ObjectOutputStream outStream, String userID) throws IOException {
        String path = CLIENT_KEYS + userID + File.separator + getCertificateFormat(userID);
        File file = new File(path);

        InputStream is = new BufferedInputStream(new FileInputStream(file));
        String fileName = file.getName();
        int size = (int) file.length();

        outStream.writeObject(fileName);
        outStream.writeObject(size);

        byte[] buffer = new byte[2048];
        int bytesRead;

        while ((bytesRead = is.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        is.close();
        outStream.flush();
    }

    private static String getCertificateFormat(String userID) {
        return userID + ".cer";
    }

    private static byte[] signNonce(long nonceFromServer, String keyStore, String keyStorePassword, String userID)
            throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, UnrecoverableKeyException,
            KeyStoreException, CertificateException, IOException {
        String alias = userID;
        PrivateKey privKey = getPrivateKey(keyStore, keyStorePassword, alias);

        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(privKey);

        byte[] nonceBytes = Long.toString(nonceFromServer).getBytes();
        signature.update(nonceBytes);
        return signature.sign();
    }

    private static PrivateKey getPrivateKey(String keyStore, String keyStorePassword, String alias)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException,
            UnrecoverableKeyException {
        FileInputStream fis = new FileInputStream(keyStore);
        KeyStore ks = KeyStore.getInstance(KEY_STORE_TYPE);
        ks.load(fis, keyStorePassword.toCharArray());

        return (PrivateKey) ks.getKey(alias, keyStorePassword.toCharArray());
    }

}
