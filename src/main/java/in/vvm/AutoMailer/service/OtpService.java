package in.vvm.AutoMailer.service;

import org.springframework.stereotype.Service;

@Service
public interface OtpService {

    void saveOtp(String email, String otp);

    boolean validateOtp(String email, String otp);

}
