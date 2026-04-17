# plataforma-core-ohs (stub)

Mock del sistema core de la aseguradora. Sirve catálogos maestros, datos de suscriptores, agentes y tarifas usando fixtures JSON estáticos.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/v1/subscribers` | Lista de asegurados |
| GET | `/v1/subscribers/:id` | Asegurado por ID |
| GET | `/v1/agents` | Lista de agentes |
| GET | `/v1/agents/:id` | Agente por ID |
| GET | `/v1/business-lines` | Ramos disponibles |
| GET | `/v1/zip-codes/:zipCode` | Zona de riesgo por CP |
| POST | `/v1/zip-codes/validate` | Validar código postal |
| GET | `/v1/catalogs/risk-classification` | Tipos de construcción |
| GET | `/v1/catalogs/guarantees` | Coberturas disponibles |
| GET | `/v1/tariffs/:type` | Tarifas (incendio/cat/fhm/equipo-electronico) |
| GET | `/health` | Health check |

## Arrancar

```bash
pnpm dev        # ts-node-dev con recarga en caliente
pnpm build      # Compilar TypeScript
pnpm start      # Ejecutar build compilado
```

## Agregar fixtures

Editar los archivos en `src/fixtures/` — los cambios se reflejan inmediatamente en modo `dev`.
