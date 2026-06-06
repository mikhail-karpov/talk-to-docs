import { useEffect, useRef } from 'react'
import { useStomp } from '@/features/websocket/stomp-context'

export function useStompSubscription<T>(destination: string, callback: (payload: T) => void): void {
  const { client, connected } = useStomp()

  // Keep the latest callback in a ref so an inline/changing callback doesn't force a re-subscribe.
  const callbackRef = useRef(callback)
  useEffect(() => {
    callbackRef.current = callback
  })

  useEffect(() => {
    if (!client || !connected) {
      return
    }
    const subscription = client.subscribe(destination, (message) => {
      callbackRef.current(JSON.parse(message.body) as T)
    })
    return () => subscription.unsubscribe()
  }, [client, connected, destination])
}
