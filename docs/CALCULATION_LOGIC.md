# Lógica de Cálculo de Prima

## Índice

1. [Visión general de la fórmula](#1-visión-general-de-la-fórmula)
2. [Cobertura de Incendio](#2-cobertura-de-incendio)
3. [Coberturas Catastróficas (CAT)](#3-coberturas-catastróficas-cat)
4. [Fenómenos Hidrometeorológicos (FHM)](#4-fenómenos-hidrometeorológicos-fhm)
5. [Coberturas adicionales](#5-coberturas-adicionales)
6. [Cálculo del total y aplicación de recargos](#6-cálculo-del-total-y-aplicación-de-recargos)

---

## 1. Visión general de la fórmula

> TODO: describir la estructura general del cálculo actuarial simplificado

```
Prima Neta Ubicación = Suma de (Valor Asegurado × Tasa por Cobertura × Factor de Zona × Factor de Riesgo)
Prima Total = Suma de Primas Netas + Derecho de Póliza
Prima con IVA = Prima Total × (1 + IVA)
```

---

## 2. Cobertura de Incendio

> TODO: definir la fórmula detallada con fuentes de tarifas

- **Tabla de tarifas**: `tarifas_incendio` (zona, tipo de construcción → tasa)
- **Valor asegurado**: valor de construcción del inmueble
- **Factor adicional**: tipo de uso (habitacional, comercial, industrial)

---

## 3. Coberturas Catastróficas (CAT)

> TODO: definir cálculo para sismo e inundación

- **Tabla de tarifas**: `tarifas_cat` (zona sísmica, peril → tasa)
- **Aplica sobre**: valor total del inmueble

---

## 4. Fenómenos Hidrometeorológicos (FHM)

> TODO: definir cálculo FHM

- **Tabla de tarifas**: `tarifa_fhm` (zona FHM → tasa)
- **Aplica sobre**: suma de valores asegurados en ubicación

---

## 5. Coberturas adicionales

> TODO: definir lógica para Robo, Equipo Electrónico, TEV

### 5.1 Robo con Violencia
### 5.2 Equipo Electrónico
### 5.3 TEV (Terrorismo, Explosión, Vandalismo)

---

## 6. Cálculo del total y aplicación de recargos

> TODO: detallar la consolidación de primas por ubicación y aplicación de derechos e IVA

- Derecho de póliza: parámetro en tabla `parametros_calculo` (clave `DERECHO_POLIZA`)
- Recargo por pago fraccionado: parámetro `RECARGO_PAGO_FRACCION`
- IVA Ecuador: parámetro `IVA` (15%)
