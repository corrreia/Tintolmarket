package security;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLClientConnection {

    public static SSLSocket getClientSSLSocket(String serverAddress, int port, String trustStore) throws UnknownHostException, IOException {
        String trustStorePath = trustStore;
        System.out.println("Truststore path: " + trustStorePath);

        System.setProperty("javax.net.ssl.trustStore", trustStorePath);

        SocketFactory socketFactory = SSLSocketFactory.getDefault();
        SSLSocket SSLsocket = (SSLSocket) socketFactory.createSocket(serverAddress, port);

        return SSLsocket;
    }
}
