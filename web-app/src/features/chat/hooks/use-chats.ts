import { useQuery } from '@tanstack/react-query'
import { getChats } from '@/features/chat/api/chat-api'
import { chatKeys } from '@/features/chat/api/query-keys'

export function useChats(projectId?: string) {
  const { data: chats = [], isPending } = useQuery({
    queryKey: chatKeys.conversations(projectId),
    queryFn: () => getChats(projectId),
  })
  return { chats, isPending }
}
