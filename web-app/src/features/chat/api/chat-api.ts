import { api } from '@/lib/api-client'
import type { Chat } from '@/features/chat/types'

export async function getChats(): Promise<Chat[]> {
  const { data } = await api.get<{ items: Chat[] }>('/api/v1/chat')
  return data.items
}

export async function createChat(body: { content: string }): Promise<Chat> {
  const { data } = await api.post<Chat>('/api/v1/chat', body)
  return data
}
