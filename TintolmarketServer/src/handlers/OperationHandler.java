package handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Class that handles the operations of the server.
 * It contains the username, the input and output streams.
 * 
 * Takes care of the operations that the client can do
 * and sends the appropriate response to the client.
 * 
 * @author Tomás Correia | fc57102
 * @author Miguel Pato   | fc56372
 * @author João Vieira   | fc45677
 */
public class OperationHandler {

    private static final String IMAGES_FROM_CLIENT = "serverWineImages";

    private static OperationHandler instance;

    private static StateHandler stateHandler;

    /**
     * Constructor for the OperationHandler class.
     */
    public OperationHandler() {
    }

    /**
     * Gets the instance of the OperationHandler class (Singleton).
     * 
     * @return The instance of the OperationHandler class.
     */
    public static OperationHandler getInstace() {
        stateHandler = StateHandler.getInstance();

        if (instance == null) {
            instance = new OperationHandler();
        }
        return instance;
    }

    /**
     * Receives the operations from the client and processes them.
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
     * @param user The username of the client.
     * @param out The output stream of the client.
     * @param in The input stream of the client.
     * @throws IOException If there is an error with the streams.
     * @throws ClassNotFoundException if there is an error with the class.
     */
    public void receiveAndProcessOps(String user, ObjectOutputStream out, ObjectInputStream in)
            throws IOException, ClassNotFoundException {

        String opFromClient = (String) in.readObject();

        String[] args = opFromClient.trim().split(" ");
        String opType = args[0];

        while (!opType.equals("quit") && !opType.equals("q")) {
            System.out.println("Received operation: " + opFromClient);
            switch (opType) {
                case "add":
                case "a":
                    System.out.println("Adding wine");
                    String wineName = args[1];
                    String imageFileName = args[2];

                    File directory = new File(IMAGES_FROM_CLIENT);
                    if (!directory.exists()) {
                        directory.mkdir(); // create the directory if it doesn't exist
                        System.out.println("Directory created: " + directory.getAbsolutePath());
                    }

                    int imageDataLength = in.readInt();
                    byte[] imageData = new byte[imageDataLength];
                    in.readFully(imageData);

                    System.out.println("Image received: " + imageFileName);
                    System.out.println("Image length: " + imageDataLength);

                    if (imageFileName.endsWith(".jpg")) {
                        imageFileName = wineName + ".jpg";
                    } else {
                        imageFileName = wineName + ".png";
                    }

                    File file = new File(IMAGES_FROM_CLIENT + "/" + imageFileName);
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(imageData);
                    fos.flush();
                    fos.close();

                    out.writeInt(stateHandler.addWine(wineName, imageFileName));
                    out.flush();
                    break;
                case "sell":
                case "s":
                    System.out.println("Selling wine");
                    String wine = args[1];
                    float price = Float.parseFloat(args[2]);
                    int quantity = Integer.parseInt(args[3]);
                    int returnCode = stateHandler.addWineListingToUser(user, wine, quantity, price);
                    out.writeInt(returnCode);
                    out.flush();
                    break;
                case "view":
                case "v":
                    System.out.println("Viewing wine");
                    String wineToView = args[1];
                    String wineView = stateHandler.wineView(wineToView);

                    if (wineView == null) {
                        out.writeInt(-1);
                        out.flush();
                    } else {
                        out.writeInt(0);
                        out.writeObject(wineView);
                        out.flush();
                    }
                    break;
                case "buy":
                case "b":
                    System.out.println("Buying wine"); // buy: <wine> <seller> <quantity>
                    String wineToBuy = args[1];
                    String seller = args[2];
                    System.out.println(args[3]);
                    int quantityToBuy = Integer.parseInt(args[3]);
                    int wineBuy = stateHandler.buySellWine(seller, user, wineToBuy, quantityToBuy);
                    out.writeInt(wineBuy);
                    out.flush();
                    break;
                case "wallet":
                case "w":
                    System.out.println("Viewing wallet");
                    Float wallet = stateHandler.getBalance(user);
                    out.writeInt(0);
                    out.writeFloat(wallet);
                    out.flush();
                    break;
                case "classify":
                case "c":
                    System.out.println("Classifying wine");
                    String wineToClassify = args[1];
                    String classification = args[2];
                    int wineClassify = stateHandler.classify(wineToClassify, classification);
                    out.writeInt(wineClassify);
                    out.flush();
                    break;
                case "talk":
                case "t":
                    System.out.println("Talking to user");
                    String userToTalk = args[1];
                    String message = args[2];
                    int userTalk = stateHandler.talk(user, userToTalk, message);
                    out.writeInt(userTalk);
                    out.flush();
                    break;
                case "read":
                case "r":
                    System.out.println("Reading messages");
                    List<String> messages = stateHandler.read(user);
                    out.writeObject(messages);
                    out.flush();
                    break;
            }

            opFromClient = (String) in.readObject();
            args = opFromClient.trim().split(" ");
            opType = args[0];
        }

        System.out.println("Closing connection with client " + user);
    }
}
