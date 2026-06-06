import { api } from '@/lib/api-client'
import type { Message } from '@/features/chat/types'

export async function getMessages(conversationId: string): Promise<Message[]> {
  const { data } = await api.get<{ items: Message[] }>(`/api/v1/chat/${conversationId}/messages`)
  return data.items
}

export async function sendMessage(conversationId: string, content: string): Promise<Message> {
  const { data } = await api.post<Message>(`/api/v1/chat/${conversationId}/messages`, { content })
  return data
}
