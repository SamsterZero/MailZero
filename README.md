# 📧 Spring Boot Auto Mailer with OTP Verification

A **Spring Boot application** that allows you to send emails with OTPs for verification purposes.  
This can be used to verify user email addresses during signup, password reset, or any secure action. 🔐

---

## 🚀 Features
- 📩 Send OTPs via email for verification
- ✅ Verify OTPs with expiration time
- 🛠 Configurable SMTP settings
- 🗂 Clean architecture with logs for debugging
- 🔒 Secure handling of OTP storage (in-memory / database support)

---

## 🧱 Tech Stack

- ☕ Java 21, Spring Boot 3.5.5
- 📬 Jakarta Mail via JavaMailSender
- 🧰 Lombok (@Slf4j, @RequiredArgsConstructor)
- 🐳 Docker / Docker Compose
- 🦾 Distroless base image → minimal, secure runtime (no shell, no package manager)
- 👨‍💻 Rootless container execution → runs as non-root user for extra security

---

## 📡 API Endpoints
### 1️⃣ Send OTP
**Get** `/send-otp`
Request Body:
```json
{
  "email": "user@example.com"
}
```
Response: "OTP sent successfully!"

### 2️⃣ Verify OTP

**POST** `/verify`
Request Body:
```json
{
  "email": "user@example.com",
  "otp": "123456"
}
```


Response (if valid): "OTP verified successfully!"


Response (if invalid/expired): "Invalid or expired OTP!"

---

## 🛡 Security Notes

- Store secrets like SMTP password in .env (not in application.properties).
- If using docker-compose, mount .env instead of hardcoding secrets.
- Never commit .env or application.properties with real credentials to GitHub.
