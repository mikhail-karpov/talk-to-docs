import { useCreateChat } from '@/features/chat/hooks/use-create-chat'
import { ChatTextarea } from './chat-textarea'

interface CreateChatFormProps {
  projectId: string
}

export function CreateChatForm({ projectId }: CreateChatFormProps) {
  const createChat = useCreateChat()

  return (
    <div className="flex flex-col gap-2">
      <ChatTextarea
        onSubmit={(content) => createChat.mutate({ content, projectId })}
        disabled={createChat.isPending}
        placeholder="Type your first message..."
        rows={4}
        ariaLabel="First message"
      />
      {createChat.errorMessage && (
        <p className="text-sm text-destructive">{createChat.errorMessage}</p>
      )}
    </div>
  )
}
