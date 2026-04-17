import { Router } from 'express'
import zipCodes from '../fixtures/zip-codes.json'

const router = Router()

router.get('/:zipCode', (req, res) => {
  const record = zipCodes.find((z: any) => z.codigoPostal === req.params.zipCode)
  if (!record) {
    return res.status(404).json({ error: 'Zip code not found', code: 'ZIP_CODE_NOT_FOUND' })
  }
  res.json(record)
})

router.post('/validate', (req, res) => {
  const { codigoPostal } = req.body as { codigoPostal?: string }
  if (!codigoPostal) {
    return res.status(400).json({ error: 'codigoPostal is required', code: 'VALIDATION_ERROR' })
  }
  const valid = zipCodes.some((z: any) => z.codigoPostal === codigoPostal)
  res.json({ codigoPostal, valid })
})

export default router
