package client;

// Tintolmarket <serverAddress> <userID> [password]
// Em que:
// • <serverAddress> identifica o servidor. O formato de serverAddress é o seguinte:
// <IP/hostname>[:Port]. O endereço IP ou o hostname do servidor são obrigatórios e o porto
// é opcional. Por omissão, o cliente deve ligar-se ao porto 12345 do servidor.
// • <clientID> identifica o utilizador local.
// • [password] – password utilizada para autenticar o utilizador local. Caso a password não
// seja dada na linha de comando, deve ser pedida ao utilizador pela aplicação.

import java.io.*;
import java.net.*;
import java.util.*;

public class Tintolmarket {

    private static final int DEFAULT_PORT = 12345;

    public static void main(String[] args) throws IOException {
        String serverAddress = "localhost";
        int port = DEFAULT_PORT;
        String clientID;
        String password;
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        
        if (args.length < 2){
            System.out.println("Not enough arguments");
            System.exit(1);
        }
        
        //parse serverAddress
        String[] serverAddressSplit = args[0].split(":");
        serverAddress = serverAddressSplit[0];
        if (serverAddressSplit.length > 1){
            try {
                port = Integer.parseInt(serverAddressSplit[1]);
            } catch (NumberFormatException e) {
                System.err.println("Cannot parse port number");
                System.exit(1);
            }
        }else{
            System.out.println("No port number specified. Defaulting to " + DEFAULT_PORT);
        }

        Socket socket = new Socket(serverAddress, port);
        ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());

        //parse clientID
        clientID = args[1];

        //parse password
        if (args.length > 2){
            password = args[2];
        }else{
            System.out.println("No password specified. Please enter password:");
            password = stdIn.readLine();
        }

        outStream.writeObject(clientID);
        outStream.writeObject(password);


        try {
            boolean loginSuccessful = (Boolean) inStream.readObject();
            if (loginSuccessful){
                System.out.println("Login successful! :)\n");
            }else{
                System.out.println("Login failed! :(\n");
                System.exit(1);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //debug print
        System.out.println("Server address: " + serverAddress);
        System.out.println("Port: " + port);
        System.out.println("Client ID: " + clientID);
        System.out.println("Password: " + password);

        outStream.close();
        inStream.close();

        socket.close();
    }
}