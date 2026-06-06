import { useEffect, useReducer, useRef, useState } from 'react'
import { getMessages, sendMessage } from '@/features/chat/api/messages-api'
import { useStomp } from '@/features/websocket/stomp-context'
import { useStompSubscription } from '@/features/websocket/use-stomp-subscription'
import type { Message } from '@/features/chat/types'

const MESSAGES_QUEUE = '/user/queue/messages'

function sortedByCreatedAt(messages: Message[]): Message[] {
  return [...messages].sort((a, b) => a.createdAt.localeCompare(b.createdAt))
}

type Action = { type: 'set'; messages: Message[] } | { type: 'add'; message: Message }

function reducer(state: Message[], action: Action): Message[] {
  switch (action.type) {
    case 'set':
      return sortedByCreatedAt(action.messages)
    case 'add':
      // Dedup by id (WS may echo the same message), then sort.
      return sortedByCreatedAt([...state.filter((m) => m.id !== action.message.id), action.message])
  }
}

function isAwaitingReply(messages: Message[]): boolean {
  if (!messages.length) return false
  return messages[messages.length - 1].authorType === 'USER'
}

export function useChat(conversationId: string) {
  const [messages, dispatch] = useReducer(reducer, [])
  const [isPending, setIsPending] = useState(true)
  const [isError, setIsError] = useState(false)
  const [isSending, setIsSending] = useState(false)
  const [errorMessage, setErrorMessage] = useState<string | null>(null)
  const { connected } = useStomp()

  useEffect(() => {
    let ignore = false
    getMessages(conversationId)
      .then((history) => {
        if (!ignore) dispatch({ type: 'set', messages: history })
      })
      .catch(() => {
        if (!ignore) setIsError(true)
      })
      .finally(() => {
        if (!ignore) setIsPending(false)
      })
    return () => {
      ignore = true
    }
  }, [conversationId])

  // Re-sync on reconnect (skip the first connect — the mount fetch above covers it).
  const hasConnectedBefore = useRef(false)
  useEffect(() => {
    if (!connected) return
    if (!hasConnectedBefore.current) {
      hasConnectedBefore.current = true
      return
    }
    let ignore = false
    getMessages(conversationId)
      .then((history) => {
        if (!ignore) dispatch({ type: 'set', messages: history })
      })
      .catch(() => {
        // Background re-sync failure: keep showing the data we already have.
      })
    return () => {
      ignore = true
    }
  }, [connected, conversationId])

  useStompSubscription<Message>(MESSAGES_QUEUE, (message) => {
    if (message.conversationId === conversationId) {
      dispatch({ type: 'add', message })
    }
  })

  async function send(content: string) {
    setIsSending(true)
    setErrorMessage(null)
    try {
      const message = await sendMessage(conversationId, content)
      dispatch({ type: 'add', message })
    } catch {
      setErrorMessage('Failed to send message. Please try again.')
    } finally {
      setIsSending(false)
    }
  }

  return {
    messages,
    isPending,
    isError,
    isConnected: connected,
    isAwaitingReply: isAwaitingReply(messages),
    sendMessage: send,
    isSending,
    errorMessage,
  }
}
