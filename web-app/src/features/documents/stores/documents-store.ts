import { create } from 'zustand'
import type { Document } from '@/features/documents/types/document'

function sortedByUpdatedAt(documents: Document[]): Document[] {
  return [...documents].sort((a, b) => b.updatedAt.localeCompare(a.updatedAt))
}

interface DocumentsState {
  documents: Document[]
  set: (docs: Document[]) => void
  upsert: (doc: Document) => void
  remove: (id: string) => void
  reset: () => void
}

export const useDocumentsStore = create<DocumentsState>((set) => ({
  documents: [],
  // Deletes are "delete and forget", so a DELETED document is treated as gone everywhere it appears.
  set: (docs) => set({ documents: sortedByUpdatedAt(docs.filter((d) => d.status !== 'DELETED')) }),
  upsert: (doc) =>
    set((s) => {
      const withoutDoc = s.documents.filter((d) => d.id !== doc.id)
      // A DELETED status arriving over WebSocket removes the row rather than upserting it.
      if (doc.status === 'DELETED') {
        return { documents: withoutDoc }
      }
      return { documents: sortedByUpdatedAt([...withoutDoc, doc]) }
    }),
  remove: (id) => set((s) => ({ documents: s.documents.filter((d) => d.id !== id) })),
  // Cleared on sign-out so the next user never sees the previous session's documents.
  reset: () => set({ documents: [] }),
}))
