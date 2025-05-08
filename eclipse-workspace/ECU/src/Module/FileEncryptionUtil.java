package Module;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

public class FileEncryptionUtil {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_SIZE = 16;

    public static void encryptFile(String inputFilePath, String outputFilePath, String secretKey) throws Exception {
        byte[] inputBytes = Files.readAllBytes(Paths.get(inputFilePath));
        byte[] keyBytes = secretKey.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
        byte[] outputBytes = cipher.doFinal(inputBytes);

        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            fos.write(iv); 
            fos.write(outputBytes);
        }
    }

    public static void decryptFile(String inputFilePath, String outputFilePath, String secretKey) throws Exception {
        byte[] allBytes = Files.readAllBytes(Paths.get(inputFilePath));
        byte[] iv = new byte[IV_SIZE];
        byte[] encryptedBytes = new byte[allBytes.length - IV_SIZE];

        System.arraycopy(allBytes, 0, iv, 0, IV_SIZE);
        System.arraycopy(allBytes, IV_SIZE, encryptedBytes, 0, encryptedBytes.length);

        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

        byte[] outputBytes = cipher.doFinal(encryptedBytes);
        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            fos.write(outputBytes);
        }
    }
}
