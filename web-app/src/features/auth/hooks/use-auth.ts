import { useQuery } from '@tanstack/react-query'
import { getMe } from '../api/auth-api'

export function useAuth() {
  const { data: user, isPending } = useQuery({
    queryKey: ['auth', 'me'],
    queryFn: getMe,
    retry: false,
    staleTime: Infinity,
  })

  return { user, isAuthenticated: !!user, isInitialized: !isPending }
}
