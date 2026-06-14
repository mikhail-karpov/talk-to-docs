import { FolderOpen } from 'lucide-react'
import { EmptyState } from '@/components/ui/empty-state'
import { NewProjectButton } from '@/features/projects/components/create-project-button'
import { ProjectCard } from '@/features/projects/components/project-card'
import { ProjectCardSkeleton } from '@/features/projects/components/project-card-skeleton'
import { useProjects } from '@/features/projects/hooks/use-projects'

export function ProjectList() {
  const { data: projects, isPending } = useProjects()

  return (
    <div className="pt-12 flex flex-col gap-6 p-8 max-w-4xl mx-auto w-full">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-semibold">Projects</h2>
        <NewProjectButton />
      </div>

      {isPending ? (
        <ul className="grid grid-cols-[repeat(auto-fill,minmax(280px,1fr))] gap-6">
          {Array.from({ length: 4 }).map((_, i) => (
            <li key={i}>
              <ProjectCardSkeleton />
            </li>
          ))}
        </ul>
      ) : !projects?.length ? (
        <EmptyState icon={FolderOpen} label="No projects yet." />
      ) : (
        <ul className="grid grid-cols-[repeat(auto-fill,minmax(280px,1fr))] gap-6">
          {projects.map((project) => (
            <li key={project.id}>
              <ProjectCard project={project} />
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}
