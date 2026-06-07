# Talk To Docs

A RAG (Retrieval-Augmented Generation) application that lets you upload documents and chat with them
using AI.

## Features

- Upload `.md`, `.txt`, and `.pdf` documents
- Embedding into a vector store (PostgreSQL + pgvector)
- Chat interface powered by AI (Ollama)

## Tech Stack

- **Backend:** Java 25, Spring Boot, Spring AI
- **Frontend:** TypeScript, Vite, React
- **Infrastructure:** Ollama, PostgreSQL

## Getting Started

Prerequisites:
- Java 25
- Node.js
- pnpm
- Docker
- Ollama (running locally)

1. Start the PostgreSQL container:

```sh
docker compose up -d
```

2. Run the backend:

```sh
cd ./docs-service
./gradlew bootRun
```

3. Run the frontend:

```sh
cd ./web-app
pnpm install
pnpm dev
```

Open `http://localhost:5173` and sign in with:

```
test@example.com
password
```

## License

This project is licensed under the MIT License.
