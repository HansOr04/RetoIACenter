import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Input } from '../Input';

describe('Input', () => {
  it('renderiza con placeholder', () => {
    render(<Input placeholder="Razón social" />);
    expect(screen.getByPlaceholderText('Razón social')).toBeInTheDocument();
  });

  it('llama onChange cuando el usuario escribe', async () => {
    const onChange = jest.fn();
    render(<Input onChange={onChange} />);
    await userEvent.type(screen.getByRole('textbox'), 'Hola');
    expect(onChange).toHaveBeenCalled();
  });

  it('muestra valor controlado', () => {
    render(<Input value="Bodega Norte" readOnly />);
    expect(screen.getByRole('textbox')).toHaveValue('Bodega Norte');
  });
});
