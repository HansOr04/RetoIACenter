-- V1: Schema inicial del cotizador de daños

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ─── Estados del folio ──────────────────────────────────────────────────────
CREATE TYPE folio_status AS ENUM ('BORRADOR', 'CALCULADO', 'EMITIDO', 'CANCELADO');

-- ─── Folios de cotización ────────────────────────────────────────────────────
CREATE TABLE folios (
    id                       UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    numero_folio             VARCHAR(20) NOT NULL UNIQUE,
    status                   folio_status NOT NULL DEFAULT 'BORRADOR',

    -- Datos generales
    rfc_contratante          VARCHAR(13),
    nombre_contratante       VARCHAR(255),
    agent_id                 VARCHAR(50),
    business_line_id         VARCHAR(50),

    -- Datos estructurales como JSONB para flexibilidad
    ubicaciones              JSONB NOT NULL DEFAULT '[]'::jsonb,
    primas_por_ubicacion     JSONB NOT NULL DEFAULT '[]'::jsonb,
    prima_total              NUMERIC(15, 2),

    -- Control de concurrencia optimista
    version                  INT NOT NULL DEFAULT 0,

    -- Auditoría
    created_at               TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at               TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

-- ─── Parámetros de cálculo generales ────────────────────────────────────────
CREATE TABLE parametros_calculo (
    id                       SERIAL PRIMARY KEY,
    clave                    VARCHAR(100) NOT NULL UNIQUE,
    valor                    NUMERIC(15, 6) NOT NULL,
    descripcion              VARCHAR(255),
    vigente_desde            DATE NOT NULL DEFAULT CURRENT_DATE
);

-- ─── Tarifas de incendio por zona y tipo de construcción ────────────────────
CREATE TABLE tarifas_incendio (
    id                       SERIAL PRIMARY KEY,
    zona_riesgo              VARCHAR(10) NOT NULL,
    tipo_construccion        VARCHAR(50) NOT NULL,
    tasa_base                NUMERIC(10, 6) NOT NULL,
    vigente_desde            DATE NOT NULL DEFAULT CURRENT_DATE,
    UNIQUE (zona_riesgo, tipo_construccion, vigente_desde)
);

-- ─── Tarifas de catástrofe (sismo, inundación) ──────────────────────────────
CREATE TABLE tarifas_cat (
    id                       SERIAL PRIMARY KEY,
    zona_riesgo              VARCHAR(10) NOT NULL,
    peril                    VARCHAR(50) NOT NULL,
    tasa_base                NUMERIC(10, 6) NOT NULL,
    vigente_desde            DATE NOT NULL DEFAULT CURRENT_DATE,
    UNIQUE (zona_riesgo, peril, vigente_desde)
);

-- ─── Tarifa FHM (fenómenos hidrometeorológicos) ──────────────────────────────
CREATE TABLE tarifa_fhm (
    id                       SERIAL PRIMARY KEY,
    zona_riesgo              VARCHAR(10) NOT NULL,
    tasa_base                NUMERIC(10, 6) NOT NULL,
    vigente_desde            DATE NOT NULL DEFAULT CURRENT_DATE,
    UNIQUE (zona_riesgo, vigente_desde)
);

-- ─── Factores de equipo electrónico ─────────────────────────────────────────
CREATE TABLE factores_equipo_electronico (
    id                       SERIAL PRIMARY KEY,
    rango_valor_desde        NUMERIC(15, 2) NOT NULL,
    rango_valor_hasta        NUMERIC(15, 2),
    factor                   NUMERIC(10, 6) NOT NULL
);

-- ─── Catálogo de códigos postales y zonas de riesgo ─────────────────────────
CREATE TABLE catalogo_cp_zonas (
    id                       SERIAL PRIMARY KEY,
    codigo_postal            VARCHAR(10) NOT NULL,
    municipio                VARCHAR(100),
    estado                   VARCHAR(100),
    pais                     VARCHAR(3) NOT NULL DEFAULT 'ECU',
    zona_riesgo_incendio     VARCHAR(10),
    zona_riesgo_sismo        VARCHAR(10),
    UNIQUE (codigo_postal, pais)
);

-- ─── Dimensión zona TEV (Terrorismo, Explosión, Vandalismo) ─────────────────
CREATE TABLE dim_zona_tev (
    id                       SERIAL PRIMARY KEY,
    zona_codigo              VARCHAR(15) NOT NULL UNIQUE,
    descripcion              VARCHAR(100),
    factor_tev               NUMERIC(10, 6) NOT NULL
);

-- ─── Dimensión zona FHM ─────────────────────────────────────────────────────
CREATE TABLE dim_zona_fhm (
    id                       SERIAL PRIMARY KEY,
    zona_codigo              VARCHAR(15) NOT NULL UNIQUE,
    descripcion              VARCHAR(100),
    factor_fhm               NUMERIC(10, 6) NOT NULL
);

-- ─── Índices ─────────────────────────────────────────────────────────────────
CREATE INDEX idx_folios_status ON folios(status);
CREATE INDEX idx_folios_agent ON folios(agent_id);
CREATE INDEX idx_folios_rfc ON folios(rfc_contratante);
CREATE INDEX idx_folios_numero ON folios(numero_folio);

-- ─── Función de actualización de timestamp ──────────────────────────────────
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_folios_updated_at
    BEFORE UPDATE ON folios
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
