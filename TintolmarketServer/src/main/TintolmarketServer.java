package main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import handlers.UserHandler;
import exceptions.TooManyArgumentsServerException;

//Servidor myServer\

public class TintolmarketServer {

	public static void main(String[] args) {
		System.out.println("------------------------=TintolmarketServer=------------------------");
		TintolmarketServer server = new TintolmarketServer();
		try {
			if (args.length > 1) {
				throw new TooManyArgumentsServerException("Too many arguments");
			}

			if (args.length == 0) {
				System.out.println("No port specified. Using default port 12345");
				server.startServer(12345);
			} else {
				int port = Integer.parseInt(args[0]);
				System.out.println("Using port " + port);
				server.startServer(port);
			}

		} catch (NumberFormatException | TooManyArgumentsServerException e) {
			System.out.println("Fail to Start Server." + e);
			System.out.println("Usage: TintolmarketServer <port>\n");
		}
	}

	public void startServer(int port) {
		ServerSocket sSoc = null;

		try {
			sSoc = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}

		while (true) {
			try {
				System.out.println("Waiting for connections...");
				Socket inSoc = sSoc.accept();
				System.out.println("Connection established with " + inSoc.getInetAddress().getHostAddress());
				ServerThread newServerThread = new ServerThread(inSoc);
				newServerThread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// sSoc.close();
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

				String userID = null;
				String passwd = null;

				Thread.sleep(100);

				try {
					userID = (String) inStream.readObject();
					passwd = (String) inStream.readObject();
					System.out.println("userID and Password received\n");
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}

				if (userID != null && passwd != null) {
					uH = UserHandler.authenticate(userID, passwd, inStream, outStream);
					if (uH != null) 
						uH.handleOps();
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