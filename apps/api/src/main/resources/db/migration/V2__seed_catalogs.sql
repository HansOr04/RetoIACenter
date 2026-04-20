-- V2: Datos semilla de catálogos para Ecuador

-- ─── Parámetros de cálculo ──────────────────────────────────────────────────
INSERT INTO parametros_calculo (clave, valor, descripcion) VALUES
('DERECHO_POLIZA',        25.00,   'Derecho de póliza fijo en USD'),
('RECARGO_PAGO_FRACCION', 0.08,    'Recargo por pago fraccionado (8%)'),
('TASA_MINIMA_INCENDIO',  0.0015,  'Tasa mínima de incendio por millar'),
('FACTOR_DEDUCIBLE_BASE', 0.05,    'Factor de deducible base (5%)'),
('IVA',                   0.15,    'IVA Ecuador vigente (15%)');

-- ─── Tarifas de incendio ─────────────────────────────────────────────────────
INSERT INTO tarifas_incendio (zona_riesgo, tipo_construccion, tasa_base) VALUES
('A', 'CONCRETO_ARMADO',       0.0015),
('A', 'MAMPOSTERIA',           0.0025),
('A', 'ACERO_ESTRUCTURAL',     0.0050),
('B', 'CONCRETO_ARMADO',       0.0020),
('B', 'MAMPOSTERIA',           0.0035),
('B', 'ACERO_ESTRUCTURAL',     0.0050);

-- ─── Tarifas de catástrofe ───────────────────────────────────────────────────
INSERT INTO tarifas_cat (zona_riesgo, peril, tasa_base) VALUES
('1', 'SISMO',       0.0020),
('1', 'INUNDACION',  0.0015),
('2', 'SISMO',       0.0035),
('2', 'INUNDACION',  0.0025),
('3', 'SISMO',       0.0060);

-- ─── Tarifas FHM ─────────────────────────────────────────────────────────────
INSERT INTO tarifa_fhm (zona_riesgo, tasa_base) VALUES
('COSTA',    0.0030),
('SIERRA',   0.0015),
('ORIENTE',  0.0025),
('GALAP',    0.0010),
('URBANO',   0.0020);

-- ─── Factores equipo electrónico ────────────────────────────────────────────
INSERT INTO factores_equipo_electronico (rango_valor_desde, rango_valor_hasta, factor) VALUES
(0,        10000,   0.0030),
(10001,    50000,   0.0025),
(50001,    200000,  0.0020),
(200001,   500000,  0.0018),
(500001,   NULL,    0.0015);

-- ─── Códigos postales Ecuador (ciudades principales) ────────────────────────
INSERT INTO catalogo_cp_zonas (codigo_postal, municipio, estado, pais, zona_riesgo_incendio, zona_riesgo_sismo) VALUES
('170101', 'Quito',       'Pichincha',  'ECU', 'A', '2'),
('170515', 'Quito Norte', 'Pichincha',  'ECU', 'A', '2'),
('090101', 'Guayaquil',   'Guayas',     'ECU', 'B', '1'),
('010101', 'Cuenca',      'Azuay',      'ECU', 'A', '2'),
('180101', 'Ambato',      'Tungurahua', 'ECU', 'A', '3');

-- ─── Zonas TEV ──────────────────────────────────────────────────────────────
INSERT INTO dim_zona_tev (zona_codigo, descripcion, factor_tev) VALUES
('TEV-1', 'Riesgo alto TEV (centros urbanos)',    0.0008),
('TEV-2', 'Riesgo medio TEV (ciudades medianas)', 0.0005),
('TEV-3', 'Riesgo bajo TEV (áreas rurales)',      0.0002);

-- ─── Zonas FHM ──────────────────────────────────────────────────────────────
INSERT INTO dim_zona_fhm (zona_codigo, descripcion, factor_fhm) VALUES
('FHM-COSTA',   'Zona costera inundable',       0.0035),
('FHM-SIERRA',  'Zona sierra volcánica',         0.0020),
('FHM-ORIENTE', 'Zona amazónica alta lluvia',    0.0030),
('FHM-GALAP',   'Galápagos',                     0.0010),
('FHM-URBANO',  'Zona urbana drenaje controlado', 0.0015);
