import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

//Servidor myServer

public class TintolmarketServer {

	public static void main(String[] args) {
		System.out.println("--------------------------------=TintolmarketServer=--------------------------------");
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
                System.out.println("Server listening on port " + port);
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
			System.out.println("thread do server para cada cliente");
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
 			
				if (userID.length() != 0){
					outStream.writeObject(new Boolean(true));
				}
				else {
					outStream.writeObject(new Boolean(false));
				}

				outStream.close();
				inStream.close();
 			
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

        private boolean checkCredentials(String userID, String passwd) {
            return false;
        }
	}
}