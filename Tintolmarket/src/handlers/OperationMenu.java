package handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.List;
import java.util.PrimitiveIterator.OfDouble;

public class OperationMenu {

    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private String username;
    private String password;
    private String operation;

    public OperationMenu(ObjectOutputStream outStream, ObjectInputStream inStream, String username, String password) {
        this.outStream = outStream;
        this.inStream = inStream;
        this.username = username;
        this.password = password;

        this.operation = null;
    }

    private void add(String wine, String image) throws IOException {
        operation = "add " + wine + " " + image;
        outStream.writeObject(operation);
        outStream.flush();
        
        int serverResponse = inStream.readInt();
        if (serverResponse == 0) {
            System.out.println(wine + " added successfully.");
        } else {
            System.out.println(wine + "already exists.");
        }
    }

    private void sell(String wine, String value, String quantity) throws IOException {
        // can't sell 0 quantity and can't sell a wine for 0 value
        if (Integer.parseInt(quantity) <= 0 || Integer.parseInt(value) <= 0) {
            System.out.println("Problem with wine value or quantity. Please try again.");
        } else {
            operation = "sell " + wine + " " + value + " " + quantity;
            outStream.writeObject(operation);
            outStream.flush();

            int serverResponse = inStream.readInt();
            if (serverResponse == 0) {
                System.out.println("Wine " + wine + " is now up for sale.");
            } else if (serverResponse == -1) {
                System.out.println("Wine " + wine + " does not exist. Please add the wine first and try again.");
            } else {
                System.out.println("Unexpected error. Please try again."); // should never happen
            }
        }
    }

    private void view(String wine) throws IOException, ClassNotFoundException {
        operation = "view " + wine;

        outStream.writeObject(operation);
        outStream.flush();

        int serverResponse = inStream.readInt();
        if (serverResponse == 0) {
            String view = (String) inStream.readObject();
            System.out.println(view);
        } else {
            System.out.println("Wine " + wine + " does not exist. Please try again.");
        }
    }

    private void buy(String wine, String seller, String quantity) throws IOException {
        if (seller.equals(username)) {
            System.out.println("You can't buy your own wine. Please try again.");
        } else {

            if (Integer.parseInt(quantity) <= 0) {
                System.out.println("Problem with wine quantity. Please try again.");
            } else {
                operation = "buy " + wine + " " + seller + " " + quantity;
                outStream.writeObject(operation);
                outStream.flush();

                int serverResponse = inStream.readInt();
                if (serverResponse == 0) {
                    System.out.println(quantity + " bottles of " + wine + " bought successfully.");
                } else {
                    System.out.println("Wine " + wine + " does not exist. Please try again.");
                }
            }
        }
    }

    private void wallet() throws IOException {
        operation = "wallet";
        outStream.writeObject(operation);
        outStream.flush();

        if (inStream.readInt() == 0) {
            Float wallet = inStream.readFloat();
            System.out.println("Your wallet has " + wallet + " euros.");
        } else {
            System.out.println("Error while reading wallet.");
        }     
    }

    private void classify(String wine, String stars) throws IOException {
        // parse stars to float
        float starsFloat = Float.parseFloat(stars);
        if (starsFloat < 1 || starsFloat > 5) {
            System.out.println("Stars must be between 1 and 5. Please try again.");
        } else {
            operation = "classify " + wine + " " + stars;
            outStream.writeObject(operation);
            outStream.flush();

            int serverResponse = inStream.readInt();
            if (serverResponse == 0){
                System.out.println(wine + " classified successfully.");
            } else {
                System.out.println(wine + " does not exist. Please try again.");
            }
        }
    }

    private void talk(String user, String message) throws IOException{
        operation = "talk " + user + " " + message;
        outStream.writeObject(operation);
        outStream.flush();

        int serverResponse = inStream.readInt();
        if(serverResponse == 0){
            System.out.println("Message sent successfully.");
        } else {
            System.out.println("User " + user + " does not exist. Please try again.");
        }
    }

