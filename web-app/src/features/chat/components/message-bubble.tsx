import { cn } from '@/lib/utils'
import type { Message } from '../types'

function formatTime(iso: string) {
  return new Intl.DateTimeFormat(undefined, { hour: '2-digit', minute: '2-digit' }).format(
    new Date(iso)
  )
}

interface MessageBubbleProps {
  message: Message
}

export function MessageBubble({ message }: MessageBubbleProps) {
  const isUser = message.authorType === 'USER'

  return (
    <div className={cn('flex flex-col gap-1', isUser ? 'items-end' : 'items-start')}>
      <div
        className={cn(
          'max-w-[75%] rounded-2xl px-4 py-2.5 text-sm whitespace-pre-wrap break-words',
          isUser
            ? 'bg-primary text-primary-foreground rounded-br-sm'
            : 'bg-muted text-foreground rounded-bl-sm'
        )}
      >
        <span className="sr-only">{isUser ? 'You said: ' : 'Assistant said: '}</span>
        {message.content}
      </div>
      <span className="text-xs text-muted-foreground px-1">{formatTime(message.createdAt)}</span>
    </div>
  )
}
