package in.vvm.AutoMailer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WelcomeEmailRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Name is required") // Ensures name is not empty
    @Size(max = 100, message = "Name cannot exceed 100 characters") // Optional length limit
    private String name;
}
