import type { LucideIcon } from 'lucide-react'

interface EmptyStateProps {
  icon: LucideIcon
  label: string
}

export function EmptyState({ icon: Icon, label }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center gap-2 py-16 text-center m-auto">
      <Icon className="size-8 text-muted-foreground" />
      <p className="text-sm text-muted-foreground">{label}</p>
    </div>
  )
}
