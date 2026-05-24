import { useParams } from 'react-router'
import { Chat } from '@/features/chat/components/chat'

export default function ChatRoute() {
  const { id } = useParams<{ id: string }>()
  if (!id) return null
  return <Chat conversationId={id} />
}
