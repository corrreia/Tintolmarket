package security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import javax.net.ssl.SSLServerSocket;

import handlers.UserHandler;
import security.sslserverconnection.SSLServerConnection;

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

    public static int receiveCertificate(ObjectInputStream inStream) throws IOException, ClassNotFoundException {
        String fileName = (String) inStream.readObject();
		int size = (int) inStream.readObject();

        String dir = CERTIFICATES_DIR + fileName;

        File file = new File(dir);
        if(file.exists()){
            return 0;
        }

        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));

        byte[] buffer = new byte[2048];
        int bytesRead = 0;
        int totalBytes = size;

        while(totalBytes > 0){
            bytesRead = inStream.read(buffer, 0, Math.min(buffer.length, totalBytes));
            out.write(buffer, 0, bytesRead);
            totalBytes -= bytesRead;
        }

        out.close();
        file.createNewFile();
        return 1;
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
                    outStream.writeObject("login");
                    outStream.flush();
                    System.out.println("User " + userID + "authenticated\n");
                } else {
                    outStream.writeObject("loginError");
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
            int i = receiveCertificate(inStream);

            if(nonce == nonceFromUser){
                if(verifyNonce(signedNonce, nonceFromUser, userID)){
                    userHandler.registerUser(userID, cert);

                    outStream.writeObject("resgistered");
                    outStream.flush();
                    System.out.println("User " + userID + "registered\n");
                } else {
                    outStream.writeObject("registrationError");
                    outStream.flush();
                    System.out.println("User " + userID + "registration error\n");
                }
            }
        }
    }
}
