import { MoreHorizontal } from 'lucide-react'
import { useNavigate } from 'react-router'
import { Button } from '@/components/ui/button'
import { ProjectDropdownMenu } from '@/features/projects/components/project-dropdown-menu'
import type { Project } from '@/features/projects/types'

interface ProjectHeaderProps {
  project: Project
}

export function ProjectHeader({ project }: ProjectHeaderProps) {
  const navigate = useNavigate()

  return (
    <div className="flex items-start justify-between gap-4">
      <div className="flex flex-col gap-2">
        <h1 className="text-2xl font-semibold">{project.title}</h1>
        <p className="text-muted-foreground">{project.description ?? 'No description'}</p>
      </div>
      <ProjectDropdownMenu
        project={project}
        onDeleted={() => navigate('/projects')}
        trigger={
          <Button variant="ghost" size="icon" aria-label="Project actions">
            <MoreHorizontal />
          </Button>
        }
      />
    </div>
  )
}
