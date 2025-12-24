package uk.thisjowi.OTP.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.thisjowi.OTP.entity.otp;
import java.util.List;

public interface OtpRepository extends JpaRepository<otp, Long> {
    List<otp> findByUserId(Long userId);
}
