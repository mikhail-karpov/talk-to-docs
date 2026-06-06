import { useState } from 'react'
import { uploadDocuments, type UploadResult } from '@/features/documents/api/documents-api'
import { useDocumentsStore } from '@/features/documents/stores/documents-store'

export function useUploadDocuments() {
  const upsert = useDocumentsStore((s) => s.upsert)
  const [isUploading, setIsUploading] = useState(false)

  async function upload(files: File[]): Promise<UploadResult> {
    setIsUploading(true)
    try {
      const result = await uploadDocuments(files)
      result.uploaded.forEach(upsert)
      return result
    } catch {
      // uploadDocuments settles per file and shouldn't reject; guard against an unexpected throw.
      return { uploaded: [], failed: files.map((f) => f.name) }
    } finally {
      setIsUploading(false)
    }
  }

  return { upload, isUploading }
}
