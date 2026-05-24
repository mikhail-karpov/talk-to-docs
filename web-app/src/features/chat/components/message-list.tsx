import { useEffect, useLayoutEffect, useRef } from 'react'
import { EmptyState } from '@/components/ui/empty-state'
import { MessageSquare } from 'lucide-react'
import { MessageBubble } from './message-bubble'
import type { Message } from '../types'

type MessageListProps = {
  messages: Message[]
  isAwaitingReply: boolean
}

const NEAR_BOTTOM_PX = 80

export function MessageList({ messages, isAwaitingReply }: MessageListProps) {
  const containerRef = useRef<HTMLDivElement>(null)
  const bottomRef = useRef<HTMLDivElement>(null)
  const wasNearBottomRef = useRef(true)
  const didInitialScrollRef = useRef(false)

  function isNearBottom() {
    const el = containerRef.current
    if (!el) return true
    return el.scrollHeight - el.scrollTop - el.clientHeight < NEAR_BOTTOM_PX
  }

  function handleScroll() {
    wasNearBottomRef.current = isNearBottom()
  }

  useLayoutEffect(() => {
    if (!didInitialScrollRef.current && messages.length > 0) {
      bottomRef.current?.scrollIntoView({ behavior: 'auto' })
      didInitialScrollRef.current = true
    }
  }, [messages.length])

  useEffect(() => {
    if (wasNearBottomRef.current) {
      bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
    }
  }, [messages, isAwaitingReply])

  return (
    <div
      ref={containerRef}
      onScroll={handleScroll}
      role="log"
      aria-live="polite"
      aria-relevant="additions"
      className="flex flex-col gap-4 p-4 overflow-y-auto flex-1"
    >
      {messages.length === 0 ? (
        <EmptyState icon={MessageSquare} label="No messages yet. Start the conversation." />
      ) : (
        <>
          {messages.map((message) => (
            <MessageBubble key={message.id} message={message} />
          ))}
          {isAwaitingReply && <TypingIndicator />}
        </>
      )}
      <div ref={bottomRef} />
    </div>
  )
}

function TypingIndicator() {
  return (
    <div className="flex items-start" aria-label="Assistant is typing">
      <div className="bg-muted text-foreground rounded-2xl rounded-bl-sm px-4 py-3">
        <div className="flex gap-1">
          <span className="size-1.5 rounded-full bg-foreground/50 animate-bounce [animation-delay:-0.3s]" />
          <span className="size-1.5 rounded-full bg-foreground/50 animate-bounce [animation-delay:-0.15s]" />
          <span className="size-1.5 rounded-full bg-foreground/50 animate-bounce" />
        </div>
      </div>
    </div>
  )
}
