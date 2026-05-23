import { Button } from '@/components/ui/button'

const HomeRoute = () => {
  return (
    <div className="flex min-h-screen items-center justify-center">
      <Button onClick={() => console.log('button clicked')}>Click me</Button>
    </div>
  )
}

export default HomeRoute
