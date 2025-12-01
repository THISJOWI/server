package uk.thisjowi.OTP.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.thisjowi.OTP.entity.otp;

public interface OtpRepository extends JpaRepository<otp, Long> {
    // Métodos personalizados si se requieren
}
