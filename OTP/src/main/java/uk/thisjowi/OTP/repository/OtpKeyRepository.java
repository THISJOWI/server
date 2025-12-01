package uk.thisjowi.OTP.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.thisjowi.OTP.model.OtpKey;

import java.util.Optional;

public interface OtpKeyRepository extends JpaRepository<OtpKey, Long> {
    Optional<OtpKey> findByUserId(String userId);
}
