package com.thisjowi.otp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.thisjowi.otp.model.OtpKey;

import java.util.Optional;

public interface OtpKeyRepository extends JpaRepository<OtpKey, Long> {
    Optional<OtpKey> findByUserId(String userId);
}
