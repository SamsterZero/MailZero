# AutoMailer Project Specifications

Welcome to the **AutoMailer (MailZero)** specifications repository. This directory contains detailed documentation outlining the architecture, API design, security implementations, and deployment workflow for the AutoMailer system.

---

## 📂 Specifications Directory Structure

Below is an overview of the specifications available in this directory:

| Specification Document | Description |
| :--- | :--- |
| 📘 [**Overview & Quick Start (README.md)**](file:///c:/Projects/Java/Springboot/MailZero/spec/README.md) | High-level overview, index, and quick start guide for developers. |
| 📡 [**API Specification (api_spec.md)**](file:///c:/Projects/Java/Springboot/MailZero/spec/api_spec.md) | Detailed HTTP API endpoints, request/response payloads, validation rules, and error handling. |
| 🧱 [**Architecture & System Design (architecture.md)**](file:///c:/Projects/Java/Springboot/MailZero/spec/architecture.md) | Component diagrams, Thread Pool configurations, OTP lifecycle management, and service hierarchy. |
| 🐳 [**Deployment & Containerization (deployment.md)**](file:///c:/Projects/Java/Springboot/MailZero/spec/deployment.md) | Multi-stage distroless Docker builds, rootless container security, and Docker Compose configuration. |

---

## 🚀 Project Overview

**AutoMailer** is a production-grade, highly secure **Spring Boot 3.5.5** service built with **Java 21** to handle transactional emails and One-Time Password (OTP) verification. 

### Core Features

1. **Secure OTP Generation & Lifecycle Management**: Generates 6-digit numeric OTPs via `SecureRandom`, maintains an ephemeral, concurrent in-memory store with automated 5-minute expiry limits, and validates attempts securely.
2. **Transactional Email Deliverability**: Supports highly personalized, HTML-formatted emails for system greetings (Welcome Emails) and access restoration (Password Reset Request flow).
3. **High-Performance Async/Bulk Processing**: Utilizes a customized `ThreadPoolTaskExecutor` (with core pool size of 10, max of 20, queue capacity of 100) and `CompletableFuture` stream processing to execute bulk email dispatches concurrently without blocking requests.
4. **Distroless & Rootless Container Security**: Deploys via Google's `gcr.io/distroless/java21-debian12` container base to eliminate shell and package manager exploits, executing under UID `1001`.

---

## 🛠 Quick Start for Spec Development

### Viewing API References
The API endpoints can be tested using standard HTTP clients (such as Postman or Bruno). For full payload schemas, validation requirements, and sample cURL requests, read the [**API Specification**](file:///c:/Projects/Java/Springboot/MailZero/spec/api_spec.md).

### Understanding System Flows
For details on how emails flow through the controller to the customized execution pool, and how OTP storage coordinates validation, consult the [**Architecture & System Design**](file:///c:/Projects/Java/Springboot/MailZero/spec/architecture.md) document.

### Running with Docker Compose
To deploy the application with proper configuration mounting and SMTP variables, refer to the [**Deployment & Containerization**](file:///c:/Projects/Java/Springboot/MailZero/spec/deployment.md) spec.
