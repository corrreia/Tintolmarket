package handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.List;

/**
 * Class that handles the operations input by the user and
 * sends them to the server.
 * 
 * It contains the username, the input and output streams.
 * 
 * @author Tomás Correia | fc57102
 * @author Miguel Pato   | fc56372
 * @author João Vieira   | fc45677
 */
public class OperationMenu {

    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private String username;
    private String operation;

    /**
     * Constructor for the OperationMenu class.
     * 
     * @param outStream The output stream of the user.
     * @param inStream The input stream of the user.
     * @param username The username of the user.
     */
    public OperationMenu(ObjectOutputStream outStream, ObjectInputStream inStream, String username) {
        this.outStream = outStream;
        this.inStream = inStream;
        this.username = username;

        this.operation = null;
    }

    /**
     * Method to handle add operations.
     * Also sends the image of the wine to the server.
     * 
     * @param wine  The name of the wine.
     * @param image The path to the image of the wine.
     */
    private void add(String wine, String image) {
        // check if image is a valid image path and if the file actually exists
        if (!image.endsWith(".jpg") && !image.endsWith(".png")) {
            System.out.println("Image must be a .jpg or .png file. Please try again.");
            return;
        }

        
        File imageFile = new File(image);
        if (!imageFile.exists()) {
            System.out.println("Image file not found. Please try again.");
            return;
        }

        operation = "add " + wine + " " + imageFile.getName();

        // send image file
        byte[] imageData;
        try {
            imageData = Files.readAllBytes(imageFile.toPath());
        } catch (IOException e) {
            System.out.println("Error while reading image file. Please try again.");
            return;
        }

        try {
            outStream.writeObject(operation);
            outStream.writeInt(imageData.length);
            outStream.write(imageData);
            outStream.flush();
        } catch (IOException e) {
            System.out.println("Error while sending image file. Please try again.");
            return;
        }

        int serverResponse;
        try {
            serverResponse = inStream.readInt();
            if (serverResponse == 0)
                System.out.println(wine + " added successfully.");
            else
                System.out.println(wine + " already exists.");
        } catch (IOException e) {
            System.out.println("Lost connection to server. Please try again.");
            return;
        }

    }

    /**
     * Method to handle sell operations.
     * Method that takes a wine previously added and puts it up for sale.
     * 
     * @param wine The name of the wine.
     * @param value The value of the wine.
     * @param quantity The quantity of the wine.
     * @throws IOException
     */
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

    /**
     * Method to handle view operations.
     * Method that takes a wine previously listed and prints its information.
     * 
     * @param wine The name of the wine.
     * @throws IOException
     * @throws ClassNotFoundException
     */
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

    /**
     * Method to handles buy operations.
     * Method that takes a wine previously listed and buys it making
     * the proper changes in the users' wallets.
     * 
     * @param wine The name of the wine.
     * @param seller The name of the seller.
     * @param quantity The quantity of the wine.
     * @throws IOException
     * @throws ClassNotFoundException
     */
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

    /**
     * Method to handle wallet operations.
     * Method that prints the current amount of money in the user's wallet.
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     */
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

    /**
     * Method to handle classify operations.
     * Method that takes a wine previously added and classifies it.
     * 
     * @param wine The name of the wine.
     * @param stars The number of stars to classify the wine.
     * @throws IOException
     * @throws ClassNotFoundException
     */
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
            if (serverResponse == 0) {
                System.out.println(wine + " classified successfully.");
            } else {
                System.out.println(wine + " does not exist. Please try again.");
            }
        }
    }

    /**
     * Method to handle talk operations.
     * Method that allows a user to send a message to another 
     * resgistered user.
     * 
     * @param user The name of the user to send the message to.
     * @param message The message to send.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void talk(String user, String message) throws IOException {
        operation = "talk " + user + " " + message;
        outStream.writeObject(operation);
        outStream.flush();

        int serverResponse = inStream.readInt();
        if (serverResponse == 0) {
            System.out.println("Message sent successfully.");
        } else {
            System.out.println("User " + user + " does not exist. Please try again.");
        }
    }

    /**
     * Method to handle read operations.
     * Method that prints all the messages received by the user.
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void read() throws IOException, ClassNotFoundException {
        operation = "read";
        outStream.writeObject(operation);
        outStream.flush();

        int serverResponse = inStream.readInt();
        if (serverResponse == 0) {
            @SuppressWarnings("unchecked")
            List<String> messages = (List<String>) inStream.readObject();

            System.out.println("\n--------------------- Inbox --------------------\n");
            if (messages.isEmpty()) {
                System.out.println("No messages.");
            } else {
                for (String msg : messages) {
                    System.out.println(msg);
                }
            }
            System.out.println("\n------------------------------------------------\n");
        }
    }

    /**
     * Method to handle incorrect operations.
     */
    private void incorrectOperation() {
        System.out.println("Incorrect operation. Please try again.\n Type 'help' for a list of available operations.");
    }

    /**
     * Method to handle help operations.
     * Method that prints a list of all the available operations.
     */
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

    /**
     * Method to handle the client's operations.
     * Operations such as:
     * - add <wine> <image> or a <wine> <image>
     * - sell <wine> <value> <quantity> or s <wine> <value> <quantity>\
     * - view <wine> or v <wine>
     * - buy <wine> <seller> <quantity> or b <wine> <seller> <quantity>
     * - wallet or w
     * - classify <wine> <stars> or c <wine> <stars>
     * - talk <user> <message> or t <user> <message>
     * - read or r
     * - help or h
     * - quit or q
     * 
     * @param in The buffer reader to read the user's input.
     * @throws Exception
     */
    public void receiveOperation(BufferedReader in) throws Exception {
        System.out.print("Enter an operation: ");
        String op = in.readLine();

        String[] opSplit = op.trim().split(" ");
        String command = opSplit[0];

        while (!command.equals("quit") && !command.equals("q")) {

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

            opSplit = op.trim().split(" ");
            command = opSplit[0];
        }

        System.out.println("Bye!");
        outStream.writeObject(command);
        outStream.flush();
    }
}
