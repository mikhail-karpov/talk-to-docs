import { Loader2, Pencil, Trash2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog'
import type { Chat } from '@/features/chat/types'
import { useDeleteChat } from '@/features/chat/hooks/use-delete-chat'

interface ChatRowActionsProps {
  chat: Chat
  onStartEditing: () => void
}

export function ChatRowActions({ chat, onStartEditing }: ChatRowActionsProps) {
  const deleteChat = useDeleteChat()

  return (
    <div className="flex flex-col items-end gap-1 pr-2">
      <div className="flex items-center opacity-0 group-hover:opacity-100 transition-opacity">
        <Button
          variant="ghost"
          size="icon"
          aria-label="Rename conversation"
          disabled={deleteChat.isPending}
          onClick={(e) => {
            e.preventDefault()
            onStartEditing()
          }}
        >
          <Pencil data-icon="inline-start" />
        </Button>
        <AlertDialog>
          <AlertDialogTrigger asChild>
            <Button
              variant="ghost"
              size="icon"
              aria-label="Delete conversation"
              disabled={deleteChat.isPending}
            >
              {deleteChat.isPending ? (
                <Loader2 data-icon="inline-start" className="animate-spin" />
              ) : (
                <Trash2 data-icon="inline-start" />
              )}
            </Button>
          </AlertDialogTrigger>
          <AlertDialogContent>
            <AlertDialogHeader>
              <AlertDialogTitle>Delete conversation?</AlertDialogTitle>
              <AlertDialogDescription>
                Chat will be permanently deleted. This action cannot be undone.
              </AlertDialogDescription>
            </AlertDialogHeader>
            <AlertDialogFooter>
              <AlertDialogCancel>Cancel</AlertDialogCancel>
              <AlertDialogAction
                variant="destructive"
                onClick={() => deleteChat.mutate(chat.id)}
                disabled={deleteChat.isPending}
              >
                Delete
              </AlertDialogAction>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialog>
      </div>
      {deleteChat.errorMessage && (
        <p className="text-xs text-destructive">{deleteChat.errorMessage}</p>
      )}
    </div>
  )
}
