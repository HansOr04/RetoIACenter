import { Router } from 'express'
import tariffs from '../fixtures/tariffs.json'

const router = Router()

const VALID_TYPES = ['incendio', 'cat', 'fhm', 'equipo-electronico'] as const
type TariffType = typeof VALID_TYPES[number]

router.get('/:type', (req, res) => {
  const type = req.params.type as TariffType
  if (!VALID_TYPES.includes(type)) {
    return res.status(400).json({ error: `Invalid tariff type. Valid types: ${VALID_TYPES.join(', ')}`, code: 'INVALID_TARIFF_TYPE' })
  }
  res.json((tariffs as Record<TariffType, unknown>)[type] ?? [])
})

export default router
