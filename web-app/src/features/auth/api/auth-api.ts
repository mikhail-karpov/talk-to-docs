import { api } from '@/lib/api-client'
import type { SignInRequest, SignUpRequest, User } from '@/features/auth/types'

export async function getMe(): Promise<User> {
  const { data } = await api.get<User>('/api/v1/auth/me')
  return data
}

export async function signIn(credentials: SignInRequest): Promise<User> {
  const { data } = await api.post<User>('/api/v1/auth/login', credentials)
  return data
}

export async function signUp(request: SignUpRequest): Promise<void> {
  await api.post('/api/v1/auth/registration', request)
}

export async function signOut(): Promise<void> {
  await api.post('/api/v1/auth/logout')
}
