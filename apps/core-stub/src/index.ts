import express from 'express'
import cors from 'cors'
import subscribersRouter from './routes/subscribers'
import agentsRouter from './routes/agents'
import businessLinesRouter from './routes/business-lines'
import zipCodesRouter from './routes/zip-codes'
import foliosRouter from './routes/folios'
import catalogsRouter from './routes/catalogs'
import tariffsRouter from './routes/tariffs'

const app = express()
const PORT = process.env.PORT ?? 4000

app.use(cors())
app.use(express.json())

app.use('/v1/subscribers', subscribersRouter)
app.use('/v1/agents', agentsRouter)
app.use('/v1/business-lines', businessLinesRouter)
app.use('/v1/zip-codes', zipCodesRouter)
app.use('/v1/folios', foliosRouter)
app.use('/v1/catalogs', catalogsRouter)
app.use('/v1/tariffs', tariffsRouter)

app.get('/health', (_req, res) => {
  res.json({ status: 'UP', service: 'plataforma-core-ohs-stub', timestamp: new Date().toISOString() })
})

app.listen(PORT, () => {
  console.log(`Core stub running on http://localhost:${PORT}`)
})

export default app
