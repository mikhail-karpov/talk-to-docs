import { useEditProject } from '@/features/projects/hooks/use-edit-project'
import type { Project } from '@/features/projects/types'
import { ProjectForm } from './project-form'
import type { ProjectFormValues } from './project-form-schema'

interface EditProjectFormProps {
  project: Project
  onSuccess: () => void
}

export function EditProjectForm({ project, onSuccess }: EditProjectFormProps) {
  const editProject = useEditProject()

  async function onSubmit(values: ProjectFormValues) {
    try {
      await editProject.mutateAsync({
        id: project.id,
        title: values.title,
        description: values.description || null,
      })
      onSuccess()
    } catch {
      // Error is surfaced to the user via editProject.errorMessage.
    }
  }

  return (
    <ProjectForm
      defaultValues={{ title: project.title, description: project.description ?? '' }}
      submitLabel="Save changes"
      pendingLabel="Saving…"
      errorMessage={editProject.errorMessage}
      onSubmit={onSubmit}
    />
  )
}
