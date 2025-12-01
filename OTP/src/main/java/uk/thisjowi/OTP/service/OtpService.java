package uk.thisjowi.OTP.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.thisjowi.OTP.entity.otp;
import uk.thisjowi.OTP.repository.OtpRepository;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class OtpService {
    @Autowired
    private OtpRepository otpRepository;

    public List<otp> getAllOtps() {
        return otpRepository.findAll();
    }

    public Optional<otp> getOtp(Long id) {
        return otpRepository.findById(id);
    }

    public otp createOtp(String user, String type, long validitySeconds) {
        otp o = new otp();
        o.setUser(user);
        o.setType(type);
        o.setValid(true);
        o.setExpiresAt(Instant.now().getEpochSecond() + validitySeconds);
        o.setSecret(generateSecret());
        return otpRepository.save(o);
    }

    public otp updateOtp(Long id, otp updatedOtp) {
        updatedOtp.setId(id);
        return otpRepository.save(updatedOtp);
    }

    public void deleteOtp(Long id) {
        otpRepository.deleteById(id);
    }

    public boolean validateOtp(Long id, String code) {
        Optional<otp> o = otpRepository.findById(id);
        if (o.isPresent() && o.get().getValid() && o.get().getExpiresAt() > Instant.now().getEpochSecond()) {
            // Aquí deberías implementar la validación TOTP/HOTP real
            return o.get().getSecret().equals(code);
        }
        return false;
    }

    private String generateSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
