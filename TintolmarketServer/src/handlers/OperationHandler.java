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

    public void receiveAndProcessops() throws IOException, ClassNotFoundException {

        String opFromClient = (String) in.readObject();

        String[] op = opFromClient.split(":");
        String opType = op[0];

        while (!opType.equals("exit")) {
            switch (opType) {
                case "add":
                case "a":

            }
            opFromClient = (String) in.readObject();
            op = opFromClient.split(":");
            opType = op[0];
        }

    }
}
