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
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLSocket;

public class ClientSecurityManager {

    private final static String SECURITY_DIRECTORY = "security" + File.separator;
    private final static String PUB_KEYS_DIRECTORY = SECURITY_DIRECTORY + "PubKeys" + File.separator;

    private static final String KEY_STORE_TYPE = "JKS";

    public static SSLSocket connect(String serverAddress, int port, String trustStore) throws UnknownHostException, IOException {
        return SSLClientConnection.getClientSSLSocket(serverAddress, port, trustStore);
    }

    public static void authenticate(ObjectOutputStream outStream, ObjectInputStream inStream, String keyStore,
            String keyStorePassword, String userID) throws IOException, ClassNotFoundException, InvalidKeyException, UnrecoverableKeyException, NoSuchAlgorithmException, SignatureException, KeyStoreException, CertificateException {
        
        outStream.writeObject(userID);
        outStream.flush();

        // get servers response
        long nonceFromServer = inStream.readLong();
        int flag = inStream.readInt(); // 1 for registered user, 0 for new user

        if(flag == 1){
            System.out.println("User already registered. Logging in...");

            // send signed nonce to server
            byte[] signedNonce = signNonce(nonceFromServer, keyStore, keyStorePassword, userID);

            outStream.writeObject(signedNonce);
            outStream.writeLong(nonceFromServer);
            outStream.flush();

            // get servers response
            String response = (String) inStream.readObject();
            if (response.equals("login")){
                System.out.println("Login successful!\n" + "Welcome back " + userID + "!");
            } else {
                System.out.println("Login failed for user " + userID + "!");
                System.exit(-1);
            }
        } else {
            System.out.println("User not registered. Registering new user...");

            // send signed nonce to server
            byte[] signedNonce = signNonce(nonceFromServer, keyStore, keyStorePassword, userID);

            outStream.writeObject(signedNonce);
            outStream.writeLong(nonceFromServer);

            String certificate = getCertificateFormat(userID);
            outStream.writeObject(certificate);

            sendCertificateToServer(outStream, userID);
            outStream.flush();

            // get servers response
            String response = (String) inStream.readObject();
            if (response.equals("resgistered")){
                System.out.println("Registration successful!\n" + "Welcome " + userID + "!");
            } else {
                System.out.println("Registration failed for user " + userID + "!");
                System.exit(-1);
            }
        }
    }

    private static void sendCertificateToServer(ObjectOutputStream outStream, String userID) throws IOException {
        String path = PUB_KEYS_DIRECTORY + userID + "RSApub.cer";
        File file = new File(path);

        InputStream is = new BufferedInputStream(new FileInputStream(file));
        String fileName = file.getName();
        int size = (int) file.length();

        outStream.writeObject(fileName);
        outStream.writeInt(size);

        byte[] buffer = new byte[2048];
        int bytesRead;

        while ((bytesRead = is.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        is.close();
        outStream.flush();
    }

    private static String getCertificateFormat(String userID) {
        return userID + "RSApub.cer";
    }

    private static byte[] signNonce(long nonceFromServer, String keyStore, String keyStorePassword, String userID) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, UnrecoverableKeyException, KeyStoreException, CertificateException, IOException {
        String alias = userID + "KeyRSA";
        PrivateKey privKey = getPrivateKey(keyStore, keyStorePassword, alias);

        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(privKey);

        byte[] nonceBytes = Long.toString(nonceFromServer).getBytes();
        signature.update(nonceBytes);

        return signature.sign();
    }

    private static PrivateKey getPrivateKey(String keyStore, String keyStorePassword, String alias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
        FileInputStream fis = new FileInputStream(SECURITY_DIRECTORY + keyStore);
        KeyStore ks = KeyStore.getInstance(KEY_STORE_TYPE);
        ks.load(fis, keyStorePassword.toCharArray());

        return (PrivateKey) ks.getKey(alias, keyStorePassword.toCharArray());
    }
    
}
