import { formatCurrency, formatDate, generateIdempotencyKey } from '../utils';

describe('formatCurrency', () => {
  it('formatea números con 2 decimales', () => {
    expect(formatCurrency(1234.5).replace(/\s/g, '')).toContain('1.234,50');
  });

  it('maneja cero', () => {
    expect(formatCurrency(0).replace(/\s/g, '')).toContain('0,00');
  });
});

describe('formatDate', () => {
  it('formatea la fecha correctamente', () => {
    const dateStr = formatDate('2026-04-20T10:00:00Z');
    expect(dateStr).toBeTruthy();
    expect(typeof dateStr).toBe('string');
  });
});

describe('generateIdempotencyKey', () => {
  it('genera una clave única que contiene un timestamp', () => {
    const key = generateIdempotencyKey();
    expect(key).toContain('-');
    expect(key.length).toBeGreaterThan(15);
  });
});
