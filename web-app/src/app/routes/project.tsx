import { Navigate, useParams } from 'react-router'
import { ProjectDetail } from '@/features/projects/components/project-detail'

export default function ProjectRoute() {
  const { id } = useParams()
  if (!id) return <Navigate to="/projects" replace />
  return <ProjectDetail id={id} />
}
