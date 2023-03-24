package handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class that handles the users of the server.
 * It contains the username, the input and output streams.
 * 
 * Takes care of the user authentication and everything
 * related to registration as well as saving the credentials
 * of the users to a file.
 * 
 * @author Tomás Correia | fc57102
 * @author Miguel Pato   | fc56372
 * @author João Vieira   | fc45677
 */
public class UserHandler {

    private ObjectOutputStream outStream = null;
    private ObjectInputStream inStream = null;
    private String username = null;

    private static final String FILE_NAME = "credentials.txt";
    private static File userFile;

    /**
     * Constructor for the UserHandler class.
     * 
     * @param username The username of the user.
     * @param inStream The input stream of the user.
     * @param outStream The output stream of the user.
     * @throws IOException  If there is an error with the streams.
     */
    private UserHandler(String username, ObjectInputStream inStream, ObjectOutputStream outStream) throws IOException {

        this.outStream = outStream;
        this.inStream = inStream;
        this.username = username;

    }

    /**
	 * Checks if the file "credentials.txt" exists.
     * If it doesn't exist, it creates it.
     * 
	 * @throws IOException  If there is an error with the file.
	 */
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

    /**
     * Registers a new user.
     * Format: username:password
     * 
     * @param userID    The username of the user.
     * @param passwd    The password of the user.
     * @return  True if the user was registered successfully.
     * @throws IOException  If there is an error with the FileOutputStream.
     */
    private static boolean registerUser(String userID, String passwd) throws IOException {
        FileOutputStream fs = new FileOutputStream(userFile, true);
        fs.write((userID + ":" + passwd).getBytes());
        fs.write(System.lineSeparator().getBytes());
        System.out.println("New user " + userID + " registered successfully");
        fs.close();
        return true;
    }

    /**
     * Checks if the user is already registered.
     * 
     * @param userID    The username of the user.
     * @return  True if the user is already registered.
     * @throws IOException  If there is an error with the BufferedReader.
     */
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

    /**
     * Checks if the credentials of the user are correct.
     * 
     * @param userID    The username of the user.
     * @param passwd    The password of the user.
     * @return  True if the credentials are correct.
     * @throws IOException  If there is an error with the BufferedReader.
     */
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

    /**
     * Authenticates the user.
     * 
     * @param username  The username of the user.
     * @param password  The password of the user.
     * @param inStream  The input stream of the user.
     * @param outStream The output stream of the user.
     * @return  The UserHandler of the user.
     * @throws IOException  If there is an error with the streams.
     */
    public static UserHandler authenticate(String username, String password, ObjectInputStream inStream,
            ObjectOutputStream outStream) throws IOException {

        checkFile();

        if (isRegistered(username)) {
            outStream.writeInt(1);
            if (checkCredentials(username, password)) {
                outStream.writeObject(true);
                System.out.println("User " + username + " logged in successfully! :)\n");
                StateHandler.getInstance().addUser(username);
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

    /**
     * Handles the operations of the user.
     * 
     * @throws IOException  If there is an error with the streams.
     */
    public void handleOps() throws IOException {

        OperationHandler opH = OperationHandler.getInstace();
        try {
            opH.receiveAndProcessOps(username, outStream, inStream);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
