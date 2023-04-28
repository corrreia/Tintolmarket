package handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import ciphers.CipherModes;

public class FileHandlerServer {

	private static FileHandlerServer instance;

    static final String CLIENT_FILES_DIRECTORY = "ClientsFiles" + File.separator;

	static final String FILE_NAME = "users.cif";
	static final String CIPHER_KEY = "cipher.key";

	private FileHandlerServer(String cipherPassword) throws Exception {
		checkFile();
		// genSecretKey(cipherPassword);
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

	public static void checkFile() throws IOException{
		File file = new File(FILE_NAME);
		if (!file.exists()) {
			file.createNewFile();
		}
	}

	public void encrypt() throws Exception{
		CipherModes.encrypt(new File(FILE_NAME), CIPHER_KEY);
	}

	public void decrypt() throws Exception{
		// check if file isn't empty
		if (new File(FILE_NAME).length() == 0) {
			return;
		}
		CipherModes.decrypt(new File(FILE_NAME), CIPHER_KEY);
	}

	// private void genSecretKey(String cipherPassword) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
	// 	File keyFile = new File(CIPHER_KEY);
	// 	if(!keyFile.exists()){
	// 		byte[] salt = { (byte) 0xc9, (byte) 0x36, (byte) 0x78, (byte) 0x99, (byte) 0x52, (byte) 0x3e, (byte) 0xea, (byte) 0xf2 };
	// 		PBEKeySpec keySpec = new PBEKeySpec(cipherPassword.toCharArray(), salt, 20); // pass, salt, iterations
	// 		SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
	// 		SecretKey key = kf.generateSecret(keySpec);
	// 		byte[] keyBytes = key.getEncoded();
	// 		FileOutputStream fos = new FileOutputStream(CIPHER_KEY);
	// 		fos.write(keyBytes);
	// 		fos.close();
	// 	}
	// }

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

