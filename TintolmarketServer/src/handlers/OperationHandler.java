package handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
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

    private static StateHandler stateHandler;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    

    /**
     * Constructor for the OperationHandler class.
     */
    public OperationHandler(ObjectInputStream inStream, ObjectOutputStream outStream) {
        this.in = inStream;
        this.out = outStream;
        stateHandler = StateHandler.getInstance();
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
     * @throws InterruptedException
     */
    public void receiveAndProcessOps(String user)
            throws IOException, ClassNotFoundException, InterruptedException {
        
        System.out.println("Waiting for operation from client...");
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
                        // go on to IMAGES_FROM_CLIENT get the image and send it
                        // it could be a .jpg or a .png
                        // IMAGES_FROM_CLIENT + File.separator + wineToView
                        File imageFile = new File(IMAGES_FROM_CLIENT + File.separator + wineToView + ".jpg");
                        if (!imageFile.exists()) {
                            imageFile = new File(IMAGES_FROM_CLIENT + File.separator + wineToView + ".png");
                        }
                        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());

                        out.writeInt(0); // no error
                        out.writeObject(wineView); // send the wine info
                        out.writeObject(imageFile.getName()); // send the image name
                        out.writeInt(imageBytes.length); // send the image length
                        out.write(imageBytes); // send the image
                    }
                    out.flush();
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
                    // message is args[2] and on
                    String message = "";
                    for (int i = 2; i < args.length; i++) {
                        message += args[i] + " ";
                    }
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

            if (!opType.equals("quit") || !opType.equals("q")) {
                opFromClient = (String) in.readObject();
                args = opFromClient.trim().split(" ");
                opType = args[0];
            }
        }

        System.out.println("Closing connection with client " + user);
    }
}
