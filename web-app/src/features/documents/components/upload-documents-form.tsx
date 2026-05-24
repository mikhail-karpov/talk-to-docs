import { useRef } from 'react'
import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Loader2, Paperclip, X } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import { cn } from '@/lib/utils'
import { formatFileSize } from '../utils/format-file-size'
import { useUploadDocuments } from '../hooks/use-upload-documents'

const MAX_FILE_SIZE = 10 * 1024 * 1024
const ALLOWED_EXTENSIONS = ['md', 'txt']

const uploadSchema = z.object({
  files: z
    .custom<File[]>()
    .refine((files) => Array.isArray(files) && files.length > 0, 'Select at least one file.')
    .refine(
      (files) => !Array.isArray(files) || files.every((f) => f.size <= MAX_FILE_SIZE),
      'Each file must be 10 MB or less.'
    )
    .refine(
      (files) =>
        !Array.isArray(files) ||
        files.every((f) => {
          const ext = f.name.split('.').pop()?.toLowerCase()
          return ext !== undefined && ALLOWED_EXTENSIONS.includes(ext)
        }),
      'Only .md and .txt files are supported.'
    ),
})

type UploadValues = z.infer<typeof uploadSchema>

interface UploadDocumentsFormProps {
  onSuccess?: () => void
}

export function UploadDocumentsForm({ onSuccess }: UploadDocumentsFormProps) {
  const inputRef = useRef<HTMLInputElement>(null)
  const upload = useUploadDocuments()

  const form = useForm<UploadValues>({
    resolver: zodResolver(uploadSchema),
    defaultValues: { files: [] },
  })

  const selectedFiles: File[] = useWatch({ control: form.control, name: 'files' }) ?? []

  const isLocked = upload.isPending

  async function triggerUpload(files: File[]) {
    const result = await form.trigger('files')
    if (!result) return
    const docs = await upload.mutateAsync(files).catch(() => null)
    if (!docs) return
    form.reset()
    onSuccess?.()
  }

  function addFiles(incoming: File[]) {
    if (isLocked) return
    const existing: File[] = form.getValues('files') ?? []
    const merged = [
      ...existing,
      ...incoming.filter((f) => !existing.some((ex) => ex.name === f.name)),
    ]
    form.setValue('files', merged, { shouldValidate: true })
    triggerUpload(merged)
  }

  function handleFileChange(e: React.ChangeEvent<HTMLInputElement>) {
    const incoming = Array.from(e.target.files ?? [])
    if (inputRef.current) inputRef.current.value = ''
    addFiles(incoming)
  }

  function removeFile(name: string) {
    const updated = selectedFiles.filter((f) => f.name !== name)
    form.setValue('files', updated, { shouldValidate: true })
  }

  return (
    <div className="flex flex-col gap-4">
      <Form {...form}>
        <form className="flex flex-col gap-4">
          <FormField
            control={form.control}
            name="files"
            render={() => (
              <FormItem>
                <FormLabel>Files</FormLabel>
                <FormControl>
                  <div
                    className={cn(
                      'flex flex-col items-center justify-center gap-2 rounded-lg border-2 border-dashed border-border p-10 text-center transition-colors',
                      isLocked
                        ? 'opacity-50 cursor-not-allowed'
                        : 'cursor-pointer hover:bg-muted/50'
                    )}
                    onClick={() => !isLocked && inputRef.current?.click()}
                    onDragOver={(e) => e.preventDefault()}
                    onDrop={(e) => {
                      e.preventDefault()
                      addFiles(Array.from(e.dataTransfer.files))
                    }}
                  >
                    {upload.isPending ? (
                      <Loader2 className="size-8 text-muted-foreground animate-spin" />
                    ) : (
                      <Paperclip className="size-8 text-muted-foreground" />
                    )}
                    <p className="text-sm text-muted-foreground">
                      Drag and drop files here, or{' '}
                      <span className="text-foreground underline underline-offset-2">browse</span>
                    </p>
                    <p className="text-xs text-muted-foreground">.md and .txt · max 10 MB each</p>
                    <input
                      ref={inputRef}
                      type="file"
                      multiple
                      accept=".md,.txt"
                      className="hidden"
                      disabled={isLocked}
                      onChange={handleFileChange}
                    />
                  </div>
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          {selectedFiles.length > 0 && (
            <ul className="flex flex-col gap-1">
              {selectedFiles.map((file) => (
                <li
                  key={file.name}
                  className="flex items-center justify-between rounded-md border px-3 py-2 text-sm"
                >
                  <span className="truncate text-foreground">{file.name}</span>
                  <div className="flex items-center gap-2 shrink-0 ml-2">
                    <span className="text-muted-foreground">{formatFileSize(file.size)}</span>
                    <Button
                      type="button"
                      variant="ghost"
                      size="icon"
                      className="size-6"
                      onClick={() => removeFile(file.name)}
                      aria-label={`Remove ${file.name}`}
                    >
                      <X />
                    </Button>
                  </div>
                </li>
              ))}
            </ul>
          )}

          {upload.errorMessage && <p className="text-sm text-destructive">{upload.errorMessage}</p>}
        </form>
      </Form>
    </div>
  )
}
