import { isAxiosError } from 'axios'
import { useMutation } from '@tanstack/react-query'
import { useNavigate } from 'react-router'
import { signUp } from '@/features/auth/api/auth-api'

export function useSignUp() {
  const navigate = useNavigate()

  const mutation = useMutation({
    mutationFn: signUp,
    onSuccess: () => {
      navigate('/sign-in', { replace: true })
    },
  })

  const errorMessage = mutation.error
    ? isAxiosError(mutation.error) && mutation.error.response?.status === 409
      ? 'An account with this email already exists.'
      : 'An unexpected error occurred. Please try again.'
    : null

  return { ...mutation, errorMessage }
}
