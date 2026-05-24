import { api } from '@/lib/api-client'
import type { Document } from '@/features/documents/types/document'

export async function getDocuments(): Promise<Document[]> {
  const { data } = await api.get<{ items: Document[] }>('/api/v1/documents')
  return data.items
}

async function uploadDocument(file: File): Promise<Document> {
  const form = new FormData()
  form.append('document', file)
  const { data } = await api.post<Document>('/api/v1/documents', form, {
    headers: { 'Content-Type': undefined },
  })
  return data
}

export async function uploadDocuments(files: File[]): Promise<Document[]> {
  return Promise.all(files.map(uploadDocument))
}

export async function deleteDocument(id: string): Promise<void> {
  await api.delete(`/api/v1/documents/${id}`)
}
