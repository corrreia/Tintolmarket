package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class OperationHandler {

    private ObjectOutputStream out;
    private ObjectInputStream in;

    StateHandler stateHandler;

    public OperationHandler(ObjectInputStream in, ObjectOutputStream out) {
        this.in = in;
        this.out = out;
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
            }
            opFromClient = (String) in.readObject();
            op = opFromClient.split(":");
            opType = op[0];
        }

    }
}
