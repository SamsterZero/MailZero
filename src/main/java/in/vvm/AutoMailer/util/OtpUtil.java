package in.vvm.AutoMailer.util;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

@Slf4j
public class OtpUtil {
    private static final SecureRandom random = new SecureRandom();

    public static String generateOtp(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // digits 0-9
        }
        String otp = sb.toString();
        log.debug("Generated OTP of length {}", length);
        return otp;
    }
}