export const chatKeys = {
  all: ['chats'] as const,
  conversations: (projectId?: string) =>
    projectId
      ? ([...chatKeys.all, 'conversations', projectId] as const)
      : ([...chatKeys.all, 'conversations'] as const),
}
