# Talk-To-Docs Web Application

React frontend for the Talk-To-Docs RAG application — upload documents and chat with an AI that answers from your content.

## Tech Stack

- **React 19** + **TypeScript** + **Vite**
- **React Router**
- **Tailwind CSS v4** + **shadcn/ui**
- **TanStack Query** (server state)
- **axios**, **STOMP / SockJS** (WebSocket)

## Structure

```
src/
├── app/         # Root component (index.tsx), router config, route components, auth guard
├── components/  # Shared components (layout/, ui/)
├── features/    # Feature modules (auth, chat, documents, projects, websocket)
├── hooks/       # Shared hooks
├── lib/         # Preconfigured libraries (axios, TanStack Query, utils)
├── index.css    # Global styles
└── main.tsx     # Application entry point
```

## Commands

```bash
pnpm dev        # Start Vite dev server (http://localhost:5173)
pnpm build      # Type-check + production build
pnpm lint       # ESLint
pnpm format     # Prettier (writes in-place)
pnpm preview    # Preview production build
```
