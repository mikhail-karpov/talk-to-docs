import { useParams } from 'react-router'
import { Chat } from '@/features/chat/components/chat'

export default function ChatRoute() {
  const { id } = useParams<{ id: string }>()
  if (!id) return null
  // Key by id so navigating between conversations remounts Chat with fresh state and subscription.
  return <Chat key={id} conversationId={id} />
}
