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
 * @author Miguel Pato   | fc56372
 * @author João Vieira   | fc45677
 */
public class UserHandler {

    private static final String FILE_NAME = "users.txt";
	
	private File file;

    public UserHandler() throws IOException {
        this.file = new File(FILE_NAME);
    }

    /**
     * Registers a new user.
     * Format: username:password
     * 
     * @param userID    The username of the user.
     * @param cert
     * @param passwd    The password of the user.
     * @return  True if the user was registered successfully.
     * @throws Exception
     */
    public boolean registerUser(String userID, String cert) throws Exception {
        FileHandlerServer fH = FileHandlerServer.getInstance();
        fH.decrypt();
        System.out.println(file.exists());
        FileOutputStream fs = new FileOutputStream(file, true);
        fs.write((userID + ":" + cert).getBytes());
        fs.write(System.lineSeparator().getBytes());
        StateHandler.getInstance().addUser(userID);
        System.out.println("New user " + userID + " registered successfully");
        fs.close();
        fH.encrypt();
        System.out.println(file.exists());
        return true;
    }

    /**
     * Checks if the user is already registered.
     * 
     * @param userID    The username of the user.
     * @return  True if the user is already registered.
     * @throws Exception
     */
    public boolean isRegistered(String userID) throws Exception {
        FileHandlerServer fH = FileHandlerServer.getInstance();
        fH.decrypt();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] user = line.split(":");
            if (user[0].equals(userID)) {
                System.out.println("User " + userID + " is already registered");
                br.close();
                fH.encrypt();
                return true;
            }
        }
        br.close();
        fH.encrypt();
        return false;
    }

    
}
