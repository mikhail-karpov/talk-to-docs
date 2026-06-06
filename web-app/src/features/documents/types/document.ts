export type DocumentStatus = 'PENDING' | 'UPLOADED' | 'PROCESSED' | 'ERROR' | 'DELETED'

export interface Document {
  id: string
  name: string
  sizeBytes: number
  status: DocumentStatus
  updatedAt: string
}
