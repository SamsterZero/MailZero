package in.vvm.AutoMailer.service;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public interface OtpEmailService {

    void sendOtp(String recipientEmail, String otp)  throws MessagingException;
}
