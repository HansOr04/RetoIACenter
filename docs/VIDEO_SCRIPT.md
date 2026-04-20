# Guion del video · Reto IA Center

> Video de demostración de ≤10 minutos. Publicación en modo **OCULTO** (unlisted) en YouTube — **NO PRIVADO** (descalifica según el reto).

## Checklist pre-grabación

- [ ] Stack levantado y estable (`docker compose ps` todo healthy)
- [ ] Datos de prueba limpios (crear un folio fresco para la demo)
- [ ] Verificado que prima > 0 en el cálculo
- [ ] IDE abierto con proyecto cargado
- [ ] Terminales preparados (al menos 2: uno para curls, uno para logs)
- [ ] Micrófono con buen audio
- [ ] Screen recording en calidad 1080p mínimo

## Guion · 10 minutos exactos

### 0:00 – 0:30 · Introducción (30s)

> "Hola, soy Hans, y les voy a mostrar mi entrega del Reto IA Center: un cotizador de daños construido con metodología ASDD. En los próximos 10 minutos van a ver el sistema funcionando end-to-end, la arquitectura, las pruebas, y la trazabilidad de cada HU al código."

### 0:30 – 2:00 · Demo funcional end-to-end (90s)

**Pantalla:** navegador en `http://localhost:3000`

Ejecutar en vivo el escenario de aceptación:

1. Click en "Crear cotización" → mostrar el folio generado
2. Llenar datos generales (rápido)
3. Configurar layout: 2 ubicaciones
4. Agregar ubicación 1 completa — **mostrar cómo aparece VÁLIDA**
5. Agregar ubicación 2 sin claveIncendio — **mostrar la alerta INCOMPLETA**
6. Configurar coberturas: incendio, CAT TEV, BI
7. Click "Calcular"
8. **Mostrar el desglose:** prima neta, prima comercial, ubicación 1 calculada con los 14 componentes, ubicación 2 con alertas
9. Ir a estado final → mostrar CALCULADA + progreso 100%

> "Fíjense que la ubicación 2 no bloqueó el cálculo de la 1. Eso cumple la regla obligatoria del reto: una ubicación incompleta genera alerta pero no impide calcular las demás."

### 2:00 – 3:30 · Arquitectura (90s)

**Pantalla:** IDE, mostrando la estructura del monorepo

> "El proyecto está organizado como monorepo con 3 aplicaciones..."

Mostrar:
- `apps/api/` con las 4 carpetas de Clean Architecture
- Ejecutar el grep que valida 0 violaciones:
  ```bash
  grep -rn "import.*infrastructure" apps/api/src/main/java/com/sofka/cotizador/domain/
  ```
  Resultado vacío → apuntar con el mouse y decir "cero violaciones"
- Abrir `CalculoPrimaService.java` y mostrar los 8 pasos

> "El dominio es puro Java, sin anotaciones de Spring. `CalculoPrimaService` se registra como `@Bean` en `DomainServicesConfig` para mantener esa pureza. Eso me permite testearlo sin arrancar el framework entero."

### 3:30 – 5:00 · Metodología ASDD en acción (90s)

**Pantalla:** `specs/` en el IDE

> "El reto exige usar ASDD. No como formalidad, sino como metodología real."

Mostrar en orden:
- `specs/config/config.yaml` → el perfil
- `specs/agents/agent_spec.prompt.md` → el agente que clasifica HUs
- `specs/guidelines/dev-guidelines.md` → las políticas que los agentes validan antes de generar
- `specs/features/HU-007-calculo-prima.md` → abrir, mostrar los criterios + INVEST + análisis técnico
- Buscar en el código:
  ```bash
  grep -rn "HU-007" apps/api/src/main/java/ | head -5
  ```
- `specs/output/backend/HU-007-report.md` → el reporte que el Backend Agent generó

> "Cada HU tiene trazabilidad: spec → código con comentarios HU-XXX → reporte de output. Cualquiera puede auditar qué implementa qué."

### 5:00 – 7:00 · Trazabilidad del cálculo (120s)

**Pantalla:** `docs/CALCULATION_LOGIC.md` + `CalculoPrimaService.java` + Postgres

> "El criterio de evaluación más crítico del reto es la trazabilidad del cálculo. Lo documenté completo."

Mostrar:
- Las 10 secciones de `CALCULATION_LOGIC.md`
- Los 14 componentes con sus fórmulas
- El ejemplo numérico que sale en la demo
- Ejecutar en Postgres en vivo:
  ```bash
  docker compose exec postgres psql -U cotizador -d cotizador -c "SELECT numero_folio, prima_neta, prima_comercial, version FROM cotizaciones WHERE numero_folio = 'F2026-XXXX';"
  ```
- Mostrar que los valores persistieron atómicamente

### 7:00 – 8:30 · Pruebas (90s)

**Pantalla:** terminal

> "La entrega tiene 4 niveles de tests."

Ejecutar brevemente cada uno:

```bash
# Unit backend
cd apps/api && ./mvnw test | tail -5

# Unit frontend
cd apps/web && pnpm test | tail -5

# Integration Karate
cd tests/integration && mvn test | tail -10
# Abrir el reporte HTML: target/karate-reports/karate-summary.html

# E2E
cd tests/e2e && pnpm exec playwright test --reporter=list | tail -5
```

> "Los 3 flujos E2E están justificados en TESTING_STRATEGY.md: happy path, ubicación incompleta, y versionado optimista. No son flujos al azar."

Abrir `tests/e2e/tests/flujo2-ubicacion-incompleta-no-bloquea.spec.ts` y leer en voz alta la justificación del JSDoc.

### 8:30 – 9:30 · Decisiones técnicas destacables (60s)

> "Tres decisiones que vale la pena resaltar:"

1. **JSONB para ubicaciones** — 20 segundos
   > "No modelé 10 tablas hijas. Usé JSONB con jsonb_set para update parcial atómico. Trade-off documentado en ARCHITECTURE.md."

2. **Circuit breaker con Resilience4j** — 20 segundos
   > "El backend no se cae si el core-stub falla. Circuit breaker abre al 50% de error y devuelve 503 controlado."

3. **Optimistic locking dual (Folio + Cotización)** — 20 segundos
   > "Separé identidad de contenido. Permite ediciones concurrentes en datos generales y ubicaciones sin chocar. Documentado en ARCHITECTURE.md sección 7."

### 9:30 – 10:00 · Cierre (30s)

> "En resumen: 13 endpoints implementados, 16 HUs documentadas con INVEST, 4 niveles de tests, Clean Architecture sin violaciones, y ASDD aplicado con trazabilidad real. Todo está en GitLab de Sofka. Gracias por su tiempo."

**Pantalla final:** el README.md principal en el navegador, centrado en los badges y la sección de adopción de ASDD.

## Consejos de grabación

- **Habla pausado.** 10 minutos se vuelven cortos cuando te aceleras.
- **Lee el guion 2 veces antes de grabar.** Identifica dónde está la cámara del cursor.
- **Graba por segmentos** si no te da confianza hacer 10 minutos corridos. Edita con cortes limpios.
- **No uses zoom excesivo.** Deja que se vea el contexto de la pantalla.
- **Narrativa > perfección.** Si algo falla en vivo, reconócelo rápido y sigue: "ah, acá veo una race condition, se arregla con…" es más convincente que parecer robot.
- **Sube en modo UNLISTED/OCULTO**, no privado. Privado descalifica según el reto.

## URL del video

> Pega aquí cuando lo subas: `https://youtu.be/XXXXX`
