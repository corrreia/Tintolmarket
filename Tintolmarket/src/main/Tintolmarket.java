package main;

import java.io.*;
import java.net.*;

import javax.net.ssl.SSLSocket;

import exceptions.IncorrectParametersClientException;
import handlers.OperationMenu;
import security.ClientSecurityManager;

/**
 * Class that represents the client of the Tintolmarket application.
 * 
 * @author Tomás Correia | fc57102
 * @author Miguel Pato   | fc56372
 * @author João Vieira   | fc45677
 */
public class Tintolmarket {

    private static final int DEFAULT_PORT = 12345;
    private static BufferedReader stdIn;

    public static void main(String[] args) throws IOException {
        System.out.println("Tintolmarket Client v1.0");
        stdIn = new BufferedReader(new InputStreamReader(System.in));

        try {
            String trustStore, keyStore, keyStorePassword, userID;

            if (args.length > 5 || args.length < 4) {
                stdIn.close();
                throw new IncorrectParametersClientException();
            } else {
                if (args[0].contains(":")) {

                    // expected usage: 127.0.0.1:12345 OR localhost:12345
                    String[] serverAddressSplit = args[0].split(":");
                    String serverAddress = serverAddressSplit[0];
                    int port = DEFAULT_PORT;

                    if (serverAddressSplit.length > 1) {
                        try {
                            port = Integer.parseInt(serverAddressSplit[1]);
                        } catch (NumberFormatException e) {
                            System.err.println("Cannot parse port number");
                            System.exit(1);
                        }
                    } else {
                        System.out.println("No port number specified. Defaulting to " + DEFAULT_PORT);
                    }

                    trustStore = args[1];
                    keyStore = args[2];
                    keyStorePassword = args[3];
                    userID = args[4];

                    // if(!trustStore.contains(".truststore")) {
					// 	trustStore += ".truststore";
					// }
					// if(!keyStore.contains(".keystore")) {
					// 	keyStore += ".keystore";
					// }
                    startClient(serverAddress, port, trustStore, keyStore, keyStorePassword, userID);
                }
            }

        } catch (IncorrectParametersClientException e) {
            System.out.println("Failed to start client: " + e.getMessage());
            System.out.println("Usage: Tintolmarket <server_address>:<port> <truststore> <keystore> <keystore_password> <user_id>");
            System.out.println("Example: Tintolmarket localhost:12345 truststore keystore password1234 user1");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void startClient(String serverAddress, int port, String trustStore, String keyStore, String keyStorePassword, String userID ) throws Exception {
        SSLSocket SSLsocket = ClientSecurityManager.connect(serverAddress, port, trustStore);

        ObjectOutputStream outStream = new ObjectOutputStream(SSLsocket.getOutputStream());
        ObjectInputStream inStream = new ObjectInputStream(SSLsocket.getInputStream());

        System.out.println("Connected to server " + serverAddress + ":" + port);

        ClientSecurityManager.authenticate(outStream, inStream, keyStore, keyStorePassword, userID);

        // Operation Menu
        OperationMenu menu = new OperationMenu(outStream, inStream, keyStore, keyStorePassword, userID);
        menu.showMenu();
        menu.receiveOperation(stdIn);

        stdIn.close();
        outStream.close();
        inStream.close();

        SSLsocket.close();
    }
}