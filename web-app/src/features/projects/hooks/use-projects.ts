import { useQuery } from '@tanstack/react-query'
import { getProjects } from '@/features/projects/api/projects-api'
import { projectKeys } from '@/features/projects/api/query-keys'

export function useProjects() {
  return useQuery({
    queryKey: projectKeys.lists(),
    queryFn: () => getProjects(),
  })
}
