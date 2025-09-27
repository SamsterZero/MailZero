package in.vvm.AutoMailer.controller;

import in.vvm.AutoMailer.dto.WelcomeEmailRequest;
import in.vvm.AutoMailer.service.TransactionalEmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WelcomeController {

    private final TransactionalEmailService transactionalEmailService;

    @PostMapping("/welcome")
    public ResponseEntity<String> sendWelcome(@RequestBody WelcomeEmailRequest request) {
        try {
            transactionalEmailService.sendWelcomeEmail(request.getEmail(), request.getName());
            return ResponseEntity.ok("Welcome email sent!");
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError().body("Failed to send email: " + e.getMessage());
        }
    }
}