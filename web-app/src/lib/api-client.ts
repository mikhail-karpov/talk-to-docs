import axios, { isAxiosError } from 'axios'
import { queryClient } from './query-client'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (isAxiosError(error) && error.response?.status === 401 && !error.config?.url?.includes('/auth/')) {
      queryClient.removeQueries({ queryKey: ['auth'] })
      window.location.assign('/sign-in')
    }
    return Promise.reject(error)
  }
)
