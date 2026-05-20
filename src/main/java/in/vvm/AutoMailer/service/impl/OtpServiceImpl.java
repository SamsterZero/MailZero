package in.vvm.AutoMailer.service.impl;

import in.vvm.AutoMailer.service.OtpService;
import in.vvm.AutoMailer.util.MaskingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class OtpServiceImpl implements OtpService {

    // Stores email → OTP + expiry
    private final ConcurrentHashMap<String, OtpData> otpStorage = new ConcurrentHashMap<>();

    @Override
    public void saveOtp(String email, String otp) {
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);
        otpStorage.put(email, new OtpData(otp, expiryTime)); // expires in 5 min
        log.info("Saved OTP for email={} with expiry={}", MaskingUtil.maskEmail(email), expiryTime);
        log.debug("OTP stored for email={}", MaskingUtil.maskEmail(email));
    }

    @Override
    public boolean validateOtp(String email, String otp) {
        OtpData otpData = otpStorage.get(email);
        if (otpData == null) {
            log.warn("No OTP found for email={}", MaskingUtil.maskEmail(email));
            return false;
        }

        if (otpData.expiry().isBefore(LocalDateTime.now())) {
            log.warn("Expired OTP for email={}. Removing from storage.", MaskingUtil.maskEmail(email));
            otpStorage.remove(email); // remove expired OTP
            return false;
        }

        if (otpData.otp().equals(otp)) {
            log.info("OTP validation success for email={}", MaskingUtil.maskEmail(email));
            otpStorage.remove(email); // Invalidate OTP upon successful verification to prevent replay attacks
            return true;
        } else {
            log.warn("OTP validation failed for email={}", MaskingUtil.maskEmail(email));
            return false;
        }
    }

    private record OtpData(String otp, LocalDateTime expiry) {}
}

