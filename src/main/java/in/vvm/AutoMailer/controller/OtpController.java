package in.vvm.AutoMailer.controller;

import in.vvm.AutoMailer.dto.VerifyOtpRequest;
import in.vvm.AutoMailer.service.OtpEmailService;
import in.vvm.AutoMailer.service.OtpService;
import in.vvm.AutoMailer.util.OtpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;
    private final OtpEmailService otpEmailService;

    @GetMapping("/generateOTP")
    public ResponseEntity<String> generateOTP(
            @RequestParam String email,
            @RequestParam(required = false) String phoneNumber
    ) {
        log.trace("TRACE: Entered generateOTP with email={} and phoneNumber={}", email, phoneNumber);
        log.debug("DEBUG: Starting OTP generation process for {}", email);

        String otp = OtpUtil.generateOtp(6);
        log.info("INFO: Generated OTP for {}: {}", email, otp);

        otpService.saveOtp(email, otp);
        log.debug("DEBUG: Saved OTP for {}", email);

        try {
            otpEmailService.sendOtp(email, otp);
            log.info("INFO: OTP sent to {}", email);
        } catch (Exception e) {
            log.error("ERROR: Failed to send OTP to {}. Reason: {}", email, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to send OTP: " + e.getMessage());
        }

        log.trace("TRACE: Exiting generateOTP for {}", email);
        return ResponseEntity.ok("OTP sent successfully to " + email);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest request) {
        log.trace("TRACE: Entered verifyOtp with email={} and otp={}", request.getEmail(), request.getOtp());
        log.debug("DEBUG: Verifying OTP for {}", request.getEmail());

        boolean valid = otpService.validateOtp(request.getEmail(), request.getOtp());

        if (valid) {
            log.info("INFO: OTP verified successfully for {}", request.getEmail());
            return ResponseEntity.ok("OTP verified successfully");
        } else {
            log.warn("WARN: Invalid or expired OTP for {}", request.getEmail());
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }
    }
}