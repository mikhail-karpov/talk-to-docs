import { useMutation, useQueryClient } from '@tanstack/react-query'
import { uploadDocuments, type UploadResult } from '@/features/documents/api/documents-api'
import { documentKeys } from '@/features/documents/api/query-keys'

export function useUploadDocuments(projectId: string) {
  const queryClient = useQueryClient()

  const mutation = useMutation({
    mutationFn: (files: File[]) => uploadDocuments(files, projectId),
    // uploadDocuments settles per file and never rejects, so onSuccess fires on partial success
    // too; refetching the project's list picks up whatever uploaded.
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: documentKeys.lists(projectId) })
    },
  })

  // Resolves with the partial-success UploadResult so the form can surface failed file names.
  function upload(files: File[]): Promise<UploadResult> {
    return mutation.mutateAsync(files)
  }

  return { upload, isUploading: mutation.isPending }
}
