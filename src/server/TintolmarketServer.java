package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//Servidor myServer

public class TintolmarketServer {
    private static final String FILENAME = "credentials.txt";

	public static void main(String[] args) {
		System.out.println("------------------------=TintolmarketServer=------------------------");
		TintolmarketServer server = new TintolmarketServer();
        if(args.length > 0){
            try {
                int port = Integer.parseInt(args[0]);
                server.startServer(port);
            } catch (NumberFormatException e) {
                System.err.println("Cannot parse port number");
                System.exit(1);
            }
        } else {
            System.out.println("No port number specified. Defaulting to 12345");
            server.startServer(12345);
        }
	}

	public void startServer (int port){
		ServerSocket sSoc = null;
        
		try {
			sSoc = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
         
		while(true) {
			try {
                System.out.println("Server listening on port " + port + "...");
				Socket inSoc = sSoc.accept();
				ServerThread newServerThread = new ServerThread(inSoc);
				newServerThread.start();
		    }
		    catch (IOException e) {
		        e.printStackTrace();
		    }
		    
		}
		//sSoc.close();
	}


	//Threads utilizadas para comunicacao com os clientes
	class ServerThread extends Thread {

		private Socket socket = null;

		ServerThread(Socket inSoc) {
			socket = inSoc;
			System.out.println("Thread created for new client");
		}
 
		public void run(){
			try {
				ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

				String userID = null;
				String passwd = null;
			
				try {
					userID = (String)inStream.readObject();
					passwd = (String)inStream.readObject();
					System.out.println("userID and Password received\n");
				}catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				
				boolean isAuthenticated = checkCredentials(userID, passwd);

                if (isAuthenticated){
                    System.out.println("Recorrent user" + userID + " authenticated!\n");
                    outStream.writeObject(isAuthenticated);
                } else {
                    System.out.println("User not in the system!");
                    if(userID.length() != 0 && passwd.length() != 0){
                        System.out.println("New user: " + userID + " with password: " + passwd);
                        System.out.println("New user registered!\n");
                        isAuthenticated = true;
                        writeUsers(userID, passwd);
                        outStream.writeObject(true);    
                    } else {
                        outStream.writeObject(false);
                    }
                }

				outStream.close();
				inStream.close();
 			
				socket.close();

			} catch (IOException e) {
				System.out.println("Connection closed.");
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

        private void writeUsers(String userID, String passwd) {
            try {
				String fileP = FILENAME;
				File file = new File(fileP);
				FileOutputStream fs = new FileOutputStream(file, true);
				fs.write((userID + ":" + passwd).getBytes());
				fs.write(System.lineSeparator().getBytes());
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }

        private boolean checkCredentials(String userID, String passwd) throws IOException {
            File file = new File(FILENAME);
			if (file.exists()) {
				FileInputStream fin = new FileInputStream(file);
				InputStream input = new BufferedInputStream(fin);
				try {
					byte[] buffer = new byte[1024];
					StringBuilder sb = new StringBuilder();
					int bytesRead;
					while ((bytesRead = input.read(buffer)) != -1) {
						sb.append(new String(buffer, 0, bytesRead));
					}
					String[] lines = sb.toString().split("\\r?\\n");
					for (String line : lines) {
						String[] parts = line.split(":");
						if (parts.length == 2 && parts[0].equals(userID) && parts[1].equals(passwd)) {
							System.out.println("User is in the system, logging in...");
							return true;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (input != null) {
						input.close();
					}
				}
			}
			return false;
        }
	}
}