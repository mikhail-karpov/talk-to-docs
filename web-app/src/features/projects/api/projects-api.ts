import { api } from '@/lib/api-client'
import type { Project } from '@/features/projects/types'

export async function getProjects(): Promise<Project[]> {
  const { data } = await api.get<Project[]>('/api/v1/projects')
  return data
}

export async function getProject(id: string): Promise<Project> {
  const { data } = await api.get<Project>(`/api/v1/projects/${id}`)
  return data
}

export async function createProject(body: {
  title: string
  description?: string | null
}): Promise<Project> {
  const { data } = await api.post<Project>('/api/v1/projects', body)
  return data
}

export async function editProject(
  id: string,
  body: { title?: string; description?: string | null }
): Promise<Project> {
  const { data } = await api.put<Project>(`/api/v1/projects/${id}`, body)
  return data
}

export async function deleteProject(id: string): Promise<void> {
  await api.delete(`/api/v1/projects/${id}`)
}
