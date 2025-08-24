package in.vvm.AutoMailer.service;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public interface TransactionalEmailService {

    void sendWelcomeEmail(String recipient, String userName) throws MessagingException;

    void sendPasswordResetEmail(String recipient, String resetLink, String name) throws MessagingException;
}

