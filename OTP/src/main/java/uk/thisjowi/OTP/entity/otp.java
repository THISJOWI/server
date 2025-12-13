package uk.thisjowi.OTP.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.thisjowi.OTP.converter.*;

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

    @Convert(converter = StringCryptoConverter.class)
    @Column(nullable = false)
    private String username;

    @Convert(converter = StringCryptoConverter.class)
    @Column(nullable = false)
    private String secret;

    @Convert(converter = LongCryptoConverter.class)
    @Column(nullable = false)
    private Long expiresAt;

    @Convert(converter = StringCryptoConverter.class)
    @Column(nullable = false)
    private String type; // TOTP, HOTP

    @Column(nullable = true)
    private String issuer;

    @Convert(converter = IntegerCryptoConverter.class)
    @Column(nullable = true)
    private Integer digits;

    @Column(nullable = true)
    private Integer period;

    @Convert(converter = StringCryptoConverter.class)
    @Column(nullable = true)
    private String algorithm;

    @Convert(converter = BooleanCryptoConverter.class)
    @Column(nullable = false)
    private Boolean valid;

}
