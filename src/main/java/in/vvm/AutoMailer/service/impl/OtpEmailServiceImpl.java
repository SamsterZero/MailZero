package in.vvm.AutoMailer.service.impl;

import in.vvm.AutoMailer.service.EmailService;
import in.vvm.AutoMailer.service.OtpEmailService;
import in.vvm.AutoMailer.util.MaskingUtil;
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
        log.info("Preparing to send OTP email to recipient={}", MaskingUtil.maskEmail(recipientEmail));
        log.debug("OTP details -> recipient={}, subject={}",
                MaskingUtil.maskEmail(recipientEmail), "Verify OTP");

        String subject = "Verify OTP";
        String body = "<p>Your OTP is: <b>" + otp + "</b></p>" +
                "<p>It is valid for 5 minutes.</p>";

        try {
            emailService.sendHtmlEmail(recipientEmail, subject, body);
            log.info("OTP email successfully sent to {}", MaskingUtil.maskEmail(recipientEmail));
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}: {}", MaskingUtil.maskEmail(recipientEmail), e.getMessage(), e);
            throw e; // rethrow so controller can handle it
        }
    }
}