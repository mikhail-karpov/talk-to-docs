import { useMutation, useQueryClient } from '@tanstack/react-query'
import { createProject } from '@/features/projects/api/projects-api'
import { projectKeys } from '@/features/projects/api/query-keys'

export function useCreateProject() {
  const queryClient = useQueryClient()

  const mutation = useMutation({
    mutationFn: (body: { title: string; description?: string | null }) => createProject(body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: projectKeys.lists() })
    },
  })

  const errorMessage = mutation.error ? 'Failed to create project. Please try again.' : null

  return { ...mutation, errorMessage }
}
