# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: flujo1-happy-path-completo.spec.ts >> Flujo 1 · Happy path completo del cotizador >> crea folio, captura datos, registra ubicaciones, configura coberturas y calcula prima
- Location: tests\flujo1-happy-path-completo.spec.ts:21:7

# Error details

```
Error: page.goto: net::ERR_CONNECTION_REFUSED at http://localhost:3000/
Call log:
  - navigating to "http://localhost:3000/", waiting until "load"

```

# Test source

```ts
  1   | import { test, expect } from '@playwright/test';
  2   | import { testData } from '../fixtures/test-data';
  3   | 
  4   | /**
  5   |  * FLUJO 1 · HAPPY PATH COMPLETO
  6   |  *
  7   |  * JUSTIFICACIÓN (criterio 7):
  8   |  * Este test cubre el escenario de aceptación oficial del reto
  9   |  * (página 14 del documento funcional), ejecutando los 10 pasos
  10  |  * de principio a fin a través de la interfaz de usuario.
  11  |  *
  12  |  * Sin este flujo no se puede afirmar que el sistema cumpla su
  13  |  * propósito principal. Es el único que valida integración REAL
  14  |  * entre las 3 aplicaciones (web, api, core-stub) y la persistencia.
  15  |  *
  16  |  * Riesgo que previene: regresiones en el camino principal del
  17  |  * negocio que pasarían desapercibidas en tests unitarios.
  18  |  */
  19  | 
  20  | test.describe('Flujo 1 · Happy path completo del cotizador', () => {
  21  |   test('crea folio, captura datos, registra ubicaciones, configura coberturas y calcula prima', async ({ page }) => {
  22  |     // Paso 1 · Entrar al cotizador y crear folio nuevo
> 23  |     await page.goto('/');
      |                ^ Error: page.goto: net::ERR_CONNECTION_REFUSED at http://localhost:3000/
  24  |     await page.click('text=Crear cotización'); // ajusta al texto real del botón
  25  |     await expect(page).toHaveURL(/\/cotizador\/[^/]+\/datos-generales/);
  26  | 
  27  |     // Captura el numero de folio desde la URL
  28  |     const url = page.url();
  29  |     const folio = url.match(/cotizador\/(F[\d-]+)/)?.[1];
  30  |     expect(folio).toBeTruthy();
  31  |     console.log(`Folio creado: ${folio}`);
  32  | 
  33  |     // Paso 2 · Capturar datos generales
  34  |     await page.fill('input[name="nombreTomador"]', testData.datosGenerales.nombreTomador);
  35  |     await page.fill('input[name="rucCedula"]', testData.datosGenerales.rucCedula);
  36  |     await page.fill('input[name="correoElectronico"]', testData.datosGenerales.correoElectronico);
  37  |     await page.selectOption('select[name="tipoInmueble"]', testData.datosGenerales.tipoInmueble);
  38  |     await page.selectOption('select[name="usoPrincipal"]', testData.datosGenerales.usoPrincipal);
  39  |     await page.fill('input[name="anoConstruccion"]', testData.datosGenerales.anoConstruccion);
  40  |     await page.fill('input[name="numeroPisos"]', testData.datosGenerales.numeroPisos);
  41  |     await page.click('button:has-text("Continuar")');
  42  | 
  43  |     // Paso 3 · Ir a layout
  44  |     await expect(page).toHaveURL(/\/layout/);
  45  |     await page.click('#btn-incremento-ubicaciones'); // Suma 1 para tener 2 ubicaciones
  46  |     await page.click('#btn-configurar-ubicaciones');
  47  | 
  48  |     // Paso 4 · Registrar ubicación completa
  49  |     await expect(page).toHaveURL(/\/ubicaciones/);
  50  | 
  51  |     const ub = testData.ubicacionCompleta;
  52  |     
  53  |     // Ubicación 1
  54  |     await page.fill('#ub0-nombre', ub.nombreUbicacion);
  55  |     await page.fill('#ub0-dir', ub.direccion);
  56  |     await page.selectOption('#ub0-cp', ub.codigoPostal);
  57  |     await page.fill('#ub0-ciudad', ub.ciudad);
  58  |     await page.selectOption('#ub0-tc', ub.tipoConstructivo);
  59  |     
  60  |     // Giro
  61  |     await page.fill('#ub0-gcod', 'G01');
  62  |     await page.fill('#ub0-gdesc', 'Oficinas');
  63  |     await page.selectOption('#ub0-ginc', ub.claveIncendio);
  64  |     
  65  |     // Garantías
  66  |     await page.fill('#ub0-garantias', ub.garantias.join(', '));
  67  |     
  68  |     // Lo mismo para la Ubicación 2 (para no fallar validaciones del server)
  69  |     await page.fill('#ub1-nombre', 'Ubicacion 2');
  70  |     await page.fill('#ub1-dir', 'Dir 2');
  71  |     await page.selectOption('#ub1-cp', ub.codigoPostal);
  72  |     await page.selectOption('#ub1-tc', ub.tipoConstructivo);
  73  |     await page.fill('#ub1-gcod', 'G02');
  74  |     await page.fill('#ub1-gdesc', 'Operaciones');
  75  |     await page.selectOption('#ub1-ginc', ub.claveIncendio);
  76  |     await page.fill('#ub1-garantias', ub.garantias.join(', '));
  77  | 
  78  |     await page.click('#btn-guardar-ubicaciones');
  79  | 
  80  |     // Paso 5 · Configurar coberturas
  81  |     await expect(page).toHaveURL(/\/coberturas/);
  82  | 
  83  |     // Activamos las adicionales necesarias (Incendio Edificios ya viene en true por defecto)
  84  |     await page.click('button:has-text("Incendio Contenidos")');
  85  |     await page.click('button:has-text("Catástrofe TEV")');
  86  |     await page.click('button:has-text("Remoción Escombros")');
  87  |     await page.click('button:has-text("Business Interruption")');
  88  |     await page.click('#btn-continuar-calculo');
  89  | 
  90  |     // Paso 6 · Ejecutar cálculo
  91  |     await expect(page).toHaveURL(/\/calculo/);
  92  |     await page.click('#btn-ejecutar-calculo');
  93  | 
  94  |     // Paso 7 · Validar resultado
  95  |     await expect(page.locator('text=/prima neta/i')).toBeVisible();
  96  |     await expect(page.locator('text=/prima comercial/i')).toBeVisible();
  97  | 
  98  |     // La prima debe ser mayor a cero (evita regresión del bug W-04)
  99  |     const primaNetaText = await page.locator('[data-testid="prima-neta"]').innerText();
  100 |     const primaNeta = parseFloat(primaNetaText.replace(/[^0-9.]/g, ''));
  101 |     expect(primaNeta).toBeGreaterThan(0);
  102 | 
  103 |     // Paso 8 · Validar desglose visible
  104 |     await expect(page.locator('text=/incendio edificios/i')).toBeVisible();
  105 |     await expect(page.locator('text=/incendio contenidos/i')).toBeVisible();
  106 |     await expect(page.locator('text=/CAT TEV/i')).toBeVisible();
  107 | 
  108 |     // Paso 9 · Ir al estado final
  109 |     await page.click('text=/estado/i');
  110 |     await expect(page).toHaveURL(/\/estado/);
  111 |     await expect(page.locator('text=/CALCULADA|CALCULADO/i')).toBeVisible();
  112 |   });
  113 | });
  114 | 
```