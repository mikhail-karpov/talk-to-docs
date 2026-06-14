import { useMutation, useQueryClient } from '@tanstack/react-query'
import { deleteDocument } from '@/features/documents/api/documents-api'
import { documentKeys } from '@/features/documents/api/query-keys'

// Scoped to a single document: each DocumentRowActions instance owns its own copy of this hook.
export function useRemoveDocument() {
  const queryClient = useQueryClient()

  const mutation = useMutation({
    mutationFn: (id: string) => deleteDocument(id),
    // Prefix-invalidate every project list to drop the row.
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: documentKeys.lists() })
    },
  })

  const deleteError = mutation.error ? 'Failed to delete document. Please try again.' : null

  return { removeDocument: mutation.mutate, isDeleting: mutation.isPending, deleteError }
}
