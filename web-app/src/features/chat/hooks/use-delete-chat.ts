import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useMatch, useNavigate } from 'react-router'
import { deleteChat } from '@/features/chat/api/chat-api'
import { chatKeys } from '@/features/chat/api/query-keys'
import type { Chat } from '@/features/chat/types'

export function useDeleteChat() {
  const queryClient = useQueryClient()
  const navigate = useNavigate()
  // The chat currently open in the URL, if any (`/chats/:id`).
  const openChatId = useMatch('/chats/:id')?.params.id

  const mutation = useMutation({
    mutationFn: (chat: Chat) => deleteChat(chat.id),
    onSuccess: (_data, chat) => {
      // Invalidating the list drops the row in place. Only navigate away when the chat being
      // deleted is the one currently open; prefer its project over the global chats list.
      queryClient.invalidateQueries({ queryKey: chatKeys.conversations() })
      if (openChatId === chat.id) {
        navigate(chat.projectId ? `/projects/${chat.projectId}` : '/chats')
      }
    },
  })

  const errorMessage = mutation.error ? 'Failed to delete conversation. Please try again.' : null

  return { ...mutation, errorMessage }
}
