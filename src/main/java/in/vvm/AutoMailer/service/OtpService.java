package in.vvm.AutoMailer.service;

public interface OtpService {

    void saveOtp(String email, String otp);

    boolean validateOtp(String email, String otp);

}
