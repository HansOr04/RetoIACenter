-- V4: Tabla principal de cotizaciones — HU-004, HU-005, HU-009, HU-010
CREATE TABLE IF NOT EXISTS cotizaciones (
  numero_folio              VARCHAR(50) PRIMARY KEY REFERENCES folios(numero_folio),
  version                   INTEGER NOT NULL DEFAULT 1,
  fecha_ultima_actualizacion TIMESTAMP NOT NULL DEFAULT NOW(),
  prima_neta                NUMERIC(15,2),
  prima_comercial           NUMERIC(15,2),
  datos                     JSONB NOT NULL DEFAULT '{}'::jsonb
);

CREATE INDEX idx_cotizaciones_ubicaciones
  ON cotizaciones USING GIN ((datos->'ubicaciones'));
