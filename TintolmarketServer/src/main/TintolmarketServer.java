package main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;

import handlers.UserHandler;
import security.ServerSecurityManager;
import exceptions.IncorrectArgumentsServerException;
/**
 * Class that represents the server of the Tintolmarket application.
 * 
 * @author Tomás Correia | fc57102
 * @author Miguel Pato   | fc56372
 * @author João Vieira   | fc45677
 */
public class TintolmarketServer {

	public static void main(String[] args) {
		System.out.println("------------------------=TintolmarketServer=------------------------");
		TintolmarketServer server = new TintolmarketServer();
		try {
			if (args.length < 3 || args.length > 4) {
				throw new IncorrectArgumentsServerException("Incorrect number of arguments.");
			}
			int port = args.length == 4 ? Integer.parseInt(args[0]) : 12345;
			String cipherPassword = args[args.length - 3];
			String keyStoreName = args[args.length - 2];
			String keyStorePassword = args[args.length - 1];
			
			if(!keyStoreName.contains(".keystore")) {
				keyStoreName += ".keystore";
			}
			
			server.startServer(port, cipherPassword, keyStoreName, keyStorePassword);
	
		} catch (NumberFormatException | IncorrectArgumentsServerException e) {
			System.out.println("Fail to Start Server." + e);
			System.out.println("Usage: TintolmarketServer [<port>] <password-cifra> <keystore> <password-keystore>\n");
		}
	}

	public void startServer(int port, String cipherPassword, String keyStoreName, String keyStorePassword) {
		SSLServerSocket SSLserverSocket = null;

		try {
			SSLserverSocket = ServerSecurityManager.connect(port, keyStoreName, keyStorePassword);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}

		while(true){
			try{
				System.out.println("Waiting for connections...");
				Socket socket = SSLserverSocket.accept();
				System.out.println("Connection established with " + socket.getInetAddress().getHostAddress());
				ServerThread newServerThread = new ServerThread(socket);
				newServerThread.start();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	// Threads utilizadas para comunicacao com os clientes
	class ServerThread extends Thread {

		private Socket socket = null;
		private UserHandler uH = null;

		ServerThread(Socket inSoc) {
			socket = inSoc;
		}

		public void run() {
			try {
				ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());


				Thread.sleep(100);
				try {
					String userID = (String) inStream.readObject();
					System.out.println("userID received\n");

					ServerSecurityManager.authenticate(outStream, inStream, userID);

				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}

				if (userID != null) {
					uH = UserHandler.authenticate(userID, inStream, outStream);
					if (uH != null) {
						uH.handleOps();
					}

				} else {
					System.out.println("Error: userID or password is null");
					outStream.writeObject(false);
				}

				outStream.close();
				inStream.close();

				socket.close();

			} catch (IOException | InterruptedException e) {
				System.out.println("Connection closed: " + e.getMessage());
			} finally {
				try {
					if (socket != null) {
						socket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}