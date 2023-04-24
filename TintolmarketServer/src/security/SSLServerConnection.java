package security;

import java.io.File;
import java.io.IOException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class SSLServerConnection {

    static final String KEY_STORE_PATH = "security" + File.separator;
    
    public static SSLServerSocket getServerSSLSocket(int port, String keyStoreName, String keyStorePassword) throws IOException{
        String keyStorePath = KEY_STORE_PATH + keyStoreName;
		
		System.setProperty("javax.net.ssl.keyStore", keyStorePath);
		System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
		
		ServerSocketFactory serverSocketFactory = SSLServerSocketFactory.getDefault();
		SSLServerSocket SSLserverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(port);
		
		return SSLserverSocket;
    }
}
