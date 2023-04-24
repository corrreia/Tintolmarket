package security;

import java.io.IOException;

import javax.net.ssl.SSLServerSocket;

public class ServerSecurityManager {

    public static SSLServerSocket connect(int port, String keyStoreName, String keyStorePassword) throws IOException {
        return SSLServerConnection.getServerSSLSocket(port, keyStoreName, keyStorePassword);
    }
}
