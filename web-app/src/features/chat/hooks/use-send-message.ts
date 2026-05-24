import { useMutation, useQueryClient } from '@tanstack/react-query'
import { sendMessage } from '../api/messages-api'
import { chatKeys } from '../api/query-keys'
import type { Message } from '../types'

export function useSendMessage(conversationId: string) {
  const queryClient = useQueryClient()

  const mutation = useMutation({
    mutationFn: (content: string) => sendMessage(conversationId, content),
    onMutate: async (content) => {
      await queryClient.cancelQueries({ queryKey: chatKeys.messages(conversationId) })
      const previous = queryClient.getQueryData<Message[]>(chatKeys.messages(conversationId))
      const optimistic: Message = {
        id: `optimistic-${Date.now()}`,
        conversationId,
        userId: '',
        authorType: 'USER',
        content,
        createdAt: new Date().toISOString(),
      }
      queryClient.setQueryData<Message[]>(chatKeys.messages(conversationId), (old = []) => [
        ...old,
        optimistic,
      ])
      return { previous }
    },
    onError: (_err, _content, ctx) => {
      if (ctx?.previous) {
        queryClient.setQueryData(chatKeys.messages(conversationId), ctx.previous)
      }
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: chatKeys.messages(conversationId) })
    },
  })

  const errorMessage = mutation.error ? 'Failed to send message. Please try again.' : null

  return { ...mutation, errorMessage }
}
