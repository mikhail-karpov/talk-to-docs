import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import { Loader2, SendHorizonal } from 'lucide-react'
import { useAutoResizeTextarea } from '@/features/chat/hooks/use-auto-resize-textarea'

interface MessageInputProps {
  onSend: (content: string) => void
  disabled?: boolean
  errorMessage?: string | null
}

export function MessageInput({ onSend, disabled, errorMessage }: MessageInputProps) {
  const [value, setValue] = useState('')
  const { ref, resize, reset } = useAutoResizeTextarea()

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
    if (!content || disabled) return
    onSend(content)
    setValue('')
    reset()
  }

  return (
    <div className="p-4">
      <div className="flex flex-col gap-1.5 max-w-3xl mx-auto">
        {errorMessage && <p className="text-sm text-destructive text-center">{errorMessage}</p>}
        <div className="relative">
          <Textarea
            ref={ref}
            value={value}
            onChange={handleChange}
            onKeyDown={handleKeyDown}
            placeholder="Type a message..."
            disabled={disabled}
            rows={3}
            aria-label="Message"
          />
          <Button
            size="icon"
            onClick={submit}
            disabled={disabled || !value.trim()}
            aria-label="Send message"
            className="absolute right-2 bottom-2 size-8"
          >
            {disabled ? (
              <Loader2 className="size-4 animate-spin" />
            ) : (
              <SendHorizonal className="size-4" />
            )}
          </Button>
        </div>
        <p className="text-xs text-muted-foreground text-center">
          AI can make mistakes. Please double-check responses.
        </p>
      </div>
    </div>
  )
}
