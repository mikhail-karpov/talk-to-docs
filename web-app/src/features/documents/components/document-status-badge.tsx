import { Badge } from '@/components/ui/badge'
import { cn } from '@/lib/utils'
import type { DocumentStatus } from '@/features/documents/types/document'

const statusConfig: Record<DocumentStatus, { label: string; className: string }> = {
  PENDING: {
    label: 'Pending',
    className: '',
  },
  UPLOADED: {
    label: 'Uploaded',
    className: 'text-blue-700 border-blue-200 bg-blue-50 dark:text-blue-300 dark:border-blue-800 dark:bg-blue-950/40',
  },
  PROCESSED: {
    label: 'Processed',
    className: 'text-green-700 border-green-200 bg-green-50 dark:text-green-300 dark:border-green-800 dark:bg-green-950/40',
  },
  ERROR: {
    label: 'Error',
    className: '',
  },
}

interface DocumentStatusBadgeProps {
  status: DocumentStatus
}

export function DocumentStatusBadge({ status }: DocumentStatusBadgeProps) {
  const { label, className } = statusConfig[status]
  const variant = status === 'ERROR' ? 'destructive' : status === 'PENDING' ? 'secondary' : 'outline'

  return (
    <Badge variant={variant} className={cn(className)}>
      {label}
    </Badge>
  )
}
