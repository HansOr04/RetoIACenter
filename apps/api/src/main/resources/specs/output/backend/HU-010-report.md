# HU-010 — Validación y Alertas de Ubicaciones Incompletas

## Estado: Implementado (cubierto por ValidadorUbicacion en HU-004)

## Servicio de dominio
`domain/service/ValidadorUbicacion.java` evalúa 6 reglas de validación en orden:

| Código alerta | Condición | Campo afectado |
|---------------|-----------|----------------|
| `CODIGO_POSTAL_INVALIDO` | `zonaCatastrofica == null` | `codigoPostal` |
| `FALTA_CLAVE_INCENDIO` | `giro == null` o `claveIncendio` en blanco | `giro.claveIncendio` |
| `SIN_GARANTIAS_TARIFABLES` | `garantias == null` o vacío | `garantias` |
| `ZONA_SIN_TARIFA` | `zonaCatastrofica != null` pero `zonaTev` o `zonaFhm` en blanco | `zonaCatastrofica` |
| `GIRO_NO_CATALOGADO` | `giro != null` pero `codigo` en blanco | `giro.codigo` |
| `TIPO_CONSTRUCTIVO_INVALIDO` | `tipoConstructivo` en blanco | `tipoConstructivo` |

## EstadoValidacionUbicacion
- `VALIDO` → ninguna alerta bloqueante
- `INCOMPLETO` → una o más alertas presentes

## Método `esCalculable()`
`Ubicacion.esCalculable()` devuelve `true` cuando `alertasBloqueantes.isEmpty()`.  
La diferencia con `VALIDO` es que podría haber alertas no bloqueantes en el futuro; actualmente son equivalentes.

## Endpoint de resumen
`GET /api/v1/quotes/{folio}/locations/summary` retorna:
- `total` — número total de ubicaciones
- `completas` — count con `estadoValidacion == VALIDO`
- `incompletas` — count con `estadoValidacion != VALIDO`
- `calculables` — count donde `esCalculable() == true`
- `indicesIncompletos` — lista de índices con estado no VALIDO
- `detalleIncompletas` — indice + nombreUbicacion + lista de alertas por cada incompleta
