export const chatKeys = {
  all: ['chats'] as const,
  conversations: () => [...chatKeys.all, 'conversations'] as const,
  messages: (conversationId: string) => [...chatKeys.all, conversationId, 'messages'] as const,
}
