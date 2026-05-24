import { useMutation, useQueryClient } from '@tanstack/react-query'
import { uploadDocuments } from '@/features/documents/api/documents-api'
import { documentKeys } from '@/features/documents/api/query-keys'

export function useUploadDocuments() {
  const queryClient = useQueryClient()

  const mutation = useMutation({
    mutationFn: (files: File[]) => uploadDocuments(files),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: documentKeys.list() })
    },
  })

  const errorMessage = mutation.error ? 'Failed to upload documents. Please try again.' : null

  return { ...mutation, errorMessage }
}
