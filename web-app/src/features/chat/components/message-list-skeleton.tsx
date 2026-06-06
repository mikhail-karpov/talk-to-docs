import { Skeleton } from '@/components/ui/skeleton'

const SKELETONS = [
  { align: 'items-start', width: 'w-64' },
  { align: 'items-end', width: 'w-48' },
  { align: 'items-start', width: 'w-72' },
  { align: 'items-end', width: 'w-56' },
  { align: 'items-start', width: 'w-60' },
]

export function MessageListSkeleton() {
  return (
    <div className="flex flex-col gap-4 p-4 flex-1">
      {SKELETONS.map((s, i) => (
        <div key={i} className={`flex flex-col gap-1 ${s.align}`}>
          <Skeleton className={`h-10 rounded-2xl ${s.width}`} />
          <Skeleton className="h-3 w-12" />
        </div>
      ))}
    </div>
  )
}
