# Lógica del cálculo de prima

> Documento técnico que describe paso a paso cómo el sistema calcula la prima neta y la prima comercial de una cotización. Este documento es el contrato de trazabilidad exigido por el criterio 8 de evaluación del reto.

## Índice

1. Principio general
2. Los 8 pasos del cálculo
3. Los 14 componentes de desglose
4. Tablas de referencia consultadas
5. Fórmulas literales aplicadas
6. Reglas de exclusión (ubicaciones incompletas)
7. Atomicidad y persistencia
8. Ejemplo numérico completo
9. Limitaciones y supuestos

---

## 1. Principio general

El cálculo opera sobre el agregado `Cotizacion` completo y produce tres salidas persistidas atómicamente:

- `primaNeta` — suma técnica de todas las primas por ubicación calculables
- `primaComercial` — prima neta multiplicada por un factor comercial parametrizado
- `primasPorUbicacion[]` — desglose individual por cada ubicación, incluyendo las no calculables con sus alertas

El cálculo NO replica una fórmula actuarial real (el reto explícitamente permite simplificaciones). La lógica es **consistente, trazable y documentada** según lo exigido.

## 2. Los 8 pasos del cálculo

Cada paso corresponde a un método o paso lógico en el proceso de cálculo de la prima (`CalculoPrimaService` y componentes relacionados):

1. **Recuperación o creación** — carga o inicializa el agregado de cotización.
2. **leerParametrosGlobales()** — consulta `cotizacion_parametros` (a través de `findParametrosCalculo`) para obtener `factorComercial`, `tasaExtension`, `tasaRemocion`, `tasaPerdidaRentas`, etc.
3. **resolverDatosTecnicosPorUbicacion(ubicacion)** — para cada ubicación, un proceso previo resuelve `zonaTev` y `zonaFhm` basado en el código postal (catálogo).
4. **esCalculable(ubicacion)** — valida si la ubicación es válida para continuar. Si no lo es, devuelve un desglose incalculable con sus alertas.
5. **calcularUbicacion(ubicacion, opcionesCobertura, catalogo)** — se ejecutan secuencialmente los 14 componentes de cálculo si las coberturas correspondientes están activadas en `OpcionesCobertura`.
6. **consolidarPrimaNetaTotal(primasUbicaciones)** — sumatoria agregada obteniendo la prima subtotal válida de cada ubicación.
7. **derivarPrimaComercialTotal(primaNeta, factorComercial)** — `primaComercial = primaNeta × factorComercial`
8. **persistirResultado(cotizacion)** — se hace UPDATE de los resultados en base de datos de manera consistente guardando la prima total resultante y actualizando estado.

## 3. Los 14 componentes de desglose

| # | Componente | Aplica cuando | Tabla fuente |
|---|---|---|---|
| 1 | Incendio edificios | `opcionesCobertura.incendioEdificios = true` | `cotizacion_tarifas_incendio` |
| 2 | Incendio contenidos | `incendioContenidos = true` | `cotizacion_tarifas_incendio` |
| 3 | Extensión de cobertura | `extensionCobertura = true` | `cotizacion_parametros` |
| 4 | CAT TEV | `catTev = true` y ubicación tiene `zonaTev` | `cotizacion_tarifas_cat_tev` |
| 5 | CAT FHM | `catFhm = true` y tiene `zonaFhm` | `cotizacion_tarifas_cat_fhm` |
| 6 | Remoción de escombros | `remocionEscombros = true` | `cotizacion_parametros` |
| 7 | Gastos extraordinarios | `gastosExtraordinarios = true` | `cotizacion_parametros` |
| 8 | Pérdida de rentas | `perdidaRentas = true` | `cotizacion_parametros` |
| 9 | Business Interruption (BI) | `bi = true` | `cotizacion_parametros` |
| 10 | Equipo electrónico | `equipoElectronico = true` | `cotizacion_factores_equipo` |
| 11 | Robo | `robo = true` | Costante en código (`ROBO_FACTOR`) |
| 12 | Dinero y valores | `dineroValores = true` | `cotizacion_parametros` |
| 13 | Vidrios | `vidrios = true` | `cotizacion_parametros` |
| 14 | Anuncios luminosos | `anunciosLuminosos = true` | `cotizacion_parametros` |

