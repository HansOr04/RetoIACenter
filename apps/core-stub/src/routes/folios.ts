import { Router } from 'express'
import folios from '../fixtures/folios.json'

const router = Router()

router.get('/', (_req, res) => {
  res.json(folios)
})

router.get('/:id', (req, res) => {
  const folio = folios.find((f: any) => f.id === req.params.id)
  if (!folio) {
    return res.status(404).json({ error: 'Folio not found', code: 'FOLIO_NOT_FOUND' })
  }
  res.json(folio)
})

export default router
