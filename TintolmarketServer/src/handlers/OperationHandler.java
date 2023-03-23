package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class OperationHandler {

    private static OperationHandler instance;

    private static StateHandler stateHandler;

    public OperationHandler() {
    }

    public static OperationHandler getInstace() {
        stateHandler = StateHandler.getInstance();

        if (instance == null) {
            instance = new OperationHandler();
        }
        return instance;
    }

    public void receiveAndProcessOps(String user, ObjectOutputStream out, ObjectInputStream in)
            throws IOException, ClassNotFoundException {

        String opFromClient = (String) in.readObject();

        String[] args = opFromClient.trim().split(" ");
        String opType = args[0];

        while (!opType.equals("exit")) {
            System.out.println("Received operation: " + opFromClient);
            switch (opType) {
                case "add":
                case "a":
                    System.out.println("Adding wine");
                    String wineName = args[1];
                    String wineImage = args[2];

                    out.writeInt(stateHandler.addWine(wineName, wineImage));
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
                    out.write(wineClassify);
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
