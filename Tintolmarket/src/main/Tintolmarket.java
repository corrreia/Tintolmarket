package main;

import java.io.*;
import java.net.*;

import exceptions.IncorrectParametersClientException;
import handlers.OperationMenu;

public class Tintolmarket {

    private static final int DEFAULT_PORT = 12345;

    public static void main(String[] args) throws IOException {
        System.out.println("Tintolmarket Client v0.1");
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        try {
            String clientID, password;

            if (args.length < 2 || args.length > 3) {
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

                    clientID = args[1];
                    // parse password
                    if (args.length > 2) {
                        password = args[2];
                    } else {
                        System.out.print("No password specified. Please enter password: ");
                        password = stdIn.readLine();
                    }

                    startClient(serverAddress, port, clientID, password);
                }
            }

        } catch (IncorrectParametersClientException e) {
            System.err.println(e);
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void startClient(String serverAddress, int port, String clientID, String password) throws Exception {
        Socket socket = new Socket(serverAddress, port);
        ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());

        outStream.writeObject(clientID);
        outStream.writeObject(password);

        try {

            int flagFromServer = inStream.readInt();
            if (flagFromServer == 1) { // user registered
                System.out.println("Logging in...\n");
                boolean loginSuccessful = (Boolean) inStream.readObject();
                if (loginSuccessful) {
                    System.out.println("Welcome back " + clientID + " :)\n");
                } else {
                    System.out.println("Failed to login" + clientID + " :(\n");
                    System.exit(1);
                }
            } else {
                System.out.println("Registering new user...\n");
                boolean registerSuccessful = (Boolean) inStream.readObject();
                if (registerSuccessful) {
                    startClient(serverAddress, port, clientID, password); // login after registering
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        OperationMenu menu = new OperationMenu(outStream, inStream, clientID);
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        menu.receiveOperation(stdIn);

        outStream.close();
        inStream.close();

        socket.close();
    }
}