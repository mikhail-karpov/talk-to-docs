# Talk-To-Docs Web Application

React frontend for the Talk-To-Docs RAG application — upload documents and chat with an AI that answers from your content.

## Tech Stack

- **React 19** + **TypeScript** + **Vite**
- **React Router**
- **Tailwind CSS v4** + **shadcn/ui**
- **TanStack Query** (server state), **Zustand** (client state)
- **axios**, **STOMP / SockJS** (WebSocket)

## Structure

```
src/
├── app/         # Routes, root component, provider, router config
├── assets/      # Static files
├── components/  # Shared components
├── features/    # Feature modules (auth, chat, documents, websocket)
├── hooks/       # Shared hooks
├── lib/         # Preconfigured libraries
├── stores/      # Global Zustand stores
├── types/       # Shared TypeScript types
└── utils/       # Shared utility functions
```

## Commands

```bash
pnpm dev        # Start Vite dev server (http://localhost:5173)
pnpm build      # Type-check + production build
pnpm lint       # ESLint
pnpm format     # Prettier (writes in-place)
pnpm preview    # Preview production build
```
