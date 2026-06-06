import { useState } from 'react'
import { deleteDocument } from '@/features/documents/api/documents-api'
import { useDocumentsStore } from '@/features/documents/stores/documents-store'

// Scoped to a single document: each DocumentRowActions instance owns its own copy of this hook.
export function useRemoveDocument() {
  const remove = useDocumentsStore((s) => s.remove)
  const [isDeleting, setIsDeleting] = useState(false)
  const [deleteError, setDeleteError] = useState<string | null>(null)

  async function removeDocument(id: string): Promise<void> {
    setIsDeleting(true)
    setDeleteError(null)
    try {
      // Deletes aren't pushed over WebSocket, so the deleting client removes the row itself.
      await deleteDocument(id)
      remove(id)
    } catch {
      setDeleteError('Failed to delete document. Please try again.')
    } finally {
      setIsDeleting(false)
    }
  }

  return { removeDocument, isDeleting, deleteError }
}
