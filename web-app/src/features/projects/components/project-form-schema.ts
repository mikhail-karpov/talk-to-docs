import { z } from 'zod'

export const projectSchema = z.object({
  title: z.string().min(1, 'Title is required').max(64, 'Title must be at most 64 characters'),
  description: z.string().max(256, 'Description must be at most 256 characters').optional(),
})

export type ProjectFormValues = z.infer<typeof projectSchema>
