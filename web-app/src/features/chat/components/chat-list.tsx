import { useState } from 'react'
import { Plus, MessageSquare } from 'lucide-react'
import { Link, NavLink } from 'react-router'
import { Button } from '@/components/ui/button'
import { EmptyState } from '@/components/ui/empty-state'
import { FullPageLoader } from '@/components/ui/full-page-loader'
import { useChats } from '@/features/chat/hooks/use-chats'
import { useRenameChat } from '@/features/chat/hooks/use-rename-chat'
import { ChatRowActions } from '@/features/chat/components/chat-row-actions'
import type { Chat } from '@/features/chat/types'

interface ChatRenameInputProps {
  defaultTitle: string
  isPending: boolean
  onSave: (title: string) => void
  onCancel: () => void
}

function ChatRenameInput({ defaultTitle, isPending, onSave, onCancel }: ChatRenameInputProps) {
  const [title, setTitle] = useState(defaultTitle)

  const handleSave = () => {
    const trimmed = title.trim()
    if (trimmed) {
      onSave(trimmed)
    } else {
      onCancel()
    }
  }

  return (
    <div className="flex flex-1 items-center gap-3 px-4 py-3 text-sm">
      <MessageSquare className="size-4 shrink-0 text-muted-foreground" />
      <input
        autoFocus
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        onKeyDown={(e) => {
          if (e.key === 'Enter') handleSave()
          if (e.key === 'Escape') onCancel()
        }}
        onBlur={handleSave}
        disabled={isPending}
        className="flex-1 bg-transparent outline-none text-sm text-foreground"
      />
    </div>
  )
}

interface ChatListItemProps {
  chat: Chat
}

function ChatListItem({ chat }: ChatListItemProps) {
  const [isEditing, setIsEditing] = useState(false)
  const renameChat = useRenameChat()

  return (
    <li className="group relative flex items-center rounded-lg hover:bg-accent">
      {isEditing ? (
        <ChatRenameInput
          defaultTitle={chat.title}
          isPending={renameChat.isPending}
          onSave={(title) => {
            if (title !== chat.title) renameChat.mutate({ id: chat.id, title })
            setIsEditing(false)
          }}
          onCancel={() => setIsEditing(false)}
        />
      ) : (
        <NavLink
          to={`/chats/${chat.id}`}
          className={({ isActive }) =>
            `flex flex-1 items-center gap-3 rounded-lg px-4 py-3 text-sm transition-colors hover:text-accent-foreground ${
              isActive ? 'bg-accent text-accent-foreground font-medium' : 'text-muted-foreground'
            }`
          }
        >
          <MessageSquare className="size-4 shrink-0" />
          <span className="flex-1 truncate">{chat.title}</span>
        </NavLink>
      )}
      {!isEditing && <ChatRowActions chat={chat} onStartEditing={() => setIsEditing(true)} />}
    </li>
  )
}

export function ChatList() {
  const { chats, isPending } = useChats()

  return (
    <div className="flex flex-col gap-6 p-8 max-w-4xl mx-auto w-full">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold">Chats</h1>
        <Button asChild>
          <Link to="/chats/new">
            <Plus className="size-4" />
            New Chat
          </Link>
        </Button>
      </div>

      {isPending ? (
        <FullPageLoader />
      ) : chats.length === 0 ? (
        <EmptyState icon={MessageSquare} label="No chats yet. Start a new one!" />
      ) : (
        <ul className="flex flex-col gap-1">
          {chats.map((chat) => (
            <ChatListItem key={chat.id} chat={chat} />
          ))}
        </ul>
      )}
    </div>
  )
}
