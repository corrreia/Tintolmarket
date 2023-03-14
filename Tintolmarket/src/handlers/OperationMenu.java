package handlers;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class OperationMenu {
    
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private String clientID;
    private String password;
    private String operation;
    
    public OperationMenu(ObjectOutputStream outStream, ObjectInputStream inStream, String clientID, String password) {
        this.outStream = outStream;
        this.inStream = inStream;
        this.clientID = clientID;
        this.password = password;

        this.operation = null;
    }

    public void showMenu() {
        System.out.println("\n___________________________________________________\n"
				+ "**************************************************************\n"
				+ "*                       TintolMarket                          \n"
				+ "*_____________________________________________________________\n"
				+ "**************************************************************\n"
				+ "* add <wine> <image> or a <wine> <image>\n"
				+ "* sell <wine> <value> <quantity> or s <wine> <value> <quantity>\n"
				+ "* view <wine> or v <wine>\n"
				+ "* buy <wine> <seller> <quantity> or b <wine> <seller> <quantity>\n"
				+ "* wallet or w \n"
				+ "* classify <wine> <stars> or c <wine> <stars>\n"
				+ "* talk <user> <message> or t <user> <message>\n"
				+ "* read or r\n"
				+ "*_____________________________________________________________\n"
				+ "**************************************************************\n");
    }
}
