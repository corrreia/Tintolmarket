package handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class that handles the users of the server.
 * It contains the username, the input and output streams.
 * 
 * Takes care of the user authentication and everything
 * related to registration as well as saving the credentials
 * of the users to a file.
 * 
 * @author Tomás Correia | fc57102
 * @author Miguel Pato | fc56372
 * @author João Vieira | fc45677
 */
public class UserHandler {

    private static final String FILE_NAME = "users.txt";

    private File file;

    public UserHandler() throws IOException {
        this.file = new File(FILE_NAME);
        checkFile();
        StateHandler.getInstance();
    }

    /**
     * Checks if the file "credentials.txt" exists.
     * If it doesn't exist, it creates it.
     * 
     * @throws IOException If there is an error with the file.
     */
    private void checkFile() throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    /**
     * Registers a new user.
     * Format: username:password
     * 
     * @param userID The username of the user.
     * @param passwd The password of the user.
     * @return True if the user was registered successfully.
     * @throws IOException If there is an error with the FileOutputStream.
     */
    public boolean registerUser(String userID) throws IOException {
        FileOutputStream fs = new FileOutputStream(file, true);
        fs.write((userID).getBytes());
        fs.write(System.lineSeparator().getBytes());
        StateHandler.getInstance().addUser(userID);
        System.out.println("New user " + userID + " registered successfully");
        fs.close();
        return true;
    }

    /**
     * Checks if the user is already registered.
     * 
     * @param userID The username of the user.
     * @return True if the user is already registered.
     * @throws IOException If there is an error with the BufferedReader.
     */
    public boolean isRegistered(String userID) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.equals(userID)) {
                br.close();
                return true;
            }
        }
        br.close();
        return false;
    }
}
