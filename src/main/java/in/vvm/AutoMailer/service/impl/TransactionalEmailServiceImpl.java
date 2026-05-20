package in.vvm.AutoMailer.service.impl;

import in.vvm.AutoMailer.service.EmailService;
import in.vvm.AutoMailer.service.TransactionalEmailService;
import in.vvm.AutoMailer.util.MaskingUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionalEmailServiceImpl implements TransactionalEmailService {

    private final EmailService emailService;

    @Override
    public void sendWelcomeEmail(String recipient, String userName) throws MessagingException {
        String subject = "Welcome to AutoMailer 🎉";
        String body = "<p>Hi " + userName + ",</p>" +
                "<p>Welcome to AutoMailer! We’re excited to have you onboard.</p>" +
                "<p>Get started by verifying your email and exploring our features.</p>" +
                "<br><p>Cheers,<br>AutoMailer Team</p>";

        log.info("Sending welcome email to {}", MaskingUtil.maskEmail(recipient));
        emailService.sendHtmlEmail(recipient, subject, body);
        log.info("Welcome email sent successfully to {}", MaskingUtil.maskEmail(recipient));
    }

    @Override
    public void sendPasswordResetEmail(String recipient, String resetLink, String userName) throws MessagingException {
        String subject = "Password Reset Request 🔑";
        String body = (userName != null ? "<p>Hi " + userName + ",</p>" : "") +
                "<p>You requested a password reset.</p>" +
                "<p>Click <a href=\"" + resetLink + "\">here</a> to reset your password.</p>" +
                "<p>If you didn’t request this, please ignore this email.</p>";

        log.info("Sending password reset email to {}", MaskingUtil.maskEmail(recipient));
        emailService.sendHtmlEmail(recipient, subject, body);
        log.info("Password reset email sent successfully to {}", MaskingUtil.maskEmail(recipient));
    }
}


