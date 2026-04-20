import { render, screen, fireEvent } from '@testing-library/react';
import { StepIndicator } from '../StepIndicator';

// Mock next/navigation so hooks don't throw in JSDOM
const mockPush = jest.fn();
jest.mock('next/navigation', () => ({
  useRouter: () => ({ push: mockPush }),
  useParams: () => ({ folio: 'TEST-0001' }),
}));

describe('StepIndicator', () => {
  const stepsLabels = ['Folio', 'Datos', 'Layout', 'Ubicaciones', 'Coberturas', 'Cálculo'];

  beforeEach(() => mockPush.mockClear());

  it('renderiza todos los pasos', () => {
    render(<StepIndicator current={1} />);
    stepsLabels.forEach(s => {
      expect(screen.getByText(s)).toBeInTheDocument();
    });
  });

  it('marca el paso actual como activo', () => {
    const { container } = render(<StepIndicator current={3} />);
    const activeIndicators = container.querySelectorAll('.is-active');
    expect(activeIndicators.length).toBe(1);

    const parentContainer = activeIndicators[0].closest('[class*="flex flex-col"]');
    expect(parentContainer).toHaveTextContent('Layout');
  });

  it('los pasos completos tienen checkmark svg', () => {
    const { container } = render(<StepIndicator current={4} />);
    const doneCircles = container.querySelectorAll('.is-done');
    expect(doneCircles.length).toBe(3); // pasos 1, 2, 3 deben estar done
  });

  it('navega al paso completado al hacer clic', () => {
    render(<StepIndicator current={5} />);
    // El paso 1 ("Folio") está completo — su botón debe ser navegable
    const folioBtn = screen.getByRole('button', { name: /Paso 1: Folio/i });
    expect(folioBtn).not.toBeDisabled();
    fireEvent.click(folioBtn);
    expect(mockPush).toHaveBeenCalledWith('/cotizador/TEST-0001');
  });

  it('no navega en pasos pendientes o actuales', () => {
    render(<StepIndicator current={3} />);
    // Paso 6 ("Cálculo") es pending
    const calculoBtn = screen.getByRole('button', { name: /Paso 6: Cálculo/i });
    expect(calculoBtn).toBeDisabled();
    fireEvent.click(calculoBtn);
    expect(mockPush).not.toHaveBeenCalled();
  });

  it('muestra barra de progreso con aria-valuenow correcto', () => {
    render(<StepIndicator current={3} />);
    const bar = screen.getByRole('progressbar');
    // step 3 of 6: ((3-1)/(6-1))*100 = 40%
    expect(bar).toHaveAttribute('aria-valuenow', '40');
  });
});

