package in.vvm.AutoMailer.dto;

import java.time.LocalDateTime;
import java.util.List;

public record EmailResponse(
        String message,
        boolean success,
        List<String> successfulRecipients,
        List<String> failedRecipients,
        LocalDateTime timestamp
) {}