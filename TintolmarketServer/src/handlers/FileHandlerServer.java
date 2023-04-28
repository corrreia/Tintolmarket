package handlers;

import java.io.File;
import ciphers.CipherModes;

public class FileHandlerServer {

	private static FileHandlerServer instance;
	private String cipherPassword;

    static final String CLIENT_FILES_DIRECTORY = "ClientsFiles" + File.separator;

	static final String FILE_NAME = "users.txt";

	private FileHandlerServer(String cipherPassword) throws Exception {
		this.cipherPassword = cipherPassword;
	}
    
	public static FileHandlerServer startInstance(String cipherPassword) throws Exception {
		if (instance == null) {
			instance = new FileHandlerServer(cipherPassword);
		}
		return instance;
	}

	public static FileHandlerServer getInstance() {
		return instance;
	}

	public void encrypt() throws Exception{
		CipherModes.encrypt(new File(FILE_NAME), cipherPassword);
	}

	public void decrypt() throws Exception{
		CipherModes.decrypt(new File(FILE_NAME));
	}

    public static void setupClientDirectory(String userID){
        String userIDPath = CLIENT_FILES_DIRECTORY + userID;
        createDirectory(userIDPath);
        
        String winePhotosPath = userIDPath + File.separator + "WinePhotos";
        createDirectory(winePhotosPath);
    }

    /**
	 * Creates a directory
	 * 
	 * @param directoryPath the directory path
	 */
	public static void createDirectory(String directoryPath) {

		File directoryFilePath = new File(directoryPath);
		if (!directoryFilePath.exists()){
			directoryFilePath.mkdirs();
		}
	}
}

