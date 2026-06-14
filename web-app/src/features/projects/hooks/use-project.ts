import { useQuery } from '@tanstack/react-query'
import { getProject } from '@/features/projects/api/projects-api'
import { projectKeys } from '@/features/projects/api/query-keys'

export function useProject(id: string) {
  return useQuery({
    queryKey: projectKeys.detail(id),
    queryFn: () => getProject(id),
  })
}
