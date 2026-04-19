-- HU-007 calculation catalog tables
-- Uses prefixed names (cotizacion_*) to avoid conflict with V1 tables

CREATE TABLE IF NOT EXISTS cotizacion_parametros (
  id                  SERIAL PRIMARY KEY,
  factor_comercial    NUMERIC(5,4)  NOT NULL DEFAULT 1.25,
  tasa_extension      NUMERIC(7,6)  NOT NULL DEFAULT 0.002,
  tasa_remocion       NUMERIC(7,6)  NOT NULL DEFAULT 0.001,
  tasa_gastos_ext     NUMERIC(7,6)  NOT NULL DEFAULT 0.0015,
  tasa_perdida_rentas NUMERIC(7,6)  NOT NULL DEFAULT 0.003,
  tasa_bi             NUMERIC(7,6)  NOT NULL DEFAULT 0.0025,
  tasa_dinero         NUMERIC(7,6)  NOT NULL DEFAULT 0.004,
  tasa_vidrios        NUMERIC(7,6)  NOT NULL DEFAULT 0.0018,
  tasa_anuncios       NUMERIC(7,6)  NOT NULL DEFAULT 0.005,
  vigente_desde       DATE          NOT NULL DEFAULT CURRENT_DATE
);

INSERT INTO cotizacion_parametros DEFAULT VALUES;

CREATE TABLE IF NOT EXISTS cotizacion_tarifas_incendio (
  id                SERIAL PRIMARY KEY,
  clave_incendio    VARCHAR(10)   NOT NULL,
  tipo_constructivo VARCHAR(50)   NOT NULL,
  tasa_edificios    NUMERIC(8,6)  NOT NULL,
  tasa_contenidos   NUMERIC(8,6)  NOT NULL
);

INSERT INTO cotizacion_tarifas_incendio (clave_incendio, tipo_constructivo, tasa_edificios, tasa_contenidos) VALUES
  ('A1', 'CONCRETO_ARMADO', 0.0012, 0.0018),
  ('A1', 'MAMPOSTERIA',     0.0015, 0.0022),
  ('A1', 'MADERA',          0.0035, 0.0048),
  ('B1', 'CONCRETO_ARMADO', 0.0020, 0.0030),
  ('B1', 'MAMPOSTERIA',     0.0025, 0.0036),
  ('B1', 'MADERA',          0.0055, 0.0075),
  ('B2', 'CONCRETO_ARMADO', 0.0028, 0.0042),
  ('B2', 'MAMPOSTERIA',     0.0034, 0.0050),
  ('B2', 'MADERA',          0.0070, 0.0095),
  ('C1', 'CONCRETO_ARMADO', 0.0038, 0.0056),
  ('C1', 'MAMPOSTERIA',     0.0045, 0.0065),
  ('C1', 'MADERA',          0.0090, 0.0120);

CREATE TABLE IF NOT EXISTS cotizacion_tarifas_cat_tev (
  id       SERIAL PRIMARY KEY,
  zona_tev VARCHAR(10)  NOT NULL UNIQUE,
  tasa     NUMERIC(8,6) NOT NULL
);

INSERT INTO cotizacion_tarifas_cat_tev (zona_tev, tasa) VALUES
  ('TEV-A', 0.0008),
  ('TEV-B', 0.0015),
  ('TEV-C', 0.0025),
  ('TEV-D', 0.0040);

CREATE TABLE IF NOT EXISTS cotizacion_tarifas_cat_fhm (
  id       SERIAL PRIMARY KEY,
  zona_fhm VARCHAR(10)  NOT NULL UNIQUE,
  tasa     NUMERIC(8,6) NOT NULL
);

INSERT INTO cotizacion_tarifas_cat_fhm (zona_fhm, tasa) VALUES
  ('FHM-1', 0.0005),
  ('FHM-2', 0.0010),
  ('FHM-3', 0.0018);

CREATE TABLE IF NOT EXISTS cotizacion_factores_equipo (
  id     SERIAL PRIMARY KEY,
  clase  VARCHAR(10)  NOT NULL,
  nivel  INTEGER      NOT NULL,
  factor NUMERIC(6,4) NOT NULL
);

INSERT INTO cotizacion_factores_equipo (clase, nivel, factor) VALUES
  ('A', 1, 0.0030), ('A', 2, 0.0025), ('A', 3, 0.0022),
  ('B', 1, 0.0045), ('B', 2, 0.0038), ('B', 3, 0.0032),
  ('C', 1, 0.0060), ('C', 2, 0.0050), ('C', 3, 0.0042);

CREATE TABLE IF NOT EXISTS cotizacion_cp_zonas (
  codigo_postal VARCHAR(10)  PRIMARY KEY,
  zona_tev      VARCHAR(10)  NOT NULL,
  zona_fhm      VARCHAR(10)  NOT NULL,
  municipio     VARCHAR(100),
  estado        VARCHAR(100)
);

INSERT INTO cotizacion_cp_zonas VALUES
  ('170103', 'TEV-B', 'FHM-2', 'Quito', 'Pichincha'),
  ('170104', 'TEV-A', 'FHM-1', 'Quito', 'Pichincha'),
  ('170105', 'TEV-C', 'FHM-2', 'Quito', 'Pichincha'),
  ('170401', 'TEV-B', 'FHM-3', 'Quito', 'Pichincha'),
  ('170109', 'TEV-D', 'FHM-2', 'Quito', 'Pichincha');
