package security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.net.ssl.SSLServerSocket;

public class ServerSecurityManager {

    public static SSLServerSocket connect(int port, String keyStoreName, String keyStorePassword) throws IOException {
        return SSLServerConnection.getServerSSLSocket(port, keyStoreName, keyStorePassword);
    }

    public static void authenticate(ObjectOutputStream outStream, ObjectInputStream inStream, String userID) {
    }
}
