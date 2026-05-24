import { Plus, MessageSquare } from 'lucide-react'
import { Link, NavLink } from 'react-router'
import { Button } from '@/components/ui/button'
import { EmptyState } from '@/components/ui/empty-state'
import { FullPageLoader } from '@/components/ui/full-page-loader'
import { useChats } from '@/features/chat/hooks/use-chats'

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
            <li key={chat.id}>
              <NavLink
                to={`/chats/${chat.id}`}
                className={({ isActive }) =>
                  `flex items-center gap-3 rounded-lg px-4 py-3 text-sm transition-colors hover:bg-accent hover:text-accent-foreground ${
                    isActive
                      ? 'bg-accent text-accent-foreground font-medium'
                      : 'text-muted-foreground'
                  }`
                }
              >
                <MessageSquare className="size-4 shrink-0" />
                {chat.title}
              </NavLink>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}
