# Talk To Docs

A RAG (Retrieval-Augmented Generation) application that lets you upload documents and chat with them
using AI.

Built with a Spring Boot backend and a modern web UI, it combines document ingestion, vector search,
and conversational AI.

## Features
- Upload .md and .txt documents
- Automatic chunking & embedding into a vector store
- Semantic search over your documents 
- Chat interface powered by AI (via Ollama)
- Metadata tracking for uploaded files 

## Tech Stack
Backend: Java 25, Gradle, Spring Boot, Spring AI
Frontend: Vite
AI Runtime: Ollama (local LLM)

## Getting Started

Make sure you have installed:
- Java 25
- Gradle
- Node.js
- pnpm
- Ollama (running locally)


1. Start the Ollama
2. Run Backend

```sh
cd docs-service
./gradlew bootRun
```

Backend will be available at 'http://localhost:8080'

3. Run Frontend

```sh
cd web-app
pnpm install
pnpm dev
```

The frontend will be available at `http://localhost:5173`

Use the following to sign in: 
```
user@example.com
password
```

## License

This project is licensed under the MIT License.
