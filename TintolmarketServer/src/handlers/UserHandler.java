package handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class UserHandler {

    private ObjectOutputStream outStream = null;
    private ObjectInputStream inStream = null;
    private String username = null;

    private static final String FILE_NAME = "credentials.txt";
    private static File userFile;

    private UserHandler(String username, ObjectInputStream inStream, ObjectOutputStream outStream) throws IOException {

        this.outStream = outStream;
        this.inStream = inStream;
        this.username = username;

    }

    private static void checkFile() throws IOException {
        userFile = new File(FILE_NAME);
        if (!userFile.exists()) {
            if (!userFile.createNewFile()) {
                throw new IOException("Could not create file " + userFile.getAbsolutePath());
            }
            System.out.println("user:password file created successfully");
        } else
            System.out.println("Using existing user:password file");
    }

    private static boolean registerUser(String userID, String passwd) throws IOException {
        FileOutputStream fs = new FileOutputStream(userFile, true);
        fs.write((userID + ":" + passwd).getBytes());
        fs.write(System.lineSeparator().getBytes());
        System.out.println("New user " + userID + " registered successfully");
        fs.close();
        return true;
    }

    private static boolean isRegistered(String userID) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(userFile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] user = line.split(":");
            if (user[0].equals(userID)) {
                System.out.println("User " + userID + " is already registered");
                br.close();
                return true;
            }
        }
        br.close();
        return false;
    }

    private static boolean checkCredentials(String userID, String passwd) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(userFile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] user = line.split(":");
            if (user[0].equals(userID) && user[1].equals(passwd)) {
                br.close();
                return true;
            }
        }
        br.close();
        return false;
    }

    public static UserHandler authenticate(String username, String password, ObjectInputStream inStream,
            ObjectOutputStream outStream) throws IOException {

        checkFile();

        if (isRegistered(username)) {
            outStream.writeInt(1);
            if (checkCredentials(username, password)) {
                outStream.writeObject(true);
                System.out.println("User " + username + " logged in successfully! :)\n");
                return new UserHandler(username, inStream, outStream);

            } else {
                outStream.writeObject(false);
                throw new IOException("Wrong password for user " + username);
            }
        } else {
            outStream.writeInt(0);
            registerUser(username, password);
            outStream.writeObject(true);
            throw new IOException("User " + username + " registered successfully! :)\n");
        }
    }

    public void handleOps() throws IOException {
        // TODO: implement this to work with the operation handler
        // by now username, inStream and outStream are available and fully functional

        OperationHandler opH = OperationHandler.getInstace();
        try {
            opH.receiveAndProcessOps(username, outStream, inStream);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
