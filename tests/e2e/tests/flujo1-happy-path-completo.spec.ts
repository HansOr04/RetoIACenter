import { test, expect } from '@playwright/test';
import { testData } from '../fixtures/test-data';

/**
 * FLUJO 1 · HAPPY PATH COMPLETO
 *
 * JUSTIFICACIÓN (criterio 7):
 * Este test cubre el escenario de aceptación oficial del reto
 * (página 14 del documento funcional), ejecutando los 10 pasos
 * de principio a fin a través de la interfaz de usuario.
 *
 * Sin este flujo no se puede afirmar que el sistema cumpla su
 * propósito principal. Es el único que valida integración REAL
 * entre las 3 aplicaciones (web, api, core-stub) y la persistencia.
 *
 * Riesgo que previene: regresiones en el camino principal del
 * negocio que pasarían desapercibidas en tests unitarios.
 */

test.describe('Flujo 1 · Happy path completo del cotizador', () => {
  test('crea folio, captura datos, registra ubicaciones, configura coberturas y calcula prima', async ({ page }) => {
    // Paso 1 · Entrar al cotizador y crear folio nuevo
    await page.goto('/');
    await page.click('text=Crear cotización'); // ajusta al texto real del botón
    await expect(page).toHaveURL(/\/cotizador\/[^/]+\/datos-generales/);

    // Captura el numero de folio desde la URL
    const url = page.url();
    const folio = url.match(/cotizador\/(F[\d-]+)/)?.[1];
    expect(folio).toBeTruthy();
    console.log(`Folio creado: ${folio}`);

    // Paso 2 · Capturar datos generales
    await page.fill('input[name="nombreTomador"]', testData.datosGenerales.nombreTomador);
    await page.fill('input[name="rucCedula"]', testData.datosGenerales.rucCedula);
    await page.fill('input[name="correoElectronico"]', testData.datosGenerales.correoElectronico);
    await page.selectOption('select[name="tipoInmueble"]', testData.datosGenerales.tipoInmueble);
    await page.selectOption('select[name="usoPrincipal"]', testData.datosGenerales.usoPrincipal);
    await page.fill('input[name="anoConstruccion"]', testData.datosGenerales.anoConstruccion);
    await page.fill('input[name="numeroPisos"]', testData.datosGenerales.numeroPisos);
    await page.click('button:has-text("Continuar")');

    // Paso 3 · Ir a layout
    await expect(page).toHaveURL(/\/layout/);
    await page.click('#btn-incremento-ubicaciones'); // Suma 1 para tener 2 ubicaciones
    await page.click('#btn-configurar-ubicaciones');

    // Paso 4 · Registrar ubicación completa
    await expect(page).toHaveURL(/\/ubicaciones/);

    const ub = testData.ubicacionCompleta;
    
    // Ubicación 1
    await page.fill('#ub0-nombre', ub.nombreUbicacion);
    await page.fill('#ub0-dir', ub.direccion);
    await page.selectOption('#ub0-cp', ub.codigoPostal);
    await page.fill('#ub0-ciudad', ub.ciudad);
    await page.selectOption('#ub0-tc', ub.tipoConstructivo);
    
    // Giro
    await page.fill('#ub0-gcod', 'G01');
    await page.fill('#ub0-gdesc', 'Oficinas');
    await page.selectOption('#ub0-ginc', ub.claveIncendio);
    
    // Garantías
    await page.fill('#ub0-garantias', ub.garantias.join(', '));
    
    // Lo mismo para la Ubicación 2 (para no fallar validaciones del server)
    await page.fill('#ub1-nombre', 'Ubicacion 2');
    await page.fill('#ub1-dir', 'Dir 2');
    await page.selectOption('#ub1-cp', ub.codigoPostal);
    await page.selectOption('#ub1-tc', ub.tipoConstructivo);
    await page.fill('#ub1-gcod', 'G02');
    await page.fill('#ub1-gdesc', 'Operaciones');
    await page.selectOption('#ub1-ginc', ub.claveIncendio);
    await page.fill('#ub1-garantias', ub.garantias.join(', '));

    await page.click('#btn-guardar-ubicaciones');

    // Paso 5 · Configurar coberturas
    await expect(page).toHaveURL(/\/coberturas/);

    // Activamos las adicionales necesarias (Incendio Edificios ya viene en true por defecto)
    await page.click('button:has-text("Incendio Contenidos")');
    await page.click('button:has-text("Catástrofe TEV")');
    await page.click('button:has-text("Remoción Escombros")');
    await page.click('button:has-text("Business Interruption")');
    await page.click('#btn-continuar-calculo');

    // Paso 6 · Ejecutar cálculo
    await expect(page).toHaveURL(/\/calculo/);
    await page.click('#btn-ejecutar-calculo');

    // Paso 7 · Validar resultado
    await expect(page.locator('text=/prima neta/i')).toBeVisible();
    await expect(page.locator('text=/prima comercial/i')).toBeVisible();

    // La prima debe ser mayor a cero (evita regresión del bug W-04)
    const primaNetaText = await page.locator('[data-testid="prima-neta"]').innerText();
    const primaNeta = parseFloat(primaNetaText.replace(/[^0-9.]/g, ''));
    expect(primaNeta).toBeGreaterThan(0);

    // Paso 8 · Validar desglose visible
    await expect(page.locator('text=/incendio edificios/i')).toBeVisible();
    await expect(page.locator('text=/incendio contenidos/i')).toBeVisible();
    await expect(page.locator('text=/CAT TEV/i')).toBeVisible();

    // Paso 9 · Ir al estado final
    await page.click('text=/estado/i');
    await expect(page).toHaveURL(/\/estado/);
    await expect(page.locator('text=/CALCULADA|CALCULADO/i')).toBeVisible();
  });
});
