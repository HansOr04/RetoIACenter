import { render, screen } from '@testing-library/react';
import { StatusBadge } from '../StatusBadge';

describe('StatusBadge', () => {
  it('muestra el texto del estado', () => {
    render(<StatusBadge estado="BORRADOR" />);
    expect(screen.getByText(/Borrador/i)).toBeInTheDocument();
  });

  it('aplica color según estado calculada', () => {
    const { container } = render(<StatusBadge estado="CALCULADO" />);
    expect(container.firstChild).toHaveClass(/badge-accent/i);
  });

  it('aplica color según estado borrador', () => {
    const { container } = render(<StatusBadge estado="BORRADOR" />);
    expect(container.firstChild).toHaveClass(/badge-warning/i);
  });
});
