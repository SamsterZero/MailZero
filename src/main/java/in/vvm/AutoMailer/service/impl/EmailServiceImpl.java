package in.vvm.AutoMailer.service.impl;

import in.vvm.AutoMailer.dto.EmailRequest;
import in.vvm.AutoMailer.dto.EmailResponse;
import in.vvm.AutoMailer.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {


    private final JavaMailSender mailSender;
    private final ExecutorService executorService;

    @Value("${spring.mail.username}")
    private String defaultSender;

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        log.debug("Preparing HTML email to={} subject={}", to, subject);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // `true` → HTML enabled
        mailSender.send(message);
        log.info("HTML email sent successfully to {}", to);
    }

    @Override
    public EmailResponse sendEmail(EmailRequest emailRequest) {
        log.info("Starting single email sending process. Recipients={}", emailRequest.recipients());
        List<String> successfulRecipients = new ArrayList<>();
        List<String> failedRecipients = new ArrayList<>();

        for (String recipient : emailRequest.recipients()) {

            try {
                log.debug("Sending single email to {}", recipient);
                sendSingleEmail(recipient, emailRequest);
                successfulRecipients.add(recipient);
                log.info("Email sent successfully to: {}", recipient);
            } catch (Exception e) {
                failedRecipients.add(recipient);
                log.error("Failed to send email to: {}", recipient, e);
            }
        }

        boolean success = !successfulRecipients.isEmpty();
        String message = String.format("Email sending completed. Success: %d, Failed: %d", successfulRecipients.size(), failedRecipients.size());

        return new EmailResponse(message, success, successfulRecipients, failedRecipients, LocalDateTime.now());
    }

    @Override
    public EmailResponse sendBulkEmail(EmailRequest emailRequest) {
        log.info("Starting bulk email sending. Recipients={}", emailRequest.recipients().size());

        List<String> successfulRecipients = new ArrayList<>();
        List<String> failedRecipients = new ArrayList<>();

        // Send emails asynchronously for better performance
        List<CompletableFuture<Void>> futures = emailRequest.recipients().stream().map(recipient -> CompletableFuture.runAsync(() -> {
            try {
                log.debug("Sending bulk email to {}", recipient);
                sendSingleEmail(recipient, emailRequest);
                synchronized (successfulRecipients) {
                    successfulRecipients.add(recipient);
                }
                log.info("Bulk email sent successfully to: {}", recipient);
            } catch (Exception e) {
                synchronized (failedRecipients) {
                    failedRecipients.add(recipient);
                }
                log.error("Failed to send bulk email to: {}", recipient, e);
            }
        }, executorService)).toList();

        // Wait for all emails to be processed
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        boolean success = !successfulRecipients.isEmpty();
        String message = String.format("Bulk email sending completed. Success: %d, Failed: %d", successfulRecipients.size(), failedRecipients.size());

        return new EmailResponse(message, success, successfulRecipients, failedRecipients, LocalDateTime.now());
    }

    private void sendSingleEmail(String recipient, EmailRequest emailRequest) throws MessagingException {
        log.debug("Preparing email to {} with subject={}", recipient, emailRequest.subject());

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String senderEmail = emailRequest.sender() != null ? emailRequest.sender() : defaultSender;
        if (emailRequest.sender() == null) {
            log.warn("Sender not provided. Falling back to default sender={}", defaultSender);
        }

        helper.setFrom(senderEmail);
        helper.setTo(recipient);
        helper.setSubject(emailRequest.subject());
        helper.setText(emailRequest.body(), emailRequest.isHtml());

        mailSender.send(message);
        log.debug("Email dispatched to mail server for recipient={}", recipient);
    }
}
