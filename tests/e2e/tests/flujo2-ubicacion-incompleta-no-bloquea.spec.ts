import { test, expect } from '@playwright/test';
import { testData } from '../fixtures/test-data';

/**
 * FLUJO 2 · UBICACIÓN INCOMPLETA NO BLOQUEA CÁLCULO
 *
 * JUSTIFICACIÓN (criterio 7):
 * El documento funcional del reto establece explícitamente:
 * "si una ubicación está incompleta, esta ubicación genera alerta,
 * pero no debe impedir calcular las demás".
 *
 * Es la regla de negocio más delicada porque cualquier refactor futuro
 * del servicio de cálculo puede romperla silenciosamente. Sin este test
 * un sistema productivo podría dejar de cotizar cuando un usuario tiene
 * múltiples ubicaciones y una está incompleta, bloqueando el negocio.
 *
 * Este flujo cubre HU-010 (ubicaciones incompletas) y la integración
 * con HU-007 (cálculo).
 */

test.describe('Flujo 2 · Ubicación incompleta no bloquea cálculo', () => {
  test('con 2 ubicaciones, 1 completa y 1 incompleta, la completa calcula y la incompleta genera alerta', async ({ page }) => {
    // Arrancar desde home
    await page.goto('/');
    await page.click('text=Crear cotización');

    // Captura de folio
    const url = page.url();
    const folio = url.match(/cotizador\/(F[\d-]+)/)?.[1];
    console.log(`Folio: ${folio}`);

    // Datos generales (mínimo para avanzar)
    await page.fill('input[name="nombreTomador"]', 'Empresa Test');
    await page.fill('input[name="rucCedula"]', '1792146739001');
    await page.fill('input[name="correoElectronico"]', 'contacto@test.com');
    await page.selectOption('select[name="tipoInmueble"]', 'EDIFICIO');
    await page.selectOption('select[name="usoPrincipal"]', 'COMERCIAL');
    await page.click('button:has-text("Continuar")');

    // Layout: 2 ubicaciones
    await page.click('#btn-incremento-ubicaciones');
    await page.click('#btn-configurar-ubicaciones');

    // Ubicación 1 · COMPLETA
    const ub1 = testData.ubicacionCompleta;
    await page.fill('#ub0-nombre', ub1.nombreUbicacion);
    await page.fill('#ub0-dir', ub1.direccion);
    await page.selectOption('#ub0-cp', ub1.codigoPostal);
    await page.fill('#ub0-ciudad', ub1.ciudad);
    await page.selectOption('#ub0-tc', ub1.tipoConstructivo);
    await page.fill('#ub0-gcod', 'G01');
    await page.fill('#ub0-gdesc', 'Oficinas');
    await page.selectOption('#ub0-ginc', ub1.claveIncendio);
    await page.fill('#ub0-garantias', ub1.garantias.join(', '));

    // Ubicación 2 · INCOMPLETA (sin claveIncendio, ni garantías tarifables)
    const ub2 = testData.ubicacionIncompleta;
    await page.fill('#ub1-nombre', ub2.nombreUbicacion);
    await page.fill('#ub1-dir', ub2.direccion);
    await page.selectOption('#ub1-cp', ub2.codigoPostal);
    await page.fill('#ub1-ciudad', ub2.ciudad);
    await page.selectOption('#ub1-tc', ub2.tipoConstructivo);
    await page.fill('#ub1-gcod', 'G02');
    await page.fill('#ub1-gdesc', 'Incompleta');
    // Deliberadamente no se asigna 'claveIncendio' ni 'garantias'

    await page.click('#btn-guardar-ubicaciones');

    // Coberturas
    await expect(page).toHaveURL(/\/coberturas/);
    await page.click('button:has-text("Incendio Contenidos")'); // Activa incendio contenidos. Edificios ya está on.
    await page.click('#btn-continuar-calculo');

    // Cálculo
    await expect(page).toHaveURL(/\/calculo/);
    await page.click('#btn-ejecutar-calculo');

    // Validaciones clave:
    // 1. El cálculo NO falla con 422
    await expect(page.locator('text=/error|fallo|422/i')).not.toBeVisible();

    // 2. Prima neta > 0 (la ubicación completa aportó)
    const primaNeta = await page.locator('[data-testid="prima-neta"]').innerText();
    expect(parseFloat(primaNeta.replace(/[^0-9.]/g, ''))).toBeGreaterThan(0);

    // 3. En el desglose hay una ubicación calculada y una con alertas
    const ubicacionCalculada = page.locator('[data-testid="ubicacion-0"]');
    const ubicacionConAlerta = page.locator('[data-testid="ubicacion-1"]');

    await expect(ubicacionCalculada.locator('text=/calculada|válida/i')).toBeVisible();
    await expect(ubicacionConAlerta.locator('text=/alerta|incompleta|no calculada|incalculable/i')).toBeVisible();
  });
});
