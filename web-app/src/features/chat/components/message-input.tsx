import { ChatTextarea } from './chat-textarea'

interface MessageInputProps {
  onSend: (content: string) => void
  disabled?: boolean
  errorMessage?: string | null
}

export function MessageInput({ onSend, disabled, errorMessage }: MessageInputProps) {
  return (
    <div className="p-4">
      <div className="flex flex-col gap-1.5 max-w-4xl mx-auto">
        {errorMessage && <p className="text-sm text-destructive text-center">{errorMessage}</p>}
        <ChatTextarea
          onSubmit={onSend}
          disabled={disabled}
          placeholder="Type a message..."
          rows={3}
          ariaLabel="Message"
          footer={
            <p className="text-xs text-muted-foreground text-center">
              AI can make mistakes. Please double-check responses.
            </p>
          }
        />
      </div>
    </div>
  )
}
