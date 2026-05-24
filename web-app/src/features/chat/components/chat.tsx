import { useSendMessage } from '../hooks/use-send-message'
import { useMessages, isAwaitingReply } from '../hooks/use-messages'
import { FullPageLoader } from '@/components/ui/full-page-loader'
import { MessageInput } from './message-input'
import { MessageList } from './message-list'

interface ChatProps {
  conversationId: string
}

export function Chat({ conversationId }: ChatProps) {
  const { messages, isPending } = useMessages(conversationId)
  const sendMessage = useSendMessage(conversationId)

  if (isPending) {
    return <FullPageLoader />
  }

  return (
    <div className="flex flex-col flex-1 max-w-4xl mx-auto w-full">
      <MessageList messages={messages} isAwaitingReply={isAwaitingReply(messages)} />
      <MessageInput
        onSend={(content) => sendMessage.mutate(content)}
        disabled={sendMessage.isPending}
        errorMessage={sendMessage.errorMessage}
      />
    </div>
  )
}
