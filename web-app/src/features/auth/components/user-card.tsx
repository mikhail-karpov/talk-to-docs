import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import { Loader2, LogOut } from 'lucide-react'
import { useAuth } from '../hooks/use-auth'
import { useSignOut } from '../hooks/use-sign-out'

export function UserCard() {
  const { user } = useAuth()
  const signOut = useSignOut()

  if (!user) return null

  const initials = `${user.firstName[0] ?? ''}${user.lastName[0] ?? ''}`.toUpperCase()

  return (
    <div className="flex items-center justify-between gap-4">
      <div className="flex items-center gap-3">
        <Avatar>
          <AvatarFallback>{initials}</AvatarFallback>
        </Avatar>
        <div>
          <p className="text-sm font-medium leading-none">
            {user.firstName} {user.lastName}
          </p>
          <p className="text-xs text-muted-foreground mt-1">{user.email}</p>
        </div>
      </div>
      <Button
        variant="ghost"
        size="sm"
        onClick={() => signOut.mutate()}
        disabled={signOut.isPending}
      >
        {signOut.isPending ? (
          <Loader2 className="animate-spin" />
        ) : (
          <LogOut />
        )}
      </Button>
    </div>
  )
}
