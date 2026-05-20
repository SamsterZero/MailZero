# 🧱 Architecture & System Design

This document details the software architecture, component hierarchies, thread pooling strategies, and data structures utilized in the **AutoMailer** (MailZero) system.

---

## 🏗️ Architecture Design Overview

AutoMailer is built as a layered, decoupled service leveraging the **Spring Boot** framework. It adheres to clean architecture principles by separating the REST controllers (Presentation), Data Transfer Objects (Payloads), Business Services (Domain Logic), and Infrastructure (SMTP Client, Multithreading, In-Memory Storage).

```mermaid
graph TD
    classDef controller fill:#d4ebf2,stroke:#333,stroke-width:2px;
    classDef dto fill:#fff2cc,stroke:#333,stroke-width:1px,stroke-dasharray: 5 5;
    classDef service fill:#d5e8d4,stroke:#333,stroke-width:2px;
    classDef infra fill:#f8cecc,stroke:#333,stroke-width:2px;
    classDef util fill:#e1d5e7,stroke:#333,stroke-width:1px;

    %% Presentation Layer
    subgraph Presentation ["Presentation Layer"]
        C1[OtpController]:::controller
        C2[ResetPasswordController]:::controller
        C3[WelcomeController]:::controller
        EH[GlobalExceptionHandler]:::controller
    end

    %% DTO Layer
    subgraph DataTransfer ["Data Transfer Objects (DTOs)"]
        D1[GenerateOtpRequest]:::dto
        D2[VerifyOtpRequest]:::dto
        D3[WelcomeEmailRequest]:::dto
        D4[PasswordResetEmailRequest]:::dto
        D5[EmailRequest]:::dto
        D6[EmailResponse]:::dto
    end

    %% Domain Service Layer
    subgraph Domain ["Business Domain Layer"]
        S1[OtpService]:::service
        S2[OtpEmailService]:::service
        S3[TransactionalEmailService]:::service
        S4[EmailService]:::service
    end

    %% Infrastructure & Core Integrations
    subgraph Infrastructure ["Infrastructure & Core"]
        MS[JavaMailSender]:::infra
        TP[ThreadPoolTaskExecutor]:::infra
        IM[ConcurrentHashMap OTP Storage]:::infra
        U1[OtpUtil - SecureRandom]:::util
    end

    %% Dependency & Flow Links
    C1 --> D1
    C1 --> D2
    C2 --> D4
    C3 --> D3
    
    C1 --> S1
    C1 --> S2
    C2 --> S3
    C3 --> S3
    
    S2 --> S4
    S3 --> S4
    
    S1 --> IM
    S4 --> MS
    S4 --> TP
    S2 --> U1
    
    EH -.-> C1
    EH -.-> C2
    EH -.-> C3
```

---

## ⚡ Thread Pool Configuration & Async Processing

To prevent incoming HTTP request threads from blocking during SMTP operations (which are network-bound and notoriously slow), AutoMailer supports asynchronous execution pipelines.

### ⚙️ Customized Executor Configuration (`EmailConfig.java`)
The application defines a custom `ExecutorService` bean named `taskExecutor` that configures a customized `ThreadPoolTaskExecutor`:

*   **Core Pool Size:** `10` threads (always kept active and running).
*   **Maximum Pool Size:** `20` threads (the upper boundary of concurrent threads under heavy load).
*   **Queue Capacity:** `100` tasks (excess requests are buffered here when all core threads are busy).
*   **Thread Prefix:** `email-exec-` (for distinct trace logging and simplified thread auditing).
*   **Annotation Drivers:** `@EnableAsync` configures the Spring context to support asynchronous method boundary executions.

### 💨 Asynchronous Bulk Processing Pipeline
For processing bulk mailing requests efficiently, `EmailServiceImpl` executes tasks asynchronously using `CompletableFuture.runAsync()` backed by the custom execution pool.

