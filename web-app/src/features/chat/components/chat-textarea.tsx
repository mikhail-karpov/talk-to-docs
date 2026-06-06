import { useState } from 'react'
import { Loader2, SendHorizonal } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import { useAutoResizeTextarea } from '@/features/chat/hooks/use-auto-resize-textarea'

interface ChatTextareaProps {
  onSubmit: (content: string) => void
  disabled?: boolean
  placeholder?: string
  rows?: number
  ariaLabel?: string
  footer?: React.ReactNode
}

export function ChatTextarea({
  onSubmit,
  disabled,
  placeholder,
  rows = 3,
  ariaLabel = 'Message',
  footer,
}: ChatTextareaProps) {
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
    onSubmit(content)
    setValue('')
    reset()
  }

  return (
    <>
      <div className="relative">
        <Textarea
          ref={ref}
          value={value}
          onChange={handleChange}
          onKeyDown={handleKeyDown}
          placeholder={placeholder}
          disabled={disabled}
          rows={rows}
          aria-label={ariaLabel}
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
      {footer}
    </>
  )
}
