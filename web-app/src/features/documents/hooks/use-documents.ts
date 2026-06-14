import { useQuery } from '@tanstack/react-query'
import { getDocuments } from '@/features/documents/api/documents-api'
import { documentKeys } from '@/features/documents/api/query-keys'

export function useDocuments(projectId: string) {
  const {
    data: documents = [],
    isPending,
    isError,
  } = useQuery({
    queryKey: documentKeys.lists(projectId),
    queryFn: () => getDocuments(projectId),
  })

  return { documents, isPending, isError }
}
