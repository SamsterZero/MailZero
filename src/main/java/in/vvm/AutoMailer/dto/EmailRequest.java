package in.vvm.AutoMailer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record EmailRequest(
        @NotEmpty(message = "Recipients list cannot be empty")
        @Size(max = 50, message = "Cannot send to more than 50 recipients at once")
        List<@Email(message = "Invalid email format") String> recipients,

        @NotBlank(message = "Subject is required")
        @Size(max = 255, message = "Subject cannot exceed 255 characters")
        String subject,

        @NotBlank(message = "Body is required")
        @Size(max = 10000, message = "Email body cannot exceed 10000 characters")
        String body,

        @Email(message = "Invalid sender email format")
        String sender,

        boolean isHtml
) {}