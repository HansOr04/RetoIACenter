import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Button } from '../Button';

describe('Button', () => {
  it('renderiza el texto del children', () => {
    render(<Button>Guardar</Button>);
    expect(screen.getByRole('button', { name: 'Guardar' })).toBeInTheDocument();
  });

  it('dispara onClick al hacer click', async () => {
    const onClick = jest.fn();
    render(<Button onClick={onClick}>Click me</Button>);
    await userEvent.click(screen.getByRole('button'));
    expect(onClick).toHaveBeenCalledTimes(1);
  });

  it('aplica disabled cuando se le pasa la prop', () => {
    render(<Button disabled>Guardar</Button>);
    expect(screen.getByRole('button')).toBeDisabled();
  });

  it('no dispara onClick cuando está disabled', async () => {
    const onClick = jest.fn();
    render(<Button disabled onClick={onClick}>Test</Button>);
    await userEvent.click(screen.getByRole('button'));
    expect(onClick).not.toHaveBeenCalled();
  });
});
