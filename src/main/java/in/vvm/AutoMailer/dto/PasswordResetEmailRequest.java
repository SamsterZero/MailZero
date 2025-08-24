package in.vvm.AutoMailer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetEmailRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Reset token is required")
    @Size(min = 6, max = 64, message = "Reset token must be between 6 and 64 characters")
    private String resetToken;

    @NotBlank(message = "Reset link is required")
    private String resetLink; // full URL provided by client

    private String name; // optional for personalization
}