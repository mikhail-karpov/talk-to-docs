import { useCreateProject } from '@/features/projects/hooks/use-create-project'
import { ProjectForm } from './project-form'
import type { ProjectFormValues } from './project-form-schema'

interface CreateProjectFormProps {
  onSuccess: () => void
}

export function CreateProjectForm({ onSuccess }: CreateProjectFormProps) {
  const createProject = useCreateProject()

  async function onSubmit(values: ProjectFormValues) {
    try {
      await createProject.mutateAsync({
        title: values.title,
        description: values.description || null,
      })
      onSuccess()
    } catch {
      // Error is surfaced to the user via createProject.errorMessage.
    }
  }

  return (
    <ProjectForm
      defaultValues={{ title: '', description: '' }}
      submitLabel="Create project"
      pendingLabel="Creating…"
      errorMessage={createProject.errorMessage}
      onSubmit={onSubmit}
    />
  )
}
