import { AlertCircle, FileText } from 'lucide-react'
import { EmptyState } from '@/components/ui/empty-state'
import { Skeleton } from '@/components/ui/skeleton'
import { useDocuments } from '@/features/documents/hooks/use-documents'
import { DocumentCard } from './document-card'

interface DocumentListProps {
  projectId: string
}

export function DocumentList({ projectId }: DocumentListProps) {
  const { documents, isPending, isError } = useDocuments(projectId)
  return (
    <div className="flex flex-col gap-6">
      {isPending ? (
        <div className="grid grid-cols-[repeat(auto-fill,minmax(280px,1fr))] gap-6">
          {Array.from({ length: 4 }).map((_, i) => (
            <Skeleton key={i} className="h-28 w-full rounded-xl" />
          ))}
        </div>
      ) : isError ? (
        <EmptyState icon={AlertCircle} label="Couldn't load documents. Please try again later." />
      ) : documents.length === 0 ? (
        <EmptyState icon={FileText} label="No documents yet. Upload your first one!" />
      ) : (
        <ul className="grid grid-cols-[repeat(auto-fill,minmax(280px,1fr))] gap-6">
          {documents.map((doc) => (
            <li key={doc.id}>
              <DocumentCard document={doc} />
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}
