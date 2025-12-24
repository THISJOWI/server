package uk.thisjowi.OTP.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.security.MessageDigest;

@Component
public class EncryptionUtil {

    @Value("${app.encryption.key:ThisIsADefaultKeyForDevOnly123}")
    private String secretKey;

    private static SecretKeySpec secretKeySpec;

    @PostConstruct
    public void init() {
        setKey(secretKey);
    }

    private static void setKey(String myKey) {
        MessageDigest sha = null;
        try {
            byte[] key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKeySpec = new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String strToEncrypt) {
        if (strToEncrypt == null) return null;
        if (secretKeySpec == null) {
             // Try to initialize with default if not set (e.g. unit tests or early init)
             // This is a fallback; normally Spring should init this via @PostConstruct
             setKey("ThisIsADefaultKeyForDevOnly123");
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting: " + e.toString(), e);
        }
    }

    public static String decrypt(String strToDecrypt) {
        if (strToDecrypt == null) return null;
        if (secretKeySpec == null) {
             setKey("ThisIsADefaultKeyForDevOnly123");
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            // Fallback: return original string if decryption fails (e.g. existing unencrypted data)
            return strToDecrypt;
        }
    }
}
