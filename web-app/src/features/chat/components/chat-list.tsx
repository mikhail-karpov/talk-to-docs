import { useState } from 'react'
import { MessageSquare } from 'lucide-react'
import { NavLink } from 'react-router'
import { EmptyState } from '@/components/ui/empty-state'
import { Skeleton } from '@/components/ui/skeleton'
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

interface ChatListProps {
  projectId?: string
}

export function ChatListContent({ projectId }: ChatListProps) {
  const { chats, isPending } = useChats(projectId)

  if (isPending) {
    return (
      <ul className="flex flex-col gap-1">
        {Array.from({ length: 5 }).map((_, i) => (
          <li key={i} className="flex items-center gap-3 px-4 py-3">
            <Skeleton className="size-4 shrink-0 rounded" />
            <Skeleton className="h-4 w-1/2" />
          </li>
        ))}
      </ul>
    )
  }

  if (chats.length === 0) {
    return <EmptyState icon={MessageSquare} label="No chats yet. Start a new one!" />
  }

  return (
    <ul className="flex flex-col gap-1">
      {chats.map((chat) => (
        <ChatListItem key={chat.id} chat={chat} />
      ))}
    </ul>
  )
}

export function ChatList({ projectId }: ChatListProps) {
  return (
    <div className="flex flex-col gap-6">
      <h2 className="text-2xl font-semibold">Chats</h2>
      <ChatListContent projectId={projectId} />
    </div>
  )
}
