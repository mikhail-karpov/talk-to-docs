import type { LucideIcon } from 'lucide-react'
import { NavLink, useLocation } from 'react-router'
import { SidebarMenuButton, SidebarMenuItem } from '@/components/ui/sidebar'

interface NavItemProps {
  to: string
  icon: LucideIcon
  label: string
}

export function NavItem({ to, icon: Icon, label }: NavItemProps) {
  const { pathname } = useLocation()

  return (
    <SidebarMenuItem>
      <SidebarMenuButton asChild isActive={pathname.startsWith(to)} tooltip={label}>
        <NavLink to={to}>
          <Icon />
          <span>{label}</span>
        </NavLink>
      </SidebarMenuButton>
    </SidebarMenuItem>
  )
}
