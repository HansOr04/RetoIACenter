# Guion del Video de Presentación

**Duración máxima**: 10 minutos  
**Formato**: YouTube modo OCULTO (no privado)

## Índice

1. [Apertura (0:00 – 0:30)](#1-apertura)
2. [Demo en vivo — Flujo completo de cotización (0:30 – 5:00)](#2-demo-en-vivo)
3. [Arquitectura y metodología ASDD (5:00 – 7:30)](#3-arquitectura-y-metodología)
4. [Pruebas automatizadas (7:30 – 9:00)](#4-pruebas-automatizadas)
5. [Cierre (9:00 – 10:00)](#5-cierre)

---

## 1. Apertura (0:00 – 0:30)

> TODO: escribir texto de presentación

- Presentación personal: nombre, rol, Sofka Technologies
- Contexto: Reto IA Center 2026 — Cotizador de Daños
- Qué verá el evaluador en los próximos 10 minutos

---

## 2. Demo en vivo (0:30 – 5:00)

> TODO: definir los pasos exactos del demo con datos de ejemplo

### Paso 1: Crear folio (HU-F01)
- Acción: clic en "Crear cotización"
- Resultado esperado: folio creado con número F2026-XXXX

### Paso 2: Capturar datos generales (HU-F02)
- Acción: completar formulario (RFC, agente, ramo)
- Resultado esperado: datos guardados, navegación al siguiente paso

### Paso 3: Configurar ubicación (HU-F04)
- Acción: agregar ubicación con CP de Quito
- Resultado esperado: zona de riesgo cargada automáticamente desde core-stub

### Paso 4: Seleccionar coberturas (HU-F05)
- Acción: activar Incendio + Sismo + FHM
- Resultado esperado: coberturas seleccionadas, subtotal visible

### Paso 5: Calcular prima (HU-F06)
- Acción: clic en "Calcular"
- Resultado esperado: prima total y desglose por ubicación

---

## 3. Arquitectura y metodología ASDD (5:00 – 7:30)

> TODO: preparar slides o pantalla del IDE con estructura de carpetas

- Mostrar estructura del monorepo
- Explicar las 4 capas de Clean Architecture en el backend
- Mostrar un spec ASDD completo (HU-001) y su trazabilidad al código

---

## 4. Pruebas automatizadas (7:30 – 9:00)

> TODO: ejecutar en vivo o mostrar resultado de CI

- Ejecutar `./mvnw test` y mostrar reporte JaCoCo (≥80%)
- Mostrar un feature file de Karate ejecutándose
- Mostrar un test Playwright ejecutándose en modo headed

---

## 5. Cierre (9:00 – 10:00)

> TODO: escribir texto de cierre

- Resumen de entregables cumplidos
- Lecciones aprendidas sobre ASDD en un reto individual
- Próximos pasos si hubiera más tiempo (autenticación, notificaciones, reportes PDF)
