import { isAxiosError } from 'axios'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router'
import { signIn } from '../api/auth-api'

export function useSignIn() {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  const mutation = useMutation({
    mutationFn: signIn,
    onSuccess: (data) => {
      queryClient.setQueryData(['auth', 'me'], data)
      navigate('/', { replace: true })
    },
  })

  const errorMessage = mutation.error
    ? isAxiosError(mutation.error) && mutation.error.response?.status === 401
      ? 'Invalid email or password.'
      : 'An unexpected error occurred. Please try again.'
    : null

  return { ...mutation, errorMessage }
}
