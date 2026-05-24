import { useQuery } from '@tanstack/react-query'
import { getChats } from '../api/chat-api'
import { chatKeys } from '../api/query-keys'

export function useChats() {
  const { data: chats = [], isPending } = useQuery({
    queryKey: chatKeys.conversations(),
    queryFn: getChats,
  })
  return { chats, isPending }
}
