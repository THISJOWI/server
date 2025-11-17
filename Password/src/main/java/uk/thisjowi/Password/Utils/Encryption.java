package uk.thisjowi.Password.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * EncryptionUtil provides AES-256-CBC encryption/decryption with random IV.
 * The encryption key is loaded from the environment variable (JWT_SECRET).
 * 
 * ✓ AES-256 (256-bit key)
 * ✓ CBC mode with random IV
 * ✓ PKCS5 padding
 * ✓ Secure key from environment
 * ✓ Proper logging without exposing data
 */
@Component
public class Encryption {
    private static final Logger log = LoggerFactory.getLogger(Encryption.class);
    
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int AES_KEY_SIZE = 32;  // 256 bits
    private static final int IV_SIZE = 16;       // 128 bits
    
    private final String secretKey;
    private volatile byte[] secretKeyBytes;

    public Encryption(@Value("${app.jwt.secret}") String jwtSecret) {
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            log.error("Encryption key not provided via JWT_SECRET environment variable");
            throw new IllegalArgumentException("JWT_SECRET environment variable is required");
        }
        
        if (jwtSecret.length() < AES_KEY_SIZE) {
            log.error("Encryption key is too short: {} chars (minimum {} required)", 
                jwtSecret.length(), AES_KEY_SIZE);
            throw new IllegalArgumentException(
                String.format("Encryption key must be at least %d characters long", AES_KEY_SIZE));
        }
        
        this.secretKey = jwtSecret;
        // Preprocesar la clave (primera ejecución)
        this.secretKeyBytes = deriveKeyBytes();
        log.info("✓ EncryptionUtil initialized with AES-256-CBC");
    }

    /**
     * Deriva los bytes de la clave de la cadena secretKey.
     * Usa los primeros 32 bytes (256 bits) para AES-256.
     */
    private byte[] deriveKeyBytes() {
        byte[] keyBytes = new byte[AES_KEY_SIZE];
        byte[] secretBytes = secretKey.getBytes();
        System.arraycopy(secretBytes, 0, keyBytes, 0, Math.min(secretBytes.length, keyBytes.length));
        return keyBytes;
    }

    /**
     * Encripta una cadena usando AES-256-CBC con IV aleatorio.
     * El IV se prepende al ciphertext antes de codificar en Base64.
     */
    public String encrypt(String plaintext) {
        if (plaintext == null) {
            return null;
        }
        
        try {
            // Generar IV aleatorio
            byte[] iv = new byte[IV_SIZE];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            
            // Inicializar cifrador con IV
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(secretKeyBytes, 0, AES_KEY_SIZE, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            
            // Encriptar
            byte[] encrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));
            
            // Combinar IV + ciphertext
            byte[] combined = new byte[IV_SIZE + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, IV_SIZE);
            System.arraycopy(encrypted, 0, combined, IV_SIZE, encrypted.length);
            
            // Codificar en Base64
            String result = Base64.getEncoder().encodeToString(combined);
            log.debug("Encrypted {} bytes of data", plaintext.length());
            return result;
            
        } catch (Exception e) {
            log.error("Error encrypting data: {}", e.getMessage());
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    /**
     * Desencripta una cadena encriptada con encrypt().
     * Extrae el IV del comienzo del ciphertext.
     */
    public String decrypt(String ciphertext) {
        if (ciphertext == null) {
            return null;
        }
        
        try {
            // Decodificar Base64
            byte[] combined = Base64.getDecoder().decode(ciphertext);
            
            // Validar longitud mínima (IV + datos)
            if (combined.length < IV_SIZE) {
                log.error("Invalid ciphertext: too short (expected at least {}, got {})", 
                    IV_SIZE, combined.length);
                throw new RuntimeException(String.format(
                    "Invalid ciphertext: too short (expected at least %d bytes, got %d)", 
                    IV_SIZE, combined.length));
            }
            
            // Extraer IV y ciphertext
            byte[] iv = new byte[IV_SIZE];
            byte[] encrypted = new byte[combined.length - IV_SIZE];
            System.arraycopy(combined, 0, iv, 0, IV_SIZE);
            System.arraycopy(combined, IV_SIZE, encrypted, 0, encrypted.length);
            
            // Inicializar cifrador con IV
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(secretKeyBytes, 0, AES_KEY_SIZE, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            
            // Desencriptar
            byte[] decrypted = cipher.doFinal(encrypted);
            String result = new String(decrypted, "UTF-8");
            log.debug("Decrypted {} bytes of data successfully", encrypted.length);
            return result;
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid Base64 in ciphertext: {}", e.getMessage());
            throw new RuntimeException("Invalid encrypted data: not valid Base64", e);
        } catch (Exception e) {
            log.error("Error decrypting data: {}", e.getMessage());
            throw new RuntimeException("Error decrypting data: " + e.getMessage(), e);
        }
    }
}
