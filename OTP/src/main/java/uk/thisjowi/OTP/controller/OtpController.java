package uk.thisjowi.OTP.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.thisjowi.OTP.entity.otp;
import uk.thisjowi.OTP.service.OtpService;
import uk.thisjowi.OTP.service.QrService;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private QrService qrService;
    @PostMapping("/decode-qr")
    public ResponseEntity<String> decodeQr(@RequestBody String base64Image) {
        try {
            String qrText = qrService.decodeQrFromBase64(base64Image);
            return ResponseEntity.ok(qrText);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error al decodificar QR: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<otp>> getAllOtps(@RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(otpService.getAllOtps(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<otp> getOtp(@PathVariable Long id) {
        Optional<otp> o = otpService.getOtp(id);
        return o.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<otp> createOtp(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody CreateOtpRequest request) {
        
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        if (request.secret != null && !request.secret.isEmpty()) {
             // Decrypt secret
             request.secret = decrypt(request.secret);
             
             // Log masked secret for debugging
             String masked = request.secret.length() > 4 ? "..." + request.secret.substring(request.secret.length() - 4) : "***";
             System.out.println("Received createOtp request for user " + userId + " with secret: " + masked);
        } else {
             System.out.println("Received createOtp request for user " + userId + " without secret");
        }
        
        return ResponseEntity.ok(otpService.createOtp(userId, request.name, request.type, request.secret, request.issuer, request.digits, request.period, request.algorithm));
    }

    public static class CreateOtpRequest {
        public String name;
        public String issuer;
        public String secret;
        public Integer digits;
        public Integer period;
        public String algorithm;
        public String type;
    }

    @PutMapping("/{id}")
    public otp updateOtp(@PathVariable Long id, @RequestBody otp updatedOtp) {
        if (updatedOtp.getSecret() != null && !updatedOtp.getSecret().isEmpty()) {
            updatedOtp.setSecret(decrypt(updatedOtp.getSecret()));
        }
        return otpService.updateOtp(id, updatedOtp);
    }

    @DeleteMapping("/{id}")
    public void deleteOtp(@PathVariable Long id) {
        otpService.deleteOtp(id);
    }

    @PostMapping("/{id}/validate")
    public ResponseEntity<String> validateOtp(@PathVariable Long id, @RequestParam String code) {
        boolean valid = otpService.validateOtp(id, code);
        return valid ? ResponseEntity.ok("OTP válido") : ResponseEntity.status(400).body("OTP inválido o expirado");
    }

    // TODO: Move to environment variable
    private static final String ENCRYPTION_KEY = "ThisJowiSecureKeyForOtpEncryption2025!";

    private String decrypt(String encryptedText) {
        try {
            // Check if it looks like base64 (simple check)
            if (!encryptedText.matches("^[A-Za-z0-9+/=]+$")) {
                return encryptedText;
            }
            
            byte[] combined = Base64.getDecoder().decode(encryptedText);
            
            // Minimum length check (IV is 16 bytes)
            if (combined.length < 16) {
                return encryptedText;
            }
            
            // Extract IV (first 16 bytes)
            byte[] iv = new byte[16];
            System.arraycopy(combined, 0, iv, 0, 16);
            
            // Extract ciphertext
            byte[] ciphertext = new byte[combined.length - 16];
            System.arraycopy(combined, 16, ciphertext, 0, ciphertext.length);
            
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec keySpec = new SecretKeySpec(ENCRYPTION_KEY.substring(0, 32).getBytes(StandardCharsets.UTF_8), "AES");
            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            
            byte[] original = cipher.doFinal(ciphertext);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Error decrypting secret: " + e.getMessage());
            // If decryption fails, assume it's plain text (backward compatibility or error)
            return encryptedText;
        }
    }

    private Long extractUserIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                String[] parts = token.split("\\.");
                if (parts.length == 3) {
                    String payload = parts[1];
                    int padding = 4 - (payload.length() % 4);
                    if (padding != 4) {
                        payload = payload + "=".repeat(padding);
                    }
                    byte[] decodedBytes = java.util.Base64.getUrlDecoder().decode(payload);
                    String decodedString = new String(decodedBytes);
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(decodedString);
                    if (node.has("sub")) {
                        return Long.parseLong(node.get("sub").asText());
                    }
                }
            } catch (Exception e) {
                // Log error
            }
        }
        return null;
    }
}
