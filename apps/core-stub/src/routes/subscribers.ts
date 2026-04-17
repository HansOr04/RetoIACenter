import { Router } from 'express'
import subscribers from '../fixtures/subscribers.json'

const router = Router()

router.get('/', (_req, res) => {
  res.json(subscribers)
})

router.get('/:id', (req, res) => {
  const subscriber = subscribers.find((s: any) => s.id === req.params.id)
  if (!subscriber) {
    return res.status(404).json({ error: 'Subscriber not found', code: 'SUBSCRIBER_NOT_FOUND' })
  }
  res.json(subscriber)
})

export default router
