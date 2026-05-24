export interface Chat {
  id: string
  title: string
  createdAt: string
}

export type AuthorType = 'USER' | 'AI'

export interface Message {
  id: string
  conversationId: string
  userId: string
  authorType: AuthorType
  content: string
  createdAt: string
}
