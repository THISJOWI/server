package com.thisjowi.otp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.thisjowi.otp.entity.otp;
import java.util.List;

public interface OtpRepository extends JpaRepository<otp, Long> {
    List<otp> findByUserId(Long userId);
}
