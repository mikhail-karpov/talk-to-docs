import { api } from '@/lib/api-client'
import type { Document } from '@/features/documents/types/document'

export async function getDocuments(projectId: string): Promise<Document[]> {
  const { data } = await api.get<{ items: Document[] }>('/api/v1/documents', {
    params: { projectId },
  })
  return data.items
}

async function uploadDocument(file: File, projectId: string): Promise<Document> {
  const form = new FormData()
  form.append('document', file)
  form.append('projectId', projectId)
  const { data } = await api.post<Document>('/api/v1/documents', form, {
    headers: { 'Content-Type': undefined },
  })
  return data
}

export interface UploadResult {
  uploaded: Document[]
  failed: string[]
}

export async function uploadDocuments(files: File[], projectId: string): Promise<UploadResult> {
  const results = await Promise.allSettled(files.map((file) => uploadDocument(file, projectId)))
  const uploaded: Document[] = []
  const failed: string[] = []
  results.forEach((result, i) => {
    if (result.status === 'fulfilled') {
      uploaded.push(result.value)
    } else {
      failed.push(files[i].name)
    }
  })
  return { uploaded, failed }
}

export async function deleteDocument(id: string): Promise<void> {
  await api.delete(`/api/v1/documents/${id}`)
}
