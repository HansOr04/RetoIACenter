import React from 'react';

/* ─── Field wrapper (label + input + error) ─── */
interface FieldProps {
  id?: string;
  label: string;
  required?: boolean;
  error?: string;
  children: React.ReactNode;
}

export function Field({ id, label, required, error, children }: Readonly<FieldProps>) {
  return (
    <div>
      <label htmlFor={id} className="field-label">
        {label}
        {required && <span aria-hidden="true" style={{ color: 'var(--danger)', marginLeft: 2 }}>*</span>}
      </label>
      {children}
      {error && <p className="field-error">{error}</p>}
    </div>
  );
}

/* ─── Input ─── */
interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  mono?: boolean;
  error?: boolean;
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ mono, error, className = '', ...props }, ref) => {
    const cls = [
      'input-field',
      mono ? 'input-mono' : '',
      error ? 'is-error' : '',
      className,
    ]
      .filter(Boolean)
      .join(' ');
    return <input ref={ref} className={cls} {...props} />;
  },
);
Input.displayName = 'Input';

/* ─── Select ─── */
interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  error?: boolean;
}

export function Select({ error, className = '', children, ...props }: Readonly<SelectProps>) {
  const cls = ['input-field', error ? 'is-error' : '', className].filter(Boolean).join(' ');
  return (
    <select className={cls} {...props}>
      {children}
    </select>
  );
}

/* ─── Textarea ─── */
interface TextareaProps extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  error?: boolean;
}

export function Textarea({ error, className = '', ...props }: Readonly<TextareaProps>) {
  const cls = ['input-field', error ? 'is-error' : '', className].filter(Boolean).join(' ');
  return <textarea className={cls} {...props} />;
}