Si una cobertura no está activa en `opcionesCobertura`, el componente retorna `0.00` y no consulta su base tarifaria.

## 4. Tablas de referencia consultadas

### `cotizacion_parametros`

Fila única con parámetros globales (ver `V5__seed_catalogs_hu007.sql`):

| Campo | Ejemplo | Uso |
|---|---|---|
| factor_comercial | 1.25 | Multiplicador de prima neta → comercial |
| tasa_extension | 0.0020 | Aplica sobre incendio edificios + contenidos |
| tasa_remocion | 0.0010 | Aplica sobre incendio edificios |
| tasa_gastos_ext | 0.0015 | Aplica sobre incendio edificios |
| tasa_perdida_rentas | 0.0030 | Aplica sobre incendio edificios |
| tasa_bi | 0.0025 | Aplica sobre incendio edificios |
| tasa_dinero | 0.0040 | Aplica sobre incendio contenidos |
| tasa_vidrios | 0.0018 | Aplica sobre incendio edificios |
| tasa_anuncios | 0.0050 | Aplica sobre incendio edificios |

### `cotizacion_tarifas_incendio`

Tasas base por combinación `clave_incendio × tipo_constructivo`:

| clave_incendio | tipo_constructivo | tasa_edificios | tasa_contenidos |
|---|---|---|---|
| A1 | CONCRETO_ARMADO | 0.0012 | 0.0018 |
| B1 | CONCRETO_ARMADO | 0.0020 | 0.0030 |
| C1 | ACERO_ESTRUCTURAL | 0.0090 | 0.0120 |

### `cotizacion_tarifas_cat_tev`

Tasas CAT TEV por zona:

| zona_tev | tasa |
|---|---|
| TEV-A | 0.0008 |
| TEV-B | 0.0015 |
| TEV-C | 0.0025 |

### `cotizacion_tarifas_cat_fhm`

Tasas FHM por zona:

| zona_fhm | tasa |
|---|---|
| FHM-1 | 0.0005 |
| FHM-2 | 0.0010 |
| FHM-3 | 0.0018 |

### `cotizacion_factores_equipo`

Factores por `clase × nivel`:

| clase | nivel | factor |
|---|---|---|
| A | 1 | 0.0030 |
| B | 1 | 0.0045 |
| C | 1 | 0.0060 |

### `cotizacion_cp_zonas`

Resuelve código postal → zonas catastróficas:

| codigo_postal | zona_tev | zona_fhm |
|---|---|---|
| 170103 | TEV-B | FHM-2 |
| 170109 | TEV-D | FHM-2 |

## 5. Fórmulas literales aplicadas

Formula base común a componentes tarifados:

```
prima_componente = baseAsegurada × factorTarifario
```

Donde:
- `baseAsegurada` = valor por defecto `1_000_000` si el request no la envía, o derivada sumando componentes primos (ej: `primaIncendioEdificios`).

### Fórmulas por componente

**1. Incendio edificios**
```
tasa = cotizacion_tarifas_incendio[claveIncendio, tipoConstructivo].tasa_edificios
prima = valorAsegurado × tasa
```

**2. Incendio contenidos**
```
tasa = cotizacion_tarifas_incendio[claveIncendio, tipoConstructivo].tasa_contenidos
prima = valorAsegurado × tasa
```

**3. Extensión de cobertura**
```
prima = (primaIncendioEdificios + primaIncendioContenidos) × parametros.tasaExtension
```

**4. CAT TEV**
```
tasa = cotizacion_tarifas_cat_tev[zonaTev].tasa
prima = valorAsegurado × tasa
```

**5. CAT FHM**
```
tasa = cotizacion_tarifas_cat_fhm[zonaFhm].tasa
prima = valorAsegurado × tasa
```

**6. Remoción de escombros**
```
prima = primaIncendioEdificios × parametros.tasaRemocion
```

**7. Gastos extraordinarios**
```
prima = primaIncendioEdificios × parametros.tasaGastosExt
```

**8. Pérdida de rentas**
```
prima = primaIncendioEdificios × parametros.tasaPerdidaRentas
```

