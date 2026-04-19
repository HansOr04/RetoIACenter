-- V3: Campos requeridos por HU-001 (idempotencia), HU-002 (datos generales), HU-003 (layout)
-- Nota: agent_id y version ya existen en V1 — no se re-crean

ALTER TABLE folios
  ADD COLUMN idempotency_key     VARCHAR(100) UNIQUE,
  ADD COLUMN tipo_negocio        VARCHAR(50),
  ADD COLUMN layout_ubicaciones  JSONB,
  ADD COLUMN datos_generales     JSONB;

CREATE INDEX idx_folios_idempotency ON folios(idempotency_key);
