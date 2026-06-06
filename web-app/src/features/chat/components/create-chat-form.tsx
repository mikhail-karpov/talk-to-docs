import { useCreateChat } from '@/features/chat/hooks/use-create-chat'
import { ChatTextarea } from './chat-textarea'

export function CreateChatForm() {
  const createChat = useCreateChat()

  return (
    <div className="flex flex-col gap-6 p-8 max-w-4xl mx-auto w-full">
      <h1 className="text-xl font-semibold">New Chat</h1>
      <div className="flex flex-col gap-2">
        <ChatTextarea
          onSubmit={(content) => createChat.mutate({ content })}
          disabled={createChat.isPending}
          placeholder="Type your first message..."
          rows={4}
          ariaLabel="First message"
        />
        {createChat.errorMessage && (
          <p className="text-sm text-destructive">{createChat.errorMessage}</p>
        )}
      </div>
    </div>
  )
}
