# docs-service

Document-aware AI chatbot backend — upload documents, chat with an AI that answers only from your content.

## Tech Stack

- **Java 25** + **Spring Boot 4** + **Spring AI 2**
- **PostgreSQL** + **pgvector** (vector store for RAG)
- **STOMP / WebSocket** (real-time notifications)
- **Ollama** — chat model: `qwen2.5`, embedding model: `nomic-embed-text`
- **Gradle**

## Structure

```
src/main/java/com/mikhailkarpov/docs/
├── ai/            # AI, RAG pipeline, document indexing
├── auth/          # Users and authorization
├── chat/          # Conversations and messages
├── config/        # Configuration
├── documents/     # Documents
└── notifications/ # Notifications
```

## Getting Started

**Prerequisites:** Java 25, Docker (PostgreSQL with pgvector + Ollama)

```bash
./gradlew bootRun   # start on http://localhost:8080
./gradlew test      # run tests (Testcontainers spins up PostgreSQL automatically)
./gradlew build     # compile + test + package JAR
```
