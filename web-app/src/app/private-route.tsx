import { Navigate, Outlet } from 'react-router'
import { FullPageLoader } from '@/components/ui/full-page-loader'
import { useAuth } from '@/features/auth/hooks/use-auth'

export function PrivateRoute() {
  const { isAuthenticated, isInitialized } = useAuth()

  if (!isInitialized) {
    return <FullPageLoader />
  }

  if (!isAuthenticated) {
    return <Navigate to="/sign-in" replace />
  }
  return <Outlet />
}
