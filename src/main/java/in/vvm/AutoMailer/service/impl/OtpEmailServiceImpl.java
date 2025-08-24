package in.vvm.AutoMailer.service.impl;

import in.vvm.AutoMailer.service.EmailService;
import in.vvm.AutoMailer.service.OtpEmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpEmailServiceImpl implements OtpEmailService {

    private final EmailService emailService;

    @Override
    public void sendOtp(String recipientEmail, String otp) throws MessagingException {
        log.info("Preparing to send OTP email to recipient={}", maskEmail(recipientEmail));
        log.debug("OTP details -> recipient={}, subject={}, otp={}",
                recipientEmail, "Verify OTP", otp);

        String subject = "Verify OTP";
        String body = "<p>Your OTP is: <b>" + otp + "</b></p>" +
                "<p>It is valid for 5 minutes.</p>";

        try {
            emailService.sendHtmlEmail(recipientEmail, subject, body);
            log.info("OTP email successfully sent to {}", maskEmail(recipientEmail));
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}: {}", recipientEmail, e.getMessage(), e);
            throw e; // rethrow so controller can handle it
        }
    }
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        String local = parts[0];
        String domain = parts[1];
        if (local.length() <= 2) return "***@" + domain;
        return local.charAt(0) + "***" + local.charAt(local.length() - 1) + "@" + domain;
    }
}