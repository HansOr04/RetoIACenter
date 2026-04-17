# Estrategia de Pruebas

## Índice

1. [Pirámide de testing](#1-pirámide-de-testing)
2. [Pruebas unitarias — Backend (JUnit 5)](#2-pruebas-unitarias--backend)
3. [Pruebas unitarias — Frontend (Jest + RTL)](#3-pruebas-unitarias--frontend)
4. [Pruebas de integración (Karate DSL)](#4-pruebas-de-integración-karate-dsl)
5. [Pruebas E2E — 3 flujos críticos (Playwright)](#5-pruebas-e2e--3-flujos-críticos)
6. [Pruebas de performance (k6 — opcional)](#6-pruebas-de-performance-k6)

---

## 1. Pirámide de testing

> TODO: insertar diagrama de la pirámide con conteos de tests por nivel

```
           /‾‾‾‾‾‾‾‾‾\
          /    E2E    \        3 flujos · Playwright
         /─────────────\
        / Integración   \      N escenarios · Karate DSL
       /─────────────────\
      /    Unitarias      \    ≥80% cobertura · JUnit 5 + Jest
     /─────────────────────\
```

---

## 2. Pruebas unitarias — Backend

> TODO: completar con clases de prueba por HU

### Cobertura objetivo: 80% líneas (JaCoCo)
### Ubicación: `apps/api/src/test/java/com/sofka/cotizador/`
### Casos por HU: ver tabla en README principal

---

## 3. Pruebas unitarias — Frontend

> TODO: completar con componentes y hooks a probar

### Cobertura objetivo: 80% statements (Jest)
### Ubicación: `apps/web/__tests__/`

---

## 4. Pruebas de integración (Karate DSL)

> TODO: completar con feature files por endpoint

### Ubicación: `tests/integration/src/test/java/features/`
### Ejecución: `cd tests/integration && mvn test`

---

## 5. Pruebas E2E — 3 flujos críticos

> TODO: completar con pasos detallados por flujo

### Flujo 1: Creación de folio y datos generales
- HUs: HU-001, HU-002, HU-F01, HU-F02
- Archivo: `tests/e2e/tests/flujo1-crear-folio.spec.ts`

### Flujo 2: Agregar ubicación y seleccionar coberturas
- HUs: HU-004, HU-006, HU-F04, HU-F05
- Archivo: `tests/e2e/tests/flujo2-ubicaciones-coberturas.spec.ts`

### Flujo 3: Calcular prima y cambiar estado
- HUs: HU-007, HU-008, HU-F06
- Archivo: `tests/e2e/tests/flujo3-calcular-prima.spec.ts`

---

## 6. Pruebas de performance (k6)

> TODO: definir SLAs y umbrales de alerta

### Script: `tests/performance/calculate_load.js`
### Ejecución: `k6 run tests/performance/calculate_load.js`
### SLAs objetivo:
- p95 latencia: < 2000 ms
- Tasa de error: < 5%
