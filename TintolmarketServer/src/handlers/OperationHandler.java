package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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

        String[] op = opFromClient.split(":");
        String opType = op[0];

        if (op.length > 1)
            op[1] = op[1].trim(); // Remove leading and trailing spaces

        while (!opType.equals("exit")) {
            System.out.println("Received operation: " + opFromClient);
            switch (opType) {
                case "add":
                case "a":
                    System.out.println("Adding wine");
                    String wineName = op[1].split(" ")[0];
                    String wineImage = op[1].split(" ")[1];

                    out.writeInt(stateHandler.addWine(wineName, wineImage));
                    out.flush();
                    break;
                case "sell":
                case "s":
                    System.out.println("Selling wine");
                    String wine = op[1].split(" ")[1];
                    int quantity = Integer.parseInt(op[1].split(" ")[2]);
                    float price = Float.parseFloat(op[1].split(" ")[3]);
                    int returnCode = stateHandler.addWineListingToUser(user, wine, quantity, price);
                    out.writeInt(returnCode);
                    out.flush();
                    break;
                case "view":
                case "v":
                    System.out.println("Viewing wine");
                    String wineToView = op[1];
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
                    String wineToBuy = op[1].split(" ")[0];
                    String seller = op[1].split(" ")[1];
                    int quantityToBuy = Integer.parseInt(op[1].split(" ")[2]);
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
            }
            opFromClient = (String) in.readObject();
            op = opFromClient.split(":");
            opType = op[0];
        }

    }
}
