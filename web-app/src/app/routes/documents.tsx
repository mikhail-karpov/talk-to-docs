import { DocumentsTable } from '@/features/documents/components/documents-table'
import { UploadDocumentsForm } from '@/features/documents/components/upload-documents-form'

export default function DocumentsRoute() {
  return (
    <div className="flex flex-col gap-6 p-8 max-w-4xl mx-auto w-full">
      <UploadDocumentsForm />
      <DocumentsTable />
    </div>
  )
}
