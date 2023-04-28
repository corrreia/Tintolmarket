package security.sslserverconnection;

import java.io.IOException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
/**
 * Class that handles the SSL server connection.
 * 
 * 
 * @author Tomás Correia | fc57102
 * @author Miguel Pato | fc56372
 * @author João Vieira | fc45677
 */
public class SSLServerConnection {

    /**
	 * Method that gets the server SSL socket.
	 * @param port	Port of the server.
	 * @param keyStoreName	Name of the keystore.
	 * @param keyStorePassword	Password of the keystore.
	 * @return	The server SSL socket.
	 * @throws IOException
	 */
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
