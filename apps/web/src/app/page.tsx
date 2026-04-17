import Link from 'next/link'

export default function HomePage() {
  return (
    <main className="min-h-screen flex flex-col items-center justify-center gap-8 p-8">
      <div className="text-center">
        <h1 className="text-4xl font-bold text-slate-900 mb-2">
          Cotizador de Seguros de Daños
        </h1>
        <p className="text-slate-500 text-lg">
          Sofka Technologies · Reto IA Center 2026
        </p>
      </div>

      <div className="flex gap-4">
        {/* TODO: HU-F01 — implementar lógica de creación de folio */}
        <button
          className="px-6 py-3 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 transition-colors"
          disabled
        >
          Crear cotización
        </button>

        {/* TODO: HU-F01 — implementar búsqueda y apertura de folio existente */}
        <button
          className="px-6 py-3 border border-slate-300 text-slate-700 rounded-lg font-medium hover:bg-slate-50 transition-colors"
          disabled
        >
          Abrir folio
        </button>
      </div>

      <p className="text-xs text-slate-400">
        En construcción · Scaffold generado por ASDD
      </p>
    </main>
  )
}
