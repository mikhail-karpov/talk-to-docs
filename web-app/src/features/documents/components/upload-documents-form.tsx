import { useRef, useState } from 'react'
import { Loader2, Paperclip } from 'lucide-react'
import { cn } from '@/lib/utils'
import { useUploadDocuments } from '@/features/documents/hooks/use-upload-documents'

const MAX_FILE_SIZE = 5 * 1024 * 1024
const ALLOWED_EXTENSIONS = ['md', 'txt', 'pdf']

const ACCEPT_ATTR = ALLOWED_EXTENSIONS.map((ext) => `.${ext}`).join(',')
const EXTENSIONS_LABEL = ALLOWED_EXTENSIONS.map((ext) => `.${ext}`).join(', ')
const MAX_FILE_SIZE_LABEL = `${MAX_FILE_SIZE / (1024 * 1024)} MB`

function rejectionReason(file: File): string | null {
  const ext = file.name.split('.').pop()?.toLowerCase()
  if (ext === undefined || !ALLOWED_EXTENSIONS.includes(ext)) {
    return 'unsupported type'
  }
  if (file.size > MAX_FILE_SIZE) {
    return `over ${MAX_FILE_SIZE_LABEL}`
  }
  return null
}

interface UploadDocumentsFormProps {
  projectId: string
}

export function UploadDocumentsForm({ projectId }: UploadDocumentsFormProps) {
  const { upload, isUploading } = useUploadDocuments(projectId)
  const inputRef = useRef<HTMLInputElement>(null)
  const [errorMessage, setErrorMessage] = useState<string | null>(null)

  async function handleFiles(incoming: File[]) {
    if (isUploading || incoming.length === 0) return

    const seen = new Set<string>()
    const accepted: File[] = []
    const failed: string[] = []
    for (const file of incoming) {
      if (seen.has(file.name)) continue
      seen.add(file.name)
      const reason = rejectionReason(file)
      if (reason) {
        failed.push(`${file.name} (${reason})`)
      } else {
        accepted.push(file)
      }
    }

    if (accepted.length > 0) {
      const result = await upload(accepted)
      failed.push(...result.failed)
    }

    setErrorMessage(failed.length > 0 ? `Couldn't upload: ${failed.join(', ')}` : null)
  }

  function handleFileChange(e: React.ChangeEvent<HTMLInputElement>) {
    const incoming = Array.from(e.target.files ?? [])
    if (inputRef.current) inputRef.current.value = ''
    void handleFiles(incoming)
  }

  function openPicker() {
    if (!isUploading) inputRef.current?.click()
  }

  return (
    <div className="flex flex-col gap-4">
      <div
        role="button"
        tabIndex={isUploading ? -1 : 0}
        aria-label="Upload documents"
        aria-disabled={isUploading}
        className={cn(
          'flex flex-col items-center justify-center gap-2 rounded-lg border-2 border-dashed border-border p-10 text-center transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring',
          isUploading ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer hover:bg-muted/50'
        )}
        onClick={openPicker}
        onKeyDown={(e) => {
          if (e.key === 'Enter' || e.key === ' ') {
            e.preventDefault()
            openPicker()
          }
        }}
        onDragOver={(e) => e.preventDefault()}
        onDrop={(e) => {
          e.preventDefault()
          void handleFiles(Array.from(e.dataTransfer.files))
        }}
      >
        {isUploading ? (
          <Loader2 className="size-8 text-muted-foreground animate-spin" />
        ) : (
          <Paperclip className="size-8 text-muted-foreground" />
        )}
        <p className="text-sm text-muted-foreground">
          Drag and drop files here, or{' '}
          <span className="text-foreground underline underline-offset-2">browse</span>
        </p>
        <p className="text-xs text-muted-foreground">
          {EXTENSIONS_LABEL} · max {MAX_FILE_SIZE_LABEL} each
        </p>
        <input
          ref={inputRef}
          type="file"
          multiple
          accept={ACCEPT_ATTR}
          className="hidden"
          disabled={isUploading}
          onChange={handleFileChange}
        />
      </div>

      {errorMessage && <p className="text-sm text-destructive">{errorMessage}</p>}
    </div>
  )
}
