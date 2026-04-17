import { Router } from 'express'
import riskClassification from '../fixtures/risk-classification.json'
import guarantees from '../fixtures/guarantees.json'

const router = Router()

router.get('/risk-classification', (_req, res) => {
  res.json(riskClassification)
})

router.get('/guarantees', (_req, res) => {
  res.json(guarantees)
})

export default router
