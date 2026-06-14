export const documentKeys = {
  all: ['documents'] as const,
  lists: (projectId?: string) =>
    projectId
      ? ([...documentKeys.all, 'list', projectId] as const)
      : ([...documentKeys.all, 'list'] as const),
}
