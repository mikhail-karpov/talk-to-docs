import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router'
import { deleteChat } from '@/features/chat/api/chat-api'
import { chatKeys } from '@/features/chat/api/query-keys'

export function useDeleteChat() {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  const mutation = useMutation({
    mutationFn: (id: string) => deleteChat(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: chatKeys.conversations() })
      navigate('/chats')
    },
  })

  const errorMessage = mutation.error ? 'Failed to delete conversation. Please try again.' : null

  return { ...mutation, errorMessage }
}
