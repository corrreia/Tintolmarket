package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class OperationHandler {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String user;

    StateHandler stateHandler;

    public OperationHandler(String user, ObjectInputStream in, ObjectOutputStream out) {
        this.in = in;
        this.out = out;
        this.user = user;

        stateHandler = StateHandler.getInstance();
    }

    // public OperationHandler getIns

    public void receiveAndProcessOps() throws IOException, ClassNotFoundException {

        String opFromClient = (String) in.readObject();

        String[] op = opFromClient.split(":");
        String opType = op[0];

        while (!opType.equals("exit")) {
            switch (opType) {
                case "add":
                case "a":
                    String wineName = op[1].split(" ")[0];
                    String wineImage = op[1].split(" ")[1];
                    stateHandler.registerWine(wineName, wineImage);
                    out.writeInt(0);
                    out.flush();
                    break;
                case "sell":
                case "s":
                    String wine = op[1].split(" ")[1];
                    int quantity = Integer.parseInt(op[1].split(" ")[2]);
                    float price = Float.parseFloat(op[1].split(" ")[3]);
                    int returnCode = stateHandler.addWineListingToUser(user, wine, quantity, price);
                    out.writeInt(returnCode);
                    out.flush();
                    break;
                case "view":
                case "v":
                    // out.writeObject(stateHandler.getWines());
                    // out.flush();
                    // break;
            }
            opFromClient = (String) in.readObject();
            op = opFromClient.split(":");
            opType = op[0];
        }

    }
}
