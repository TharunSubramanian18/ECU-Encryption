package Module;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Test {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_SIZE = 16; 
    private static final int KEY_LENGTH = 16; 

  
    public static String generateRandomKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[KEY_LENGTH];
        random.nextBytes(keyBytes);
        StringBuilder keyBuilder = new StringBuilder();
        for (byte b : keyBytes) {
            keyBuilder.append(String.format("%02x", b)); 
        }
        return keyBuilder.toString(); 
    }

    
    public static void decryptFile(String inputFilePath, String outputFilePath, String secretKey) throws Exception {
        byte[] allBytes = Files.readAllBytes(Paths.get(inputFilePath));

      
        System.out.println("[+] Total bytes read from encrypted file: " + allBytes.length);

        
        byte[] iv = Arrays.copyOfRange(allBytes, 0, IV_SIZE); 
        byte[] encryptedBytes = Arrays.copyOfRange(allBytes, IV_SIZE, allBytes.length); 


        System.out.println("[+] IV: " + Arrays.toString(iv));
        System.out.println("[+] Encrypted data size: " + encryptedBytes.length);

     
        byte[] keyBytes = Arrays.copyOf(secretKey.getBytes("UTF-8"), 16); 
        if (keyBytes.length != 16) {
            throw new Exception("The key length is incorrect, it must be 16 bytes for AES-128.");
        }

    
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

        byte[] outputBytes = cipher.doFinal(encryptedBytes);

     
        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            fos.write(outputBytes);
        }

        System.out.println("[+] Decryption complete. File saved to: " + outputFilePath);
    }

    public static void main(String args[]) {
 
        String inputFilePath = "C:\\Users\\THARUN\\Downloads\\New folder\\Extended_ECU_Data_Report_2.xlsx.bin"; 
        String outputFilePath = inputFilePath.replace(".bin", "_decrypted.xlsx"); 


        String secretKey = "1234567890123456";

        boolean decryptionSuccess = false;
        int attemptCount = 0;

        while (!decryptionSuccess) {
            try {
                attemptCount++;
                System.out.println("[+] Attempt #" + attemptCount + " with key: " + secretKey);
                decryptFile(inputFilePath, outputFilePath, secretKey);
                decryptionSuccess = true; 
            } catch (Exception e) {
                System.err.println("[-] Decryption failed on attempt #" + attemptCount + ": " + e.getMessage());

        
                secretKey = generateRandomKey();
                System.out.println("[+] Generating new secret key for next attempt: " + secretKey);
            }
        }
    }
}
