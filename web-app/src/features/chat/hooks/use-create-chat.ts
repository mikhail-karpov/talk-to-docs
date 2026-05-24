import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router'
import { createChat } from '@/features/chat/api/chat-api'
import { chatKeys } from '@/features/chat/api/query-keys'

export function useCreateChat() {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  const mutation = useMutation({
    mutationFn: (body: { content: string }) => createChat(body),
    onSuccess: ({ id }) => {
      queryClient.invalidateQueries({ queryKey: chatKeys.conversations() })
      navigate(`/chats/${id}`)
    },
  })

  const errorMessage = mutation.error ? 'Failed to start chat. Please try again.' : null

  return { ...mutation, errorMessage }
}