**9. BI (Business Interruption)**
```
prima = primaIncendioEdificios × parametros.tasaBi
```

**10. Equipo electrónico**
```
factor = cotizacion_factores_equipo[claseEquipo="B", nivelUbicacion].factor
// Fallback a 0.0045 si no existe coincidencia en el catálogo
prima = valorAsegurado × factor
```

**11. Robo**
```
prima = primaIncendioContenidos × ROBO_FACTOR // Constante 0.15 hardcodeada
```

**12. Dinero y valores**
```
prima = primaIncendioContenidos × parametros.tasaDinero
```

**13. Vidrios**
```
prima = primaIncendioEdificios × parametros.tasaVidrios
```

**14. Anuncios luminosos**
```
prima = primaIncendioEdificios × parametros.tasaAnuncios
```

### Total por ubicación

```
totalUbicacion = suma_componentes_activos
```

### Total de la cotización

```
primaNeta = suma_totales_ubicaciones_calculables
primaComercial = primaNeta × factorComercial
```

## 6. Reglas de exclusión (ubicaciones incompletas)

Una ubicación es excluible del cálculo y marcada como "incalculable" si no cumple reglas de negocio esenciales.
Aparece en `primasPorUbicacion[]` como no calculada con alertas (HU-010):

```json
{
  "indice": 1,
  "calculada": false,
  "total": 0.00,
  "desglose": null,
  "alertas": [
    { "codigo": "ERROR_VALIDACION_UBICACION", "mensaje": "..." }
  ]
}
```

## 7. Atomicidad y persistencia

La operación de persistencia de cálculo en `CalculoPrimaService` y casos de uso es **atómica**. Una única transacción (`@Transactional`) u operación de repositorio actualiza el agregado entero. Si cualquier paso falla, no hay un cálculo inconcluso financiero.

## 8. Ejemplo numérico completo

**Escenario:** 1 ubicación válida en Quito, CP 170103 (zona TEV-B, FHM-2), giro B1, tipo CONCRETO_ARMADO. Coberturas activas: incendio edificios, incendio contenidos, extensión, CAT TEV, remoción escombros, pérdida rentas. Suma asegurada default = 1,000,000.

| Componente | Cálculo | Resultado |
|---|---|---|
| Incendio edificios | 1,000,000 × 0.0020 | 2,000.00 |
| Incendio contenidos | 1,000,000 × 0.0030 | 3,000.00 |
| Extensión cobertura | (2,000.00 + 3,000.00) × 0.0020 | 10.00 |
| CAT TEV | 1,000,000 × 0.0015 | 1,500.00 |
| Remoción escombros | 2,000.00 × 0.0010 | 2.00 |
| Pérdida rentas | 2,000.00 × 0.0030 | 6.00 |
| **Total ubicación** | | **6,518.00** |

```
primaNeta = 6,518.00
primaComercial = 6,518.00 × 1.25 = 8,147.50
```

## 9. Limitaciones y supuestos

- **Suma asegurada default:** el diseño ignora un payload de valorAsegurado e invoca una constante local `1,000,000.00` desde el dominio.
- **Fórmula sin descuentos por garantías:** se suprimió en el código base inicial la matemática compleja con `(1 - descuentoGarantias)`. El multiplicador es lineal.
- **Factores de equipo electrónico y robo:** `clase` en el equipo electronico está invocada siempre con `"B"` independientemente del request (con fallback en 0.0045) y Robo llama a un `ROBO_FACTOR = 0.15` como constante interna.
- **Parametrización por derivado:** Varias coberturas (ej: remocion escombros, rentas, etc) aplican la tasa paramétrica base sobre `primaIncendioEdificios` en vez de usar un monto fijo o sumar por días de interrupción.
- **Moneda única:** todos los cálculos y campos asumen de facto operarse en una moneda de conversión uniforme (ej: USD).

## 10. Referencias

- Código: `apps/api/src/main/java/com/sofka/cotizador/domain/service/CalculoPrimaService.java`
- Seeders: `apps/api/src/main/resources/db/migration/V5__seed_catalogs_hu007.sql`
- Adaptador Base Datos: `CatalogoTarifasJpaAdapter.java`
