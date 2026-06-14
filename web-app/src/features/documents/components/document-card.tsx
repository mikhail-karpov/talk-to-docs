import { Card, CardAction, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { formatFileSize } from '@/features/documents/utils/format-file-size'
import type { Document } from '@/features/documents/types/document'
import { DocumentRowActions } from './document-row-actions'
import { Badge } from '@/components/ui/badge'

interface DocumentCardProps {
  document: Document
}

export function DocumentCard({ document }: DocumentCardProps) {
  return (
    <Card className="h-full">
      <CardHeader>
        <CardTitle className="truncate">{document.name}</CardTitle>
        <CardAction>
          <DocumentRowActions document={document} />
        </CardAction>
      </CardHeader>
      <CardContent className="flex items-center justify-between gap-2 text-sm text-muted-foreground">
        <div className="flex items-center gap-2">
          <span>{formatFileSize(document.sizeBytes)}</span>
          {document.status === 'ERROR' && <Badge variant="destructive">Error</Badge>}
        </div>
        <span className="shrink-0">{new Date(document.updatedAt).toLocaleDateString()}</span>
      </CardContent>
    </Card>
  )
}
