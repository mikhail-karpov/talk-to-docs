import { SignInForm } from '@/features/auth/components/sign-in-form'
import { useAuth } from '@/features/auth/hooks/use-auth'
import { FullPageLoader } from '@/components/ui/full-page-loader'
import { Navigate } from 'react-router'

export default function SignInRoute() {
  const { isAuthenticated, isInitialized } = useAuth()

  if (!isInitialized) {
    return <FullPageLoader />
  }

  if (isAuthenticated) {
    return <Navigate to={'/'} replace />
  }

  return (
    <div className="flex min-h-screen items-center justify-center p-4">
      <SignInForm />
    </div>
  )
}
