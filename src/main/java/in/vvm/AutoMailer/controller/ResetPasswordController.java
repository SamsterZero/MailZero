package in.vvm.AutoMailer.controller;

import in.vvm.AutoMailer.dto.PasswordResetEmailRequest;
import in.vvm.AutoMailer.service.TransactionalEmailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ResetPasswordController {

    private final TransactionalEmailService transactionalEmailService;

    @PostMapping("/reset-password")
    public ResponseEntity<String> sendPasswordResetEmail(
            @Valid @RequestBody PasswordResetEmailRequest request
    ) {
        try {
            log.info("Received request to send password reset email to {}", request.getEmail());
            String fullResetLink = request.getResetLink() + "?token=" + request.getResetToken();
            transactionalEmailService.sendPasswordResetEmail(
                    request.getEmail(),
                    fullResetLink,
                    request.getName()
            );
            return ResponseEntity.ok("Password reset email sent successfully to " + request.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to {}", request.getEmail(), e);
            return ResponseEntity.internalServerError()
                    .body("Failed to send password reset email: " + e.getMessage());
        }
    }
}