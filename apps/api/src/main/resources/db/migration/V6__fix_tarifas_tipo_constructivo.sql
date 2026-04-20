-- V6: Alinear valores de tipo constructivo con el dominio en tarifas_incendio y cotizacion_tarifas_incendio

-- Actualiza tarifas_incendio (de V1/V2)
UPDATE tarifas_incendio SET tipo_construccion = 'CONCRETO_ARMADO' WHERE tipo_construccion = 'SOLIDA';
UPDATE tarifas_incendio SET tipo_construccion = 'MAMPOSTERIA' WHERE tipo_construccion = 'MIXTA';
UPDATE tarifas_incendio SET tipo_construccion = 'ACERO_ESTRUCTURAL' WHERE tipo_construccion = 'MADERA';

-- Agregar fila faltante en tarifas_incendio para escenario B y tipo constructivo 3
INSERT INTO tarifas_incendio (zona_riesgo, tipo_construccion, tasa_base) 
VALUES ('B', 'ACERO_ESTRUCTURAL', 0.0050)
ON CONFLICT DO NOTHING;

-- Actualiza cotizacion_tarifas_incendio (de V5) en caso aplicable
UPDATE cotizacion_tarifas_incendio SET tipo_constructivo = 'ACERO_ESTRUCTURAL' WHERE tipo_constructivo = 'MADERA';
