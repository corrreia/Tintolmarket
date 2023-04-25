package security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Random;

import javax.net.ssl.SSLServerSocket;

import handlers.UserHandler;

public class ServerSecurityManager {

    private final static String SECURITY_DIR = "security" + File.separator;
    public final static String CERTIFICATES_DIR = SECURITY_DIR + "certificates" + File.separator;

    private final static String CERTIFICATE_FORMAT = "RSApub.cer";

    private static long generateNonce() {
        SecureRandom random = new SecureRandom();
        return Math.abs(random.nextLong());
    }

    public static SSLServerSocket connect(int port, String keyStoreName, String keyStorePassword) throws IOException {
        return SSLServerConnection.getServerSSLSocket(port, keyStoreName, keyStorePassword);
    }

    public static boolean verifyNonce(byte[] signedNonce, long nonceFromUser, String userID) throws NoSuchAlgorithmException, FileNotFoundException, CertificateException, InvalidKeyException, SignatureException {
        PublicKey pubK = getPubKFromCertificate(userID);
        
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(pubK);
        signature.update(Long.toString(nonceFromUser).getBytes());
        return signature.verify(signedNonce);
    }

    public static PublicKey getPubKFromCertificate(String userID) throws FileNotFoundException, CertificateException {
        String path;

        if(userID.contains(".cer")){
            path = CERTIFICATES_DIR + userID;
        } else {
            path = CERTIFICATES_DIR + userID + File.separator + CERTIFICATE_FORMAT;
        }

        FileInputStream fis = new FileInputStream(path);
        CertificateFactory cf = CertificateFactory.getInstance("X509");
        Certificate cert = cf.generateCertificate(fis);
        return cert.getPublicKey();
    }

    public static void authenticate(ObjectOutputStream outStream, ObjectInputStream inStream, String userID) throws IOException, InterruptedException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, CertificateException, SignatureException {
        long nonce = generateNonce();

        UserHandler userHandler = new UserHandler(userID, inStream, outStream);

        if(userHandler.isRegistered(userID)){
            outStream.writeLong(nonce);
            outStream.writeInt(1); // user registered
            outStream.flush();

            Thread.sleep(100);
            byte[] signedNonce = (byte[]) inStream.readObject();
            long nonceFromUser = inStream.readLong();

            if(nonce == nonceFromUser){
                if(verifyNonce(signedNonce, nonceFromUser, userID)){
                    outStream.writeObject(true);
                    outStream.flush();
                    System.out.println("User " + userID + "authenticated\n");
                } else {
                    outStream.writeObject(false);
                    outStream.flush();
                    System.out.println("User " + userID + "authentication error\n");
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
            receiveCertificate(inStream);
        }
    }
}
