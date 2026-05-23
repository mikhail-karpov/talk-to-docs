import { queryClient } from '@/lib/query-client'
import { QueryClientProvider } from '@tanstack/react-query'
import HomeRoute from '@/app/routes/home'

const App = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <HomeRoute />
    </QueryClientProvider>
  )
}

export default App
