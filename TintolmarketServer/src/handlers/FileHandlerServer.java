package handlers;

import java.io.File;

public class FileHandlerServer {

    static final String CLIENT_FILES_DIRECTORY = "ClientsFiles" + File.separator;
    

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
