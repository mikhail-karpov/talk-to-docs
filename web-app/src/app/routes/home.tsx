import { UserCard } from '@/features/auth/components/user-card'

export default function HomeRoute() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-4">
      <UserCard />
    </div>
  )
}
