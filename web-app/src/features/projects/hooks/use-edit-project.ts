import { useMutation, useQueryClient } from '@tanstack/react-query'
import { editProject } from '@/features/projects/api/projects-api'
import { projectKeys } from '@/features/projects/api/query-keys'

export function useEditProject() {
  const queryClient = useQueryClient()

  const mutation = useMutation({
    mutationFn: ({
      id,
      ...body
    }: {
      id: string
      title?: string
      description?: string | null
    }) => editProject(id, body),
    onSuccess: (_data, { id }) => {
      // Edit affects both the list rows and the project's detail view.
      queryClient.invalidateQueries({ queryKey: projectKeys.lists() })
      queryClient.invalidateQueries({ queryKey: projectKeys.detail(id) })
    },
  })

  const errorMessage = mutation.error ? 'Failed to update project. Please try again.' : null

  return { ...mutation, errorMessage }
}
