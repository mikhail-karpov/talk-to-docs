import { Loader2, Trash2 } from 'lucide-react'
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
import { useDeleteDocument } from '@/features/documents/hooks/use-delete-document'
import type { Document } from '@/features/documents/types/document'

interface DocumentRowActionsProps {
  document: Document
}

export function DocumentRowActions({ document }: DocumentRowActionsProps) {
  const deleteDocument = useDeleteDocument()

  function handleDelete() {
    deleteDocument.mutate(document.id)
  }

  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>
        <Button
          variant="ghost"
          size="icon"
          aria-label="Delete document"
          disabled={deleteDocument.isPending}
        >
          {deleteDocument.isPending ? (
            <Loader2 data-icon="inline-start" className="animate-spin" />
          ) : (
            <Trash2 data-icon="inline-start" />
          )}
        </Button>
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Delete document?</AlertDialogTitle>
          <AlertDialogDescription>
            <strong>{document.name}</strong> will be permanently deleted. This action cannot be
            undone.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>Cancel</AlertDialogCancel>
          <AlertDialogAction
            variant="destructive"
            onClick={handleDelete}
            disabled={deleteDocument.isPending}
          >
            Delete
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  )
}
