import { QueryClientProvider } from '@tanstack/react-query'
import { RouterProvider } from 'react-router'
import { queryClient } from '@/lib/query-client'
import { createRouter } from './router'

const router = createRouter()

const App = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
    </QueryClientProvider>
  )
}

export default App
