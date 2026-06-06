import type { Client } from '@stomp/stompjs'
import { createContext, useContext } from 'react'

type StompContextType = {
  client: Client | null
  connected: boolean
}

export const StompContext = createContext<StompContextType | null>(null)

export const useStomp = () => {
  const ctx = useContext(StompContext)
  if (!ctx) {
    throw new Error('useStomp must be used within StompProvider')
  }
  return ctx
}
