#!/bin/bash
set -e
echo "=== Verificando docker-compose ==="
docker compose config --quiet && echo "✓ docker-compose.yml válido"

echo ""
echo "=== Levantando servicios ==="
docker compose up --build -d

echo ""
echo "=== Esperando health checks ==="
sleep 30

echo ""
echo "=== Verificando servicios ==="
curl -sf http://localhost:4000/health && echo "✓ core-stub OK" || echo "✗ core-stub FALLA"
curl -sf http://localhost:8080/actuator/health && echo "✓ api OK" || echo "✗ api FALLA"
curl -sf http://localhost:3000 && echo "✓ web OK" || echo "✗ web FALLA"

echo ""
echo "=== Test smoke: crear folio ==="
KEY="smoke-$(date +%s)"
curl -sf -X POST http://localhost:8080/api/v1/folios \
  -H "Content-Type: application/json" \
  -H "X-Idempotency-Key: $KEY" \
  -d '{}' | python3 -m json.tool || echo "✗ Crear folio FALLA"

echo ""
echo "=== Done ==="
