package security.sslserverconnection;

import java.io.IOException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class SSLServerConnection {

    
    public static SSLServerSocket getServerSSLSocket(int port, String keyStoreName, String keyStorePassword) throws IOException{
        String keyStorePath = keyStoreName;
		System.out.println("Keystore path: " + keyStorePath);
		
		System.setProperty("javax.net.ssl.keyStore", keyStorePath);
		System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
		
		ServerSocketFactory serverSocketFactory = SSLServerSocketFactory.getDefault();
		SSLServerSocket SSLserverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(port);
		
		return SSLserverSocket;
    }
}
