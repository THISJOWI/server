package main.java.uk.thisjowi.OTP.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class OtpKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String otp;
    private LocalDateTime createdAt;

    public OtpKey() {}

    public OtpKey(String userId, String otp, LocalDateTime createdAt) {
        this.userId = userId;
        this.otp = otp;
        this.createdAt = createdAt;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
