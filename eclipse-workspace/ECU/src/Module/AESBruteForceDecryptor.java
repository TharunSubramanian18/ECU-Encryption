package Module;/*
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
