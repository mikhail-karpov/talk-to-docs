# Talk To Docs

A RAG (Retrieval-Augmented Generation) application that lets you upload documents and chat with them
using AI.

Built with a Spring Boot backend and a modern web UI, it combines document ingestion, vector search,
and conversational AI.

## Features
- Upload .md, .txt and .pdf documents
- Embedding into a vector store (PostgreSQL)
- Chat interface powered by AI (Ollama)

## Tech Stack
- Backend: Java 25, Gradle, Spring Boot, Spring AI
- Frontend: Typescript, Vite, React
- Infrastructure: Ollama, PostgreSQL

## Getting Started

Make sure you have installed:
- Java 25
- Gradle
- Node.js
- pnpm
- Ollama (running locally)
- Docker Compose

1. Run the Ollama locally

2. Start the PostgreSQL container

```sh
docker compose up -d
```

3. Run Backend

```sh
cd ./docs-service
./gradlew bootRun
```

4. Run Frontend

```sh
cd ./web-app
pnpm install
pnpm dev
```

Open the browser at `http://localhost:5173` and use the following to sign in: 
```
test@example.com
password
```

## License

This project is licensed under the MIT License.
