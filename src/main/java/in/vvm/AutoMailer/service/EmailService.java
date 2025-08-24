package in.vvm.AutoMailer.service;

import in.vvm.AutoMailer.dto.EmailRequest;
import in.vvm.AutoMailer.dto.EmailResponse;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException;

    EmailResponse sendEmail(EmailRequest emailRequest);

    EmailResponse sendBulkEmail(EmailRequest emailRequest);
}
