package uk.thisjowi.OTP.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.thisjowi.OTP.entity.otp;
import uk.thisjowi.OTP.service.OtpService;
import uk.thisjowi.OTP.service.QrService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/otp")
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
    public List<otp> getAllOtps() {
        return otpService.getAllOtps();
    }

    @GetMapping("/{id}")
    public ResponseEntity<otp> getOtp(@PathVariable Long id) {
        Optional<otp> o = otpService.getOtp(id);
        return o.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public otp createOtp(@RequestParam String user, @RequestParam String type, @RequestParam(defaultValue = "300") long validitySeconds) {
        return otpService.createOtp(user, type, validitySeconds);
    }

    @PutMapping("/{id}")
    public otp updateOtp(@PathVariable Long id, @RequestBody otp updatedOtp) {
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
}
