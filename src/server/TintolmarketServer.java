// TintolmarketServer <port>
// • <port> identifica o porto (TCP) para aceitar ligações de clientes. Por omissão o servidor
// deve usar o porto 12345.

import java.io.*;
import java.net.*;
import java.util.*;

public class TintolmarketServer {
    public static void main(String[] args) throws IOException {
        int port = 12345;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Cannot parse port number");
                System.exit(1);
            }
        } else {
            System.out.println("No port number specified. Defaulting to " + port);
        }
    
        System.out.println("Server listening on port " + port);
    
    }
}