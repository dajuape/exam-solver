# exam-solver

**exam-solver** is a backend-oriented microservices project designed to automate the **correction and solving of academic exams**. It simulates a production-grade distributed system and showcases real-world AI integrations using **Java 17**, **Spring Boot**, **RabbitMQ**, **OpenAI GPT-4 (Text & Vision)**, and **Nougat OCR** for scientific documents.

This project is part of my personal portfolio, aiming to demonstrate my expertise in scalable backend architectures, intelligent document processing, and modern microservice design.

---

## 🔧 Features

- Accepts exam submissions as **PDF files**, **scanned documents**, or **photos**
- Hybrid document processing pipeline:
  - PDFs parsed via **Apache PDFBox**
  - Images processed with **Tesseract OCR**
  - If OCR fails → fallback to **OpenAI GPT-4 Vision**
  - Scientific or mathematical exams → routed to **Nougat OCR** (LaTeX output)
- Advanced pre-processing:
  - Text cleaning and noise removal
  - Language detection (`es`, `en`, `fr`, `de`)
  - Exercise segmentation using multilingual patterns
- Results returned as `.pdf` or `.zip` based on format

## 🧠 Preprocessing Pipeline
```text
📄 PDF
   ↓
[ Apache PDFBox ]
   ↓
Cleaned & structured text
   ↓
Split into exercises
   ↓
Send each exercise → OpenAI (Text)

🖼️ Image
   ↓
[ Tesseract OCR ]
   ↓
✔ Cleaned text → Split & send each exercise to OpenAI (Text)
✘ Failed OCR → GPT-4 Vision (Image)

📐 Scientific / Math-heavy Exam
   ↓
[ Nougat OCR ]
   ↓
Extracted LaTeX blocks
   ↓
Send each exercise → OpenAI (LaTeX-rich prompt)
```

## 🧱 Architecture

- **Microservices Overview**:

| Service                   | Description                                                 |
|---------------------------|-------------------------------------------------------------|
| `exam-solver-gateway`     | API Gateway (Spring Cloud Gateway)                          |
| `exam-solver-preprocessor`| Handles OCR, LaTeX, text cleanup and segmentation            |
| `exam-solver-nougat`      | Scientific OCR microservice using Facebook's Nougat         |
| `exam-solver-openai`      | Stateless service for OpenAI GPT API (Text & Vision)        |
| `exam-solver-integrator`  | Orchestrates retry logic, exam states and GPT processing    |
| `exam-solver-shared`      | Contains shared enums, DTOs, and JPA entities               |

- **Event-driven architecture** powered by **RabbitMQ**

- **Fallback detection** and **retry mechanism**:
  - State flow: `PENDING` → `PROCESSING` → `FROZEN` / `FAILED` / `COMPLETED`

- **NGINX** serves as reverse proxy and load balancer (multi-instance ready)



## 💻 Tech Stack

- **Java 17**, **Spring Boot 3**, **Spring Cloud Gateway**
- **PostgreSQL**, **RabbitMQ**
- **Apache PDFBox**, **Tesseract OCR**, **Nougat OCR**
- **OpenAI GPT-4 API** (Text & Vision)
- **Docker Compose**, **NGINX**
- **OpenAPI / Swagger** for API documentation

## 🚀 Getting Started

- Clone the repository
- Run `docker-compose up`
- NGINX exposes the system at: `http://localhost:80`
- API docs available at: `http://localhost/swagger-ui.html`

## 🧠 Author

Developed by **Daniel Juape Abad**, backend engineer with 3+ years of experience in Java, Spring Boot, and microservice architectures.
Passionate about clean design, distributed systems, and real-world AI integration.

[LinkedIn](https://linkedin.com/in/danieljuape) • [Email](mailto:daniel.juape@gmail.com)


