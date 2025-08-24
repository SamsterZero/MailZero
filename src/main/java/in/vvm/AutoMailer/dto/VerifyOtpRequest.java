package in.vvm.AutoMailer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpRequest {
    private String email;
    private String otp;
}