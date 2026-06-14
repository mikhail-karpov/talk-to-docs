import { MoreHorizontal } from 'lucide-react'
import { useNavigate } from 'react-router'
import { Button } from '@/components/ui/button'
import { Card, CardAction, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { ProjectDropdownMenu } from '@/features/projects/components/project-dropdown-menu'
import type { Project } from '@/features/projects/types'

interface ProjectCardProps {
  project: Project
}

export function ProjectCard({ project }: ProjectCardProps) {
  const navigate = useNavigate()

  function open() {
    navigate(`/projects/${project.id}`)
  }

  return (
    <Card
      role="button"
      tabIndex={0}
      aria-label={`Open project ${project.title}`}
      className="h-full cursor-pointer transition-colors hover:bg-muted/50 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
      onClick={open}
      onKeyDown={(e) => {
        if (e.key === 'Enter' || e.key === ' ') {
          e.preventDefault()
          open()
        }
      }}
    >
      <CardHeader>
        <CardTitle className="truncate">{project.title}</CardTitle>
        <CardDescription>{project.description ?? 'No description'}</CardDescription>
        <CardAction
          onClick={(e) => e.stopPropagation()}
          onKeyDown={(e) => e.stopPropagation()}
        >
          <ProjectDropdownMenu
            project={project}
            trigger={
              <Button
                variant="ghost"
                size="icon"
                aria-label="Project actions"
                className="opacity-0 transition-opacity group-hover/card:opacity-100 group-focus-within/card:opacity-100 [@media(hover:none)]:opacity-100"
              >
                <MoreHorizontal />
              </Button>
            }
          />
        </CardAction>
      </CardHeader>
    </Card>
  )
}
