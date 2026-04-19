import { type ClassValue, clsx } from 'clsx';
export function cn(...inputs: ClassValue[]) { return clsx(inputs); }

export function formatCurrency(n: number) {
  return new Intl.NumberFormat('es-EC', {
    style: 'currency', currency: 'USD', minimumFractionDigits: 2,
  }).format(n);
}

export function formatDate(iso: string) {
  return new Date(iso).toLocaleString('es-EC', {
    dateStyle: 'medium', timeStyle: 'short',
  });
}

export function generateIdempotencyKey() {
  return `${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;
}
