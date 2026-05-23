# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
pnpm dev        # Start Vite dev server (http://localhost:5173)
pnpm build      # Type-check + production build
pnpm lint       # ESLint
pnpm format     # Prettier (writes in-place)
pnpm preview    # Preview production build
```

## Tech Stack

- **React 19** + **TypeScript 6** + **Vite 8**
- **React Router**
- **Tailwind CSS v4** and **shadcn/ui**
- **axios**
- **TanStack Query** for server-side state management
- **Zustand** for client-side state management

## PLANNING MODE - ALWAYS FOLLOW

- Always ask clarifying questions
- Never assume design, tech stack or features
- Use deep-dive sub-agents to assist with research
- Use deep-dive sub-agents to review the different aspects of your plan before presenting to the user

## REFERENCES

@docs/project-structure.md - defines project structure
