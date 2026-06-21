import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import { useSidebar } from '@/components/ui/sidebar'
import { Loader2, LogOut } from 'lucide-react'
import { useAuth } from '@/features/auth/hooks/use-auth'
import { useSignOut } from '@/features/auth/hooks/use-sign-out'

export function UserCard() {
  const { state } = useSidebar()
  const { user } = useAuth()
  const signOut = useSignOut()

  if (!user) return null

  const initials = `${user.firstName[0] ?? ''}${user.lastName[0] ?? ''}`.toUpperCase()

  if (state === 'collapsed') {
    return (
      <div className="flex justify-center py-2">
        <Avatar className="size-8 rounded-lg">
          <AvatarFallback className="rounded-lg text-xs">{initials}</AvatarFallback>
        </Avatar>
      </div>
    )
  }

  return (
    <div className="flex items-center justify-between gap-4">
      <div className="flex items-center gap-3 min-w-0">
        <Avatar className="shrink-0">
          <AvatarFallback>{initials}</AvatarFallback>
        </Avatar>
        <div className="min-w-0">
          <p className="text-sm font-medium leading-none truncate">
            {user.firstName} {user.lastName}
          </p>
          <p className="text-xs text-muted-foreground mt-1 truncate">{user.email}</p>
        </div>
      </div>
      <Button
        variant="ghost"
        size="sm"
        className="shrink-0"
        onClick={() => signOut.mutate()}
        disabled={signOut.isPending}
      >
        {signOut.isPending ? <Loader2 className="animate-spin" /> : <LogOut />}
      </Button>
    </div>
  )
}
