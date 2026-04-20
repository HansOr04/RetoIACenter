import http from 'k6/http'
import { check, sleep } from 'k6'
import { Rate, Trend } from 'k6/metrics'

// Métricas personalizadas
const errorRate = new Rate('errors')
const calculateTrend = new Trend('calculate_duration')

export const options = {
  stages: [
    { duration: '1m', target: 10 },  // ramp-up a 10 usuarios en 1 min
    { duration: '3m', target: 10 },  // mantener 10 usuarios durante 3 min
    { duration: '1m', target: 0 },   // ramp-down
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'],   // 95% de requests < 2s
    errors: ['rate<0.05'],               // tasa de error < 5%
  },
}

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080'

// TODO: implementar fixture de folio con ubicaciones completas
const folioId = __ENV.FOLIO_ID || 'replace-with-real-folio-id'

export default function () {
  const url = `${BASE_URL}/api/v1/quotes/${folioId}/calculate`

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  }

  const res = http.post(url, null, params)

  const success = check(res, {
    'status is 200 or 201': (r) => r.status === 200 || r.status === 201,
    'response has primaTotal': (r) => {
      try {
        const body = JSON.parse(r.body)
        return body.primaTotal !== undefined
      } catch {
        return false
      }
    },
  })

  errorRate.add(!success)
  calculateTrend.add(res.timings.duration)

  sleep(1)
}
