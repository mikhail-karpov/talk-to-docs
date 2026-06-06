import { Navigate, Outlet } from 'react-router'
import { FullPageLoader } from '@/components/ui/full-page-loader'
import { useAuth } from '@/features/auth/hooks/use-auth'
import { StompProvider } from '@/features/websocket/stomp-provider'

export function PrivateRoute() {
  const { isAuthenticated, isInitialized } = useAuth()

  if (!isInitialized) {
    return <FullPageLoader />
  }

  if (!isAuthenticated) {
    return <Navigate to="/sign-in" replace />
  }

  return (
    <StompProvider>
      <Outlet />
    </StompProvider>
  )
}
