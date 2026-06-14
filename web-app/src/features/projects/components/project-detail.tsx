import { FolderOpen } from 'lucide-react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { EmptyState } from '@/components/ui/empty-state'
import { FullPageLoader } from '@/components/ui/full-page-loader'
import { ChatList } from '@/features/chat/components/chat-list'
import { CreateChatForm } from '@/features/chat/components/create-chat-form'
import { DocumentList } from '@/features/documents/components/documents-list'
import { UploadDocumentsForm } from '@/features/documents/components/upload-documents-form'
import { ProjectHeader } from '@/features/projects/components/project-header'
import { useProject } from '@/features/projects/hooks/use-project'

interface ProjectDetailProps {
  id: string
}

export function ProjectDetail({ id }: ProjectDetailProps) {
  const { data: project, isPending, isError } = useProject(id)

  if (isPending) {
    return <FullPageLoader />
  }

  if (isError || !project) {
    return <EmptyState icon={FolderOpen} label="Project not found." />
  }

  return (
    <div className="p-8 max-w-6xl mx-auto w-full">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 flex flex-col gap-8">
          <ProjectHeader project={project} />
          <CreateChatForm projectId={project.id} />
          <ChatList projectId={project.id} />
        </div>

        <aside className="lg:col-span-1">
          <Card>
            <CardHeader>
              <CardTitle>Documents</CardTitle>
            </CardHeader>
            <CardContent className="flex flex-col gap-6">
              <UploadDocumentsForm projectId={project.id} />
              <DocumentList projectId={project.id} />
            </CardContent>
          </Card>
        </aside>
      </div>
    </div>
  )
}
