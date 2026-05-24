import { useState } from 'react'
import { Loader2, SendHorizonal } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import { useCreateChat } from '../hooks/use-create-chat'
import { useAutoResizeTextarea } from '../hooks/use-auto-resize-textarea'

export function CreateChatForm() {
  const [value, setValue] = useState('')
  const { ref, resize } = useAutoResizeTextarea()
  const createChat = useCreateChat()

  function handleChange(e: React.ChangeEvent<HTMLTextAreaElement>) {
    setValue(e.target.value)
    resize()
  }

  function handleKeyDown(e: React.KeyboardEvent<HTMLTextAreaElement>) {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      submit()
    }
  }

  function submit() {
    const content = value.trim()
    if (!content || createChat.isPending) return
    createChat.mutate({ content })
  }

  return (
    <div className="flex flex-col gap-6 p-8 max-w-4xl mx-auto w-full">
      <h1 className="text-xl font-semibold">New Chat</h1>
      <div className="flex flex-col gap-2">
        <div className="relative">
          <Textarea
            ref={ref}
            value={value}
            onChange={handleChange}
            onKeyDown={handleKeyDown}
            placeholder="Type your first message..."
            disabled={createChat.isPending}
            rows={4}
            aria-label="First message"
          />
          <Button
            size="icon"
            onClick={submit}
            disabled={createChat.isPending || !value.trim()}
            aria-label="Start chat"
            className="absolute right-2 bottom-2 size-8"
          >
            {createChat.isPending ? (
              <Loader2 className="size-4 animate-spin" />
            ) : (
              <SendHorizonal className="size-4" />
            )}
          </Button>
        </div>
        {createChat.errorMessage && (
          <p className="text-sm text-destructive">{createChat.errorMessage}</p>
        )}
      </div>
    </div>
  )
}
