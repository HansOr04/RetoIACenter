import { Router } from 'express'
import agents from '../fixtures/agents.json'

const router = Router()

router.get('/', (_req, res) => {
  res.json(agents)
})

router.get('/:id', (req, res) => {
  const agent = agents.find((a: any) => a.id === req.params.id)
  if (!agent) {
    return res.status(404).json({ error: 'Agent not found', code: 'AGENT_NOT_FOUND' })
  }
  res.json(agent)
})

export default router
