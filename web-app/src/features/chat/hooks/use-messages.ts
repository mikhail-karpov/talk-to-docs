import { useQuery } from '@tanstack/react-query'
import { getMessages } from '../api/messages-api'
import { chatKeys } from '../api/query-keys'
import type { Message } from '../types'

export function isAwaitingReply(messages: Message[] | undefined): boolean {
  if (!messages?.length) return false
  return messages[messages.length - 1].authorType === 'USER'
}

export function useMessages(conversationId: string) {
  const { data: messages = [], isPending } = useQuery({
    queryKey: chatKeys.messages(conversationId),
    queryFn: () => getMessages(conversationId),
    refetchInterval: (query) => (isAwaitingReply(query.state.data) ? 2500 : false),
    refetchIntervalInBackground: false,
  })
  return { messages, isPending }
}
