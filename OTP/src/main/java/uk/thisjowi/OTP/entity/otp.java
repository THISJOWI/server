package uk.thisjowi.OTP.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "otp")
public class otp {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Long userId;

    @Column(nullable = false)
    private String user;

    @Column(nullable = false)
    private String secret;

    @Column(nullable = false)
    private Long expiresAt;

    @Column(nullable = false)
    private String type; // TOTP, HOTP

    @Column(nullable = false)
    private Boolean valid;

}
