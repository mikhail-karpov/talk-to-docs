import { api } from '@/lib/api-client'
import type { Chat, Message } from '@/features/chat/types'

export async function getChats(projectId?: string): Promise<Chat[]> {
  const { data } = await api.get<{ items: Chat[] }>('/api/v1/chat', {
    params: projectId ? { projectId } : undefined,
  })
  return data.items
}

export async function createChat(body: { content: string; projectId: string }): Promise<Message> {
  const { data } = await api.post<Message>('/api/v1/chat', body)
  return data
}

export async function deleteChat(id: string): Promise<void> {
  await api.delete(`/api/v1/chat/${id}`)
}

export async function renameChat(id: string, title: string): Promise<Chat> {
  const { data } = await api.put<Chat>(`/api/v1/chat/${id}`, { title })
  return data
}
