package uk.thisjowi.Notes.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


@Component
public class EncryptionUtil {

    private static final Logger logger = LoggerFactory.getLogger(EncryptionUtil.class);
    
    private static byte[] secretKeyBytes;
    private static final int AES_KEY_SIZE = 32; // 256 bits
    private static final int IV_SIZE = 16; // 128 bits for AES
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";

    /**
     * Initialize encryption utility with secret key from application.yml configuration.
     * Uses ${JWT_SECRET} environment variable via Spring injection.
     * 
     * @param secretKey the encryption secret key (minimum 32 characters)
     */
    public EncryptionUtil(@Value("${jwt.secret}") String secretKey) {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            logger.warn("[Encryption] No encryption key provided, using development default");
            secretKey = "default-dev-secret-key-at-least-32-characters-long";
        }
        
        if (secretKey.length() < AES_KEY_SIZE) {
            throw new IllegalArgumentException("Encryption secret key must be at least " + AES_KEY_SIZE + " characters long (256 bits). Current length: " + secretKey.length());
        }
        
        // Generate consistent key bytes using SHA-256
        secretKeyBytes = generateKeyBytes(secretKey);
        logger.info("[Encryption] Encryption utility initialized successfully with key length: {} chars -> {} bits", secretKey.length(), secretKeyBytes.length * 8);
    }

    /**
     * Generate AES key bytes from secret key using SHA-256.
     */
    private static byte[] generateKeyBytes(String secretKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(secretKey.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            logger.error("[Encryption] SHA-256 algorithm not available", e);
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Encrypt plaintext using AES-256-CBC with random IV.
     * 
     * @param plaintext the text to encrypt
     * @return Base64-encoded string containing IV + ciphertext
     * @throws RuntimeException if encryption fails
     */
    public static String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            logger.warn("[Encryption] Attempt to encrypt null or empty plaintext");
            return plaintext;
        }

        try {
            // Generate random IV
            SecureRandom random = new SecureRandom();
            byte[] iv = new byte[IV_SIZE];
            random.nextBytes(iv);

            // Create cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(secretKeyBytes, 0, AES_KEY_SIZE, KEY_ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            // Encrypt
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Concatenate IV + ciphertext
            byte[] ivAndEncrypted = new byte[IV_SIZE + encrypted.length];
            System.arraycopy(iv, 0, ivAndEncrypted, 0, IV_SIZE);
            System.arraycopy(encrypted, 0, ivAndEncrypted, IV_SIZE, encrypted.length);

            // Base64 encode
            String result = Base64.getEncoder().encodeToString(ivAndEncrypted);
            logger.debug("[Encryption] Data encrypted successfully (plaintext length: {}, encrypted length: {})", plaintext.length(), result.length());
            return result;
        } catch (Exception e) {
            logger.error("[Encryption] Encryption failed", e);
            throw new RuntimeException("Error encrypting data: " + e.getMessage(), e);
        }
    }

    /**
     * Decrypt Base64-encoded ciphertext using AES-256-CBC.
     * 
     * @param encryptedText Base64-encoded string containing IV + ciphertext
     * @return decrypted plaintext
     * @throws RuntimeException if decryption fails
     */
    public static String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isBlank()) {
            logger.warn("[Encryption] Attempt to decrypt null or empty text");
            return encryptedText;
        }

        try {
            // Base64 decode
            byte[] decodedBytes;
            try {
                decodedBytes = Base64.getDecoder().decode(encryptedText);
            } catch (IllegalArgumentException e) {
                logger.warn("[Encryption] Invalid Base64 format, returning original string");
                return encryptedText;
            }

            // Validate minimum length (IV + at least one block)
            if (decodedBytes.length < IV_SIZE + 16) {
                logger.warn("[Encryption] Encrypted data too short, returning original string");
                return encryptedText;
            }

            // Extract IV
            byte[] iv = new byte[IV_SIZE];
            System.arraycopy(decodedBytes, 0, iv, 0, IV_SIZE);

            // Extract ciphertext
            byte[] ciphertext = new byte[decodedBytes.length - IV_SIZE];
            System.arraycopy(decodedBytes, IV_SIZE, ciphertext, 0, decodedBytes.length - IV_SIZE);

            // Create cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(secretKeyBytes, 0, AES_KEY_SIZE, KEY_ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            // Decrypt
            byte[] decrypted = cipher.doFinal(ciphertext);
            String result = new String(decrypted, StandardCharsets.UTF_8);
            logger.debug("[Encryption] Data decrypted successfully (encrypted length: {}, decrypted length: {})", encryptedText.length(), result.length());
            return result;
        } catch (Exception e) {
            logger.error("[Encryption] Decryption failed", e);
            throw new RuntimeException("Error decrypting data: " + e.getMessage(), e);
        }
    }
}

