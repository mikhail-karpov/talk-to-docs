import { Client } from '@stomp/stompjs'
import { useEffect, useState, type ReactNode } from 'react'
import SockJS from 'sockjs-client'
import { StompContext } from '@/features/websocket/stomp-context'

export function StompProvider({ children }: { children: ReactNode }) {
  const [connected, setConnected] = useState(false)
  const [client] = useState(
    () =>
      new Client({
        webSocketFactory: () => new SockJS(`${import.meta.env.VITE_API_URL}/ws`),
        reconnectDelay: 5000,
        heartbeatIncoming: 10000,
        heartbeatOutgoing: 10000,
        onConnect: () => setConnected(true),
        onWebSocketClose: () => setConnected(false),
        debug: (str) => {
          if (import.meta.env.DEV) {
            console.log(str)
          }
        },
      })
  )

  useEffect(() => {
    client.activate()
    return () => {
      setConnected(false)
      void client.deactivate()
    }
  }, [client])

  return <StompContext.Provider value={{ client, connected }}>{children}</StompContext.Provider>
}
