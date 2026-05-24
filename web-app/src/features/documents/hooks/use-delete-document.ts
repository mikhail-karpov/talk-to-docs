import { useMutation, useQueryClient } from '@tanstack/react-query'
import { deleteDocument } from '../api/documents-api'
import { documentKeys } from '../api/query-keys'

export function useDeleteDocument() {
  const queryClient = useQueryClient()

  const mutation = useMutation({
    mutationFn: (id: string) => deleteDocument(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: documentKeys.list() })
    },
  })

  const errorMessage = mutation.error ? 'Failed to delete document. Please try again.' : null

  return { ...mutation, errorMessage }
}
