import { useMutation, useQueryClient } from '@tanstack/react-query'
import { deleteProject } from '@/features/projects/api/projects-api'
import { projectKeys } from '@/features/projects/api/query-keys'
import { chatKeys } from '@/features/chat/api/query-keys'

export function useDeleteProject() {
  const queryClient = useQueryClient()

  const mutation = useMutation({
    mutationFn: (id: string) => deleteProject(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: projectKeys.lists() })
      queryClient.invalidateQueries({ queryKey: chatKeys.all })
    },
  })

  const errorMessage = mutation.error ? 'Failed to delete project. Please try again.' : null

  return { ...mutation, errorMessage }
}
