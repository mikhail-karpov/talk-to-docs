import { useQuery } from '@tanstack/react-query'
import { getDocuments } from '@/features/documents/api/documents-api'
import { documentKeys } from '@/features/documents/api/query-keys'

export function useDocuments() {
  const query = useQuery({
    queryKey: documentKeys.list(),
    queryFn: getDocuments,
    refetchInterval: (q) => {
      const data = q.state.data
      if (!data) return false
      return data.some((d) => d.status === 'PENDING' || d.status === 'UPLOADED') ? 3000 : false
    },
  })

  return {
    ...query,
    documents: query.data ?? [],
  }
}
