import { render, screen } from '@testing-library/react';
import { StepIndicator } from '../StepIndicator';

describe('StepIndicator', () => {
  const stepsLabels = ['Folio', 'Datos', 'Layout', 'Ubicaciones', 'Coberturas', 'Cálculo'];

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
    
    // In the DOM, the circle '.is-active' is inside a div, alongside another div that contains the label
    const parentContainer = activeIndicators[0].parentElement?.parentElement;
    expect(parentContainer).toHaveTextContent('Layout');
  });
});
