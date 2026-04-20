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
      <label htmlFor={id} className="field-label" style={{ marginBottom: '8px', display: 'block', fontSize: '0.8125rem', fontWeight: 600, color: 'var(--cream)', letterSpacing: '0.01em' }}>
        {label}
        {required && <span aria-hidden="true" style={{ color: 'var(--accent)', marginLeft: 3 }}>*</span>}
      </label>
      {children}
      {error && (
        <p className="field-error" style={{ marginTop: '6px', fontSize: '0.75rem', color: 'var(--danger)', display: 'flex', alignItems: 'center', gap: '4px' }}>
          <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
            <circle cx="6" cy="6" r="5.5" stroke="currentColor"/>
            <path d="M6 3v3.5M6 8.5h.01" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
          </svg>
          {error}
        </p>
      )}
    </div>
  );
}

/* ─── Input ─── */
interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  mono?: boolean;
  error?: boolean;
  icon?: React.ReactNode;
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ mono, error, icon, className = '', ...props }, ref) => {
    const wrapperCls = ['input-wrapper', error ? 'is-error' : ''].filter(Boolean).join(' ');
    const inputCls = ['input-field', mono ? 'input-mono' : '', className].filter(Boolean).join(' ');
    
    if (!icon) {
      return <input ref={ref} className={[inputCls, 'border', 'border-border', 'bg-surface-2', error ? 'is-error' : ''].filter(Boolean).join(' ')} {...props} />;
    }

    return (
      <div className={wrapperCls}>
        <div className="input-icon">
          {icon}
        </div>
        <input ref={ref} className={inputCls} {...props} />
      </div>
    );
  },
);
Input.displayName = 'Input';

/* ─── Select ─── */
interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  error?: boolean;
  icon?: React.ReactNode;
}

export function Select({ error, icon, className = '', children, ...props }: Readonly<SelectProps>) {
  const wrapperCls = ['input-wrapper', error ? 'is-error' : ''].filter(Boolean).join(' ');
  const inputCls = ['input-field', className].filter(Boolean).join(' ');

  if (!icon) {
    return (
      <select className={[inputCls, 'border', 'border-border', 'bg-surface-2', error ? 'is-error' : ''].filter(Boolean).join(' ')} {...props}>
        {children}
      </select>
    );
  }

  return (
    <div className={wrapperCls}>
      <div className="input-icon">
        {icon}
      </div>
      <select className={inputCls} {...props}>
        {children}
      </select>
    </div>
  );
}

/* ─── Textarea ─── */
interface TextareaProps extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  error?: boolean;
  icon?: React.ReactNode;
}

export function Textarea({ error, icon, className = '', ...props }: Readonly<TextareaProps>) {
  const wrapperCls = ['input-wrapper', 'textarea-wrapper', error ? 'is-error' : ''].filter(Boolean).join(' ');
  const inputCls = ['input-field', className].filter(Boolean).join(' ');

  if (!icon) {
    return <textarea className={[inputCls, 'border', 'border-border', 'bg-surface-2', error ? 'is-error' : ''].filter(Boolean).join(' ')} {...props} />;
  }

  return (
    <div className={wrapperCls}>
      <div className="input-icon">
        {icon}
      </div>
      <textarea className={inputCls} {...props} style={{ paddingTop: '12px', ...props.style }} />
    </div>
  );
}
