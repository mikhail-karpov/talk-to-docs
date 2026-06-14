import { ChatList } from '@/features/chat/components/chat-list'

export default function ChatsRoute() {
  return (
    <div className="flex flex-col gap-6 p-8 max-w-4xl mx-auto w-full">
      <ChatList />
    </div>
  )
}
