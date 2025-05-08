package Module;/*
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class AESBruteForce {
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    public static void tryDecrypt(String encryptedFilePath) {
        try {
            byte[] allBytes = Files.readAllBytes(Paths.get(encryptedFilePath));

            byte[] iv = Arrays.copyOfRange(allBytes, 0, 16);
            byte[] ciphertext = Arrays.copyOfRange(allBytes, 16, allBytes.length);

            for (int i = 0; i <= 9999; i++) {
                String keyStr = String.format("%04d", i); // e.g. 0001
                byte[] keyBytes = String.format("%-16s", keyStr).replace(' ', '0').getBytes();

                try {
                    Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                    SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
                    IvParameterSpec ivSpec = new IvParameterSpec(iv);
                    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

                    byte[] decrypted = cipher.doFinal(ciphertext);
                    String plaintext = new String(decrypted);

                    System.out.println("[!] Key found: " + keyStr);
                    System.out.println("Decrypted text: \n" + plaintext);

                    Files.write(Paths.get("decrypted_output.txt"), decrypted);
                    System.out.println("Saved to decrypted_output.txt");
                    return;
                } catch (Exception ex) {
                    // Try next key
                }
            }

            System.out.println("[-] Key not found. Strong encryption.");
        } catch (IOException e) {
            System.err.println("File read error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        tryDecrypt("C:\\Users\\THARUN\\Downloads\\Extended_ECU_Data_Report_2.xlsx.bin");
    }
}
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;
public class AESBruteForceDecryption {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_SIZE = 16; // AES CBC mode requires a 16-byte IV

    // Function to check if the decrypted data contains an Excel file signature
    private static boolean isValidExcel(byte[] decryptedData) {
        // Excel XLSX files typically start with the following bytes (PK\x03\x04)
        return decryptedData.length > 4 && decryptedData[0] == 'P' && decryptedData[1] == 'K' 
                && decryptedData[2] == 0x03 && decryptedData[3] == 0x04;
    }

    // Function to pad the key to 16 bytes (for AES-128)
    private static byte[] get16ByteKey(byte[] keyBytes) {
        byte[] paddedKey = new byte[16];
        System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 16));
        // If the key is shorter than 16 bytes, we pad it with zeros
        return paddedKey;
    }

    // Try brute-forcing with 4-byte keys (0000 to 9999, as an example)
    public static void tryDecrypt(String encryptedFilePath) {
        try {
            // Read the encrypted file into a byte array
            byte[] encryptedData = Files.readAllBytes(Paths.get(encryptedFilePath));

            // Extract the IV (first 16 bytes of the file)
            byte[] iv = new byte[IV_SIZE];
            System.arraycopy(encryptedData, 0, iv, 0, IV_SIZE);
            byte[] ciphertext = new byte[encryptedData.length - IV_SIZE];
            System.arraycopy(encryptedData, IV_SIZE, ciphertext, 0, ciphertext.length);

            // Brute-force all 4-byte key combinations (0000 to 9999 for simplicity)
            for (int i = 0; i < 10000; i++) {
                String keyStr = String.format("%04d", i); // Zero-padded 4-digit number (0000 to 9999)
                byte[] keyBytes = keyStr.getBytes(); // Convert to byte array

                // Ensure the key is padded to 16 bytes
                byte[] paddedKey = get16ByteKey(keyBytes);

                // Create SecretKeySpec and IvParameterSpec for AES
                SecretKeySpec secretKey = new SecretKeySpec(paddedKey, "AES");
                IvParameterSpec ivSpec = new IvParameterSpec(iv);

                // Initialize AES cipher in DECRYPT_MODE
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

                try {
                    // Attempt decryption
                    byte[] decryptedData = cipher.doFinal(ciphertext);

                    // Check if the decrypted data contains a valid Excel file signature
                    if (isValidExcel(decryptedData)) {
                        System.out.println("Possible key found: " + keyStr);
                        System.out.println("Decrypted data (Excel content): ");
                        System.out.println(new String(decryptedData)); // Print or save to a file
                        return; // If valid, exit the loop
                    }
                } catch (Exception e) {
                    // Ignore exceptions and continue the brute force attack
                }
            }
            System.out.println("Brute-force failed. No valid key found.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Specify the path to your encrypted file
        String encryptedFilePath = "C:\\Users\\THARUN\\OneDrive\\ドキュメント\\Downloads\\anime.txt.bin";
        
        // Try to decrypt the file with brute-force attack
        tryDecrypt(encryptedFilePath);
    }
}*/

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class AESBruteForceDecryptor {

    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int MIN_KEYLEN = 4;
    private static final int MAX_KEYLEN = 6;
    private static final String KNOWN_PHRASE = "ECU";  

    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String ENCRYPTED_FILE = "C:\\\\Users\\\\THARUN\\\\OneDrive\\\\ドキュメント\\\\Downloads\\\\anime.txt.bin";

    public static void main(String[] args) throws Exception {
        byte[] allBytes = Files.readAllBytes(new File(ENCRYPTED_FILE).toPath());
        byte[] iv = Arrays.copyOfRange(allBytes, 0, 16);
        byte[] ciphertext = Arrays.copyOfRange(allBytes, 16, allBytes.length);

        bruteForceDecrypt(ciphertext, iv);
    }

    public static void bruteForceDecrypt(byte[] ciphertext, byte[] iv) {
        for (int len = MIN_KEYLEN; len <= MAX_KEYLEN; len++) {
            bruteForceRecursive("", len, ciphertext, iv);
        }
        System.out.println("[-] No valid key found in keyspace.");
    }

    private static void bruteForceRecursive(String prefix, int maxLength, byte[] ciphertext, byte[] iv) {
        if (prefix.length() == maxLength) {
            tryKey(prefix, ciphertext, iv);
            return;
        }

        for (char c : CHARSET.toCharArray()) {
            bruteForceRecursive(prefix + c, maxLength, ciphertext, iv);
        }
    }

    private static void tryKey(String keyStr, byte[] ciphertext, byte[] iv) {
        try {
            byte[] keyBytes = padKeyToLength(keyStr.getBytes("UTF-8"), 16); 
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            byte[] decrypted = cipher.doFinal(ciphertext);
            String decryptedText = new String(decrypted);

            if (decryptedText.contains(KNOWN_PHRASE)) {
                System.out.println("[+] Found key: " + keyStr);
                System.out.println("[+] Decrypted message:");
                System.out.println(decryptedText);
                System.exit(0);
            }
        } catch (GeneralSecurityException | java.io.UnsupportedEncodingException e) {
        }
    }

    private static byte[] padKeyToLength(byte[] key, int length) {
        return Arrays.copyOf(key, length); 
    }
}