    private void read() throws IOException, ClassNotFoundException {
        operation = "read";
        outStream.writeObject(operation);
        outStream.flush();
        
        int serverResponse = inStream.readInt();
        if (serverResponse == 0){
            @SuppressWarnings("unchecked")
            List<String> messages = (List<String>) inStream.readObject();

			System.out.println("\n--------------------- Inbox --------------------\n"); 
            if(messages.isEmpty()){
                System.out.println("No messages.");
            } else {
                for(String msg : messages){
                    System.out.println(msg);
                }
            }
            System.out.println("\n------------------------------------------------\n"); 
        }
    }

    private void incorrectOperation() {
        System.out.println("Incorrect operation. Please try again.\n Type 'help' for a list of available operations.");
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
                + "* help or h\n"
                + "* quit or q\n"
                + "*_____________________________________________________________\n"
                + "**************************************************************\n");
    }

    public void receiveOperation(BufferedReader in) throws Exception {
        System.out.print("Enter an operation: ");
        String op = in.readLine();

        String[] opSplit = op.split(" ");
        String command = opSplit[0];

        while (!command.equals("quit")) {

            switch (command) {
                case "add":
                case "a":
                    if (opSplit.length == 3) {
                        String wine = opSplit[1];
                        String image = opSplit[2];
                        add(wine, image);
                    } else {
                        incorrectOperation();
                        System.out.println("Hint: add <wine> <image> or a <wine> <image>");
                    }
                    break;
                case "sell":
                case "s":
                    if (opSplit.length == 4) {
                        String wine = opSplit[1];
                        String value = opSplit[2];
                        String quantity = opSplit[3];
                        sell(wine, value, quantity);
                    } else {
                        incorrectOperation();
                        System.out.println("Hint: sell <wine> <value> <quantity> or s <wine> <value> <quantity>");
                    }
                    break;
                case "view":
                case "v":
                    if (opSplit.length == 2) {
                        String wine = opSplit[1];
                        view(wine);
                    } else {
                        incorrectOperation();
                        System.out.println("Hint: view <wine> or v <wine>");
                    }
                    break;
                case "buy":
                case "b":
                    if (opSplit.length == 4) {
                        String wine = opSplit[1];
                        String seller = opSplit[2];
                        String quantity = opSplit[3];
                        buy(wine, seller, quantity);
                    } else {
                        incorrectOperation();
                        System.out.println("Hint: buy <wine> <seller> <quantity> or b <wine> <seller> <quantity>");
                    }
                    break;
                case "wallet":
                case "w":
                    if (opSplit.length == 1) {
                        wallet();
                    } else {
                        incorrectOperation();
                        System.out.println("Hint: wallet or w");
                    }
                    break;
                case "classify":
                case "c":
                    if (opSplit.length == 3) {
                        String wine = opSplit[1];
                        String stars = opSplit[2];
                        classify(wine, stars);
                    } else {
                        incorrectOperation();
                        System.out.println("Hint: classify <wine> <stars> or c <wine> <stars>");
                    }
                    break;
                case "talk":
                case "t":
                    if (opSplit.length >= 3) {
                        String user = opSplit[1];
                        String message = opSplit[2];
                        talk(user, message);
                    } else {
                        incorrectOperation();
                        System.out.println("Hint: talk <user> <message> or t <user> <message>");
                    }
                    break;
                case "read":
                case "r":
                    if (opSplit.length == 1) {
                        read();
                    } else {
                        incorrectOperation();
                        System.out.println("Hint: read or r");
                    }
                    break;
                case "help":
                case "h":
                    if (opSplit.length == 1) {
                        showMenu();
                    } else {
                        incorrectOperation();
                        System.out.println("Hint: help or h");
                    }
                    break;
                default:
                    incorrectOperation();
                    break;
            }
            System.out.print("Enter an operation: ");
            op = in.readLine();

            opSplit = op.split(" ");
            command = opSplit[0];
        }
        
        System.out.println("Bye!");
        outStream.writeObject(command);
        outStream.flush();
    }
}