```mermaid
sequenceDiagram
    autonumber
    participant App as EmailServiceImpl
    participant Pool as Custom Executor (taskExecutor)
    participant Worker as Worker Thread
    participant SMTP as SMTP Mail Server

    App->>App: sendBulkEmail(EmailRequest)
    Note over App: Split recipients list into individual task streams
    
    loop For each recipient
        App->>Pool: CompletableFuture.runAsync(sendSingleEmail, taskExecutor)
        activate Pool
        Pool-->>App: Return CompletableFuture<Void>
        deactivate Pool
    end
    
    activate Pool
    Note over Pool: Threads in pool process tasks concurrently
    Pool->>Worker: Dispatch task
    activate Worker
    Worker->>SMTP: SMTP transaction for Recipient X
    activate SMTP
    SMTP-->>Worker: SMTP 250 OK
    deactivate SMTP
    Worker-->>Pool: Task Complete
    deactivate Worker
    deactivate Pool
    
    App->>App: CompletableFuture.allOf(futures).join()
    Note over App: Block caller thread until all worker threads complete
    App-->>App: Compile EmailResponse (Success/Fail metrics)
```

---

## 🔐 OTP Lifecycle & In-Memory Storage

AutoMailer manages OTP lifecycles entirely in-memory using an ephemeral cache pattern. This avoids database overhead while maintaining strict concurrency safety.

### 💾 Data Structure: `OtpServiceImpl`
OTP cache records are kept in a concurrent-safe storage map:
```java
private final ConcurrentHashMap<String, OtpData> otpStorage = new ConcurrentHashMap<>();
```
Each entry maps an `email` (key) to an instance of `OtpData`, which is a lightweight, immutable Java `record`:
```java
private record OtpData(String otp, LocalDateTime expiry) {}
```

### ⏳ OTP Lifecycle States & Transitions
An OTP transition has three key stages: Generation, Ephemeral Storage, and Verification/Expiry Cleanup.

```mermaid
stateDiagram-v2
    [*] --> Generated : Client requests OTP via /api/otp/generateOTP
    
    state Generated {
        [*] --> SecureRandomDigits : OtpUtil.generateOtp(6)
        SecureRandomDigits --> FormattedHTML : OtpEmailService prepares body
    }

    Generated --> SavedInMemory : saveOtp(email, otp)
    note right of SavedInMemory : Expiry is set to LocalTime + 5 Minutes

    state SavedInMemory {
        [*] --> EphemeralState
        EphemeralState --> Active : Current Time < Expiry
        EphemeralState --> Expired : Current Time >= Expiry
    }

    Active --> Validated : validateOtp(email, correctOtp)
    note left of Validated : OTP matches & valid. Returns true.

    Active --> ValidationFailed : validateOtp(email, incorrectOtp)
    note left of ValidationFailed : OTP mismatch. Returns false. Remains in memory.

    Expired --> LazilyCleaned : validateOtp(email, anyOtp)
    note right of LazilyCleaned : otpStorage.remove(email) triggered. Returns false.

    Validated --> [*]
    LazilyCleaned --> [*]
```

### 🛡️ Secure OTP Generation
OTP digits are compiled using a secure random generator inside `OtpUtil.java`:
```java
private static final SecureRandom random = new SecureRandom();
```
By utilizing `SecureRandom` instead of `java.util.Random`, generated OTPs are cryptographically strong and resistant to sequence predictability attacks.

---

## 🕵️ Logs & PII (Personally Identifiable Information) Safeguards

To conform to standard privacy mandates (e.g., GDPR, CCPA) and security best practices, the system prevents plaintext email addresses from leaking into stdout or files.

### 🛡️ Email Masking Helper
The `OtpEmailServiceImpl` implements a masking method (`maskEmail`) before generating `INFO` level statements:

*   **Logic:**
    *   If the email string is null or does not contain `@`, it is returned unchanged.
    *   If the local part (before `@`) is **2 characters or fewer**, it is entirely replaced with asterisks (e.g., `***@domain.com`).
    *   If the local part is **longer than 2 characters**, the system preserves only the first and last character of the local part, replacing all intermediate characters with `***` (e.g., `j***e@domain.com`).

*   **Example Output:**
    *   `vne.vvm@gmail.com` ➔ `v***m@gmail.com`
    *   `me@vvm.in` ➔ `***@vvm.in`
