import { WifiOff } from 'lucide-react'
import { useChat } from '@/features/chat/hooks/use-chat'
import { MessageInput } from './message-input'
import { MessageList } from './message-list'
import { MessageListSkeleton } from './message-list-skeleton'

interface ChatProps {
  conversationId: string
}

export function Chat({ conversationId }: ChatProps) {
  const {
    messages,
    isPending,
    isError,
    isConnected,
    isAwaitingReply,
    sendMessage,
    isSending,
    errorMessage,
  } = useChat(conversationId)

  if (isPending) {
    return (
      <div className="flex flex-col flex-1 max-w-4xl mx-auto w-full">
        <MessageListSkeleton />
      </div>
    )
  }

  if (isError) {
    return (
      <div className="flex flex-col flex-1 max-w-4xl mx-auto w-full items-center justify-center">
        <p className="text-sm text-destructive">
          Failed to load messages. Please refresh the page.
        </p>
      </div>
    )
  }

  return (
    <div className="flex flex-col flex-1 max-w-4xl mx-auto w-full">
      <MessageList messages={messages} isAwaitingReply={isAwaitingReply} />
      {!isConnected && (
        <div className="flex items-center justify-center gap-1.5 py-1.5 text-xs text-muted-foreground">
          <WifiOff className="size-3.5" />
          Reconnecting… messages may be delayed.
        </div>
      )}
      <MessageInput onSend={sendMessage} disabled={isSending} errorMessage={errorMessage} />
    </div>
  )
}
