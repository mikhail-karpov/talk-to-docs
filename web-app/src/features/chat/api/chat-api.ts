import { api } from '@/lib/api-client'
import type { Chat, Message } from '@/features/chat/types'

export async function getChats(): Promise<Chat[]> {
  const { data } = await api.get<{ items: Chat[] }>('/api/v1/chat')
  return data.items
}

export async function createChat(body: { content: string }): Promise<Message> {
  const { data } = await api.post<Message>('/api/v1/chat', body)
  return data
}
