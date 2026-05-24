import { useRef, useCallback } from 'react'

export function useAutoResizeTextarea() {
  const ref = useRef<HTMLTextAreaElement>(null)

  const resize = useCallback(() => {
    const el = ref.current
    if (!el) return
    el.style.height = 'auto'
    el.style.height = `${el.scrollHeight}px`
  }, [])

  const reset = useCallback(() => {
    if (ref.current) ref.current.style.height = 'auto'
  }, [])

  return { ref, resize, reset }
}
