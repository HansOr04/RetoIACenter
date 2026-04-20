# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: flujo2-ubicacion-incompleta-no-bloquea.spec.ts >> Flujo 2 · Ubicación incompleta no bloquea cálculo >> con 2 ubicaciones, 1 completa y 1 incompleta, la completa calcula y la incompleta genera alerta
- Location: tests\flujo2-ubicacion-incompleta-no-bloquea.spec.ts:22:7

# Error details

```
Error: page.goto: net::ERR_CONNECTION_REFUSED at http://localhost:3000/
Call log:
  - navigating to "http://localhost:3000/", waiting until "load"

```

# Test source

```ts
  1  | import { test, expect } from '@playwright/test';
  2  | import { testData } from '../fixtures/test-data';
  3  | 
  4  | /**
  5  |  * FLUJO 2 · UBICACIÓN INCOMPLETA NO BLOQUEA CÁLCULO
  6  |  *
  7  |  * JUSTIFICACIÓN (criterio 7):
  8  |  * El documento funcional del reto establece explícitamente:
  9  |  * "si una ubicación está incompleta, esta ubicación genera alerta,
  10 |  * pero no debe impedir calcular las demás".
  11 |  *
  12 |  * Es la regla de negocio más delicada porque cualquier refactor futuro
  13 |  * del servicio de cálculo puede romperla silenciosamente. Sin este test
  14 |  * un sistema productivo podría dejar de cotizar cuando un usuario tiene
  15 |  * múltiples ubicaciones y una está incompleta, bloqueando el negocio.
  16 |  *
  17 |  * Este flujo cubre HU-010 (ubicaciones incompletas) y la integración
  18 |  * con HU-007 (cálculo).
  19 |  */
  20 | 
  21 | test.describe('Flujo 2 · Ubicación incompleta no bloquea cálculo', () => {
  22 |   test('con 2 ubicaciones, 1 completa y 1 incompleta, la completa calcula y la incompleta genera alerta', async ({ page }) => {
  23 |     // Arrancar desde home
> 24 |     await page.goto('/');
     |                ^ Error: page.goto: net::ERR_CONNECTION_REFUSED at http://localhost:3000/
  25 |     await page.click('text=Crear cotización');
  26 | 
  27 |     // Captura de folio
  28 |     const url = page.url();
  29 |     const folio = url.match(/cotizador\/(F[\d-]+)/)?.[1];
  30 |     console.log(`Folio: ${folio}`);
  31 | 
  32 |     // Datos generales (mínimo para avanzar)
  33 |     await page.fill('input[name="nombreTomador"]', 'Empresa Test');
  34 |     await page.fill('input[name="rucCedula"]', '1792146739001');
  35 |     await page.fill('input[name="correoElectronico"]', 'contacto@test.com');
  36 |     await page.selectOption('select[name="tipoInmueble"]', 'EDIFICIO');
  37 |     await page.selectOption('select[name="usoPrincipal"]', 'COMERCIAL');
  38 |     await page.click('button:has-text("Continuar")');
  39 | 
  40 |     // Layout: 2 ubicaciones
  41 |     await page.click('#btn-incremento-ubicaciones');
  42 |     await page.click('#btn-configurar-ubicaciones');
  43 | 
  44 |     // Ubicación 1 · COMPLETA
  45 |     const ub1 = testData.ubicacionCompleta;
  46 |     await page.fill('#ub0-nombre', ub1.nombreUbicacion);
  47 |     await page.fill('#ub0-dir', ub1.direccion);
  48 |     await page.selectOption('#ub0-cp', ub1.codigoPostal);
  49 |     await page.fill('#ub0-ciudad', ub1.ciudad);
  50 |     await page.selectOption('#ub0-tc', ub1.tipoConstructivo);
  51 |     await page.fill('#ub0-gcod', 'G01');
  52 |     await page.fill('#ub0-gdesc', 'Oficinas');
  53 |     await page.selectOption('#ub0-ginc', ub1.claveIncendio);
  54 |     await page.fill('#ub0-garantias', ub1.garantias.join(', '));
  55 | 
  56 |     // Ubicación 2 · INCOMPLETA (sin claveIncendio, ni garantías tarifables)
  57 |     const ub2 = testData.ubicacionIncompleta;
  58 |     await page.fill('#ub1-nombre', ub2.nombreUbicacion);
  59 |     await page.fill('#ub1-dir', ub2.direccion);
  60 |     await page.selectOption('#ub1-cp', ub2.codigoPostal);
  61 |     await page.fill('#ub1-ciudad', ub2.ciudad);
  62 |     await page.selectOption('#ub1-tc', ub2.tipoConstructivo);
  63 |     await page.fill('#ub1-gcod', 'G02');
  64 |     await page.fill('#ub1-gdesc', 'Incompleta');
  65 |     // Deliberadamente no se asigna 'claveIncendio' ni 'garantias'
  66 | 
  67 |     await page.click('#btn-guardar-ubicaciones');
  68 | 
  69 |     // Coberturas
  70 |     await expect(page).toHaveURL(/\/coberturas/);
  71 |     await page.click('button:has-text("Incendio Contenidos")'); // Activa incendio contenidos. Edificios ya está on.
  72 |     await page.click('#btn-continuar-calculo');
  73 | 
  74 |     // Cálculo
  75 |     await expect(page).toHaveURL(/\/calculo/);
  76 |     await page.click('#btn-ejecutar-calculo');
  77 | 
  78 |     // Validaciones clave:
  79 |     // 1. El cálculo NO falla con 422
  80 |     await expect(page.locator('text=/error|fallo|422/i')).not.toBeVisible();
  81 | 
  82 |     // 2. Prima neta > 0 (la ubicación completa aportó)
  83 |     const primaNeta = await page.locator('[data-testid="prima-neta"]').innerText();
  84 |     expect(parseFloat(primaNeta.replace(/[^0-9.]/g, ''))).toBeGreaterThan(0);
  85 | 
  86 |     // 3. En el desglose hay una ubicación calculada y una con alertas
  87 |     const ubicacionCalculada = page.locator('[data-testid="ubicacion-0"]');
  88 |     const ubicacionConAlerta = page.locator('[data-testid="ubicacion-1"]');
  89 | 
  90 |     await expect(ubicacionCalculada.locator('text=/calculada|válida/i')).toBeVisible();
  91 |     await expect(ubicacionConAlerta.locator('text=/alerta|incompleta|no calculada/i')).toBeVisible();
  92 |   });
  93 | });
  94 | 
```