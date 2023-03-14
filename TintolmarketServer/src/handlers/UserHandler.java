package handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class UserHandler {

    private static final String FILE_NAME = "credentials.txt";
    private File userFile;

    public UserHandler() throws IOException {
        this.userFile = new File(FILE_NAME);
        if (!userFile.exists()) {
            userFile.createNewFile();
        }
    }

    public void registerUser(String userID, String passwd) throws IOException {
        
        FileOutputStream fs = new FileOutputStream(userFile, true);
        fs.write((userID + ":" + passwd).getBytes());
        fs.write(System.lineSeparator().getBytes());
        System.out.println("New user " + userID + " registered successfully");
        fs.close();
    }

    public boolean isRegistered(String userID) throws IOException {
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

    public boolean checkCredentials(String userID, String passwd) throws IOException {
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
}
