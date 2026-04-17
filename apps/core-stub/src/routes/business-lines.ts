import { Router } from 'express'
import businessLines from '../fixtures/business-lines.json'

const router = Router()

router.get('/', (_req, res) => {
  res.json(businessLines)
})

router.get('/:id', (req, res) => {
  const bl = businessLines.find((b: any) => b.id === req.params.id)
  if (!bl) {
    return res.status(404).json({ error: 'Business line not found', code: 'BUSINESS_LINE_NOT_FOUND' })
  }
  res.json(bl)
})

export default router
