import { useMutation, useQueryClient } from '@tanstack/react-query'
import { renameChat } from '@/features/chat/api/chat-api'
import { chatKeys } from '@/features/chat/api/query-keys'

export function useRenameChat() {
  const queryClient = useQueryClient()

  const mutation = useMutation({
    mutationFn: ({ id, title }: { id: string; title: string }) => renameChat(id, title),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: chatKeys.conversations() })
    },
  })

  const errorMessage = mutation.error ? 'Failed to rename conversation. Please try again.' : null

  return { ...mutation, errorMessage }
}
