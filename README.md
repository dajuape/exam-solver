# exam-solver

**exam-solver** is a backend-oriented project designed to simulate a real-world microservices architecture focused on exam correction automation. It showcases my experience in building distributed systems using Java, Spring Boot, RabbitMQ, PostgreSQL, and OpenAI GPT-4 Vision.

## 🔧 Features

- The platform accepts exam submissions in PDF or image format and automatically processes them through a hybrid pipeline:
- PDFs are parsed using Apache PDFBox to extract text
- Images and scanned exams are preprocessed using Tesseract OCR
- If OCR fails, OpenAI GPT-4 Vision is used as a fallback
- Once the content is extracted, it's evaluated or completed by GPT based on the selected mode (`RESOLVE` or `CORRECT`)
- Gateway traffic is routed through NGINX to simulate load balancing and production-grade reverse proxy setup

## 🧱 Architecture

- Microservices: Gateway, Preprocessor, Integrator, OpenAI Service
- API access is routed through **NGINX**, which acts as a load balancer and reverse proxy to the Gateway
- The Gateway is implemented using **Spring Cloud Gateway**, providing routing, filtering, and basic API management
- Message-driven processing via RabbitMQ decouples the ingestion from the AI processing pipeline
- Retry mechanism with state transitions (`PENDING`, `PROCESSING`, `FROZEN`, `FAILED`, `COMPLETED`)
- Results returned in `.txt` or `.zip` format

## 💻 Tech Stack

- **Java 17**, **Spring Boot 3**, **Spring Cloud Gateway**
- **PostgreSQL**, **RabbitMQ**, **Tesseract OCR**, **Apache PDFBox**
- **OpenAI GPT-4 API (Text + Vision)**
- **NGINX** (as reverse proxy + load balancer)
- **Docker Compose** (multi-container orchestration)
- **OpenAPI / Swagger** (API docs)

## 🚀 Getting Started

- Clone the repository
- Run `docker-compose up`
- NGINX exposes the system at: `http://localhost:80`
- API docs available at: `http://localhost/swagger-ui.html`

## 📁 Microservices

- `exam-solver-gateway` — Spring Cloud Gateway + controller endpoints
- `exam-solver-preprocessor` — Extracts text from PDF or image
- `exam-solver-integrator` — Manages status flow and retries
- `exam-solver-openai` — Stateless wrapper for OpenAI API
- `exam-solver-shared` — Shared entities, DTOs, and enums

## 🧠 Author

Developed by Daniel Juape Abad, backend engineer with 3+ years of experience.  
Focused on scalable, modular architecture and real-world AI integrations.

