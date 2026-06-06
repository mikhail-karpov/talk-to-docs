import { useEffect, useRef, useState } from 'react'
import { getDocuments } from '@/features/documents/api/documents-api'
import { useStompSubscription } from '@/features/websocket/use-stomp-subscription'
import { useStomp } from '@/features/websocket/stomp-context'
import { useDocumentsStore } from '@/features/documents/stores/documents-store'
import type { Document } from '@/features/documents/types/document'

// User-scoped queue carrying document change events (create, status update). Deletes are not pushed
// over WebSocket — the deleting client removes the row itself ("delete and forget").
const DOCUMENTS_QUEUE = '/user/queue/documents'

/**
 * Owns list hydration: fetches the list once, re-syncs on WebSocket reconnect, and forwards live
 * document-change events to the store. Upload and remove logic live in their own hooks. Must be
 * rendered under `StompProvider`. Call it once and pass the result down.
 */
export function useDocuments() {
  const documents = useDocumentsStore((s) => s.documents)
  const set = useDocumentsStore((s) => s.set)
  const upsert = useDocumentsStore((s) => s.upsert)
  const [isPending, setIsPending] = useState(true)
  const [isError, setIsError] = useState(false)
  const { connected } = useStomp()

  // Seed with the fetched list. The ignore flag drops a resolved fetch after unmount.
  // `set` is a stable Zustand action reference; including it satisfies the exhaustive-deps rule.
  useEffect(() => {
    let ignore = false
    getDocuments()
      .then((docs) => {
        if (!ignore) set(docs)
      })
      .catch(() => {
        // A failed initial load surfaces an error state instead of an empty list.
        if (!ignore) setIsError(true)
      })
      .finally(() => {
        if (!ignore) setIsPending(false)
      })
    return () => {
      ignore = true
    }
  }, [set])

  // Re-sync on every reconnect (but not the first connect, which the mount fetch above already
  // covers): the broker keeps no history, so any status change that landed while we were
  // disconnected would otherwise be lost. A failed re-sync keeps the current list rather than
  // clearing it or flipping the error state.
  const hasConnectedBefore = useRef(false)
  useEffect(() => {
    if (!connected) return
    if (!hasConnectedBefore.current) {
      hasConnectedBefore.current = true
      return
    }
    let ignore = false
    getDocuments()
      .then((docs) => {
        if (!ignore) set(docs)
      })
      .catch(() => {
        // Background re-sync failure: keep showing the data we already have.
      })
    return () => {
      ignore = true
    }
  }, [connected, set])

  // Live updates carry creates and status changes; the store dedups by id. Deletes are not pushed.
  useStompSubscription<Document>(DOCUMENTS_QUEUE, upsert)

  return { documents, isPending, isError }
}
