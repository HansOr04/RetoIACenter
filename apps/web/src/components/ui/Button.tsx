import React from 'react';

type Variant = 'primary' | 'ghost' | 'outline';
type Size = 'sm' | 'md' | 'lg';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant;
  size?: Size;
  full?: boolean;
  loading?: boolean;
  children: React.ReactNode;
}

const variantClass: Record<Variant, string> = {
  primary: 'btn-primary',
  ghost: 'btn-ghost',
  outline: 'btn-outline',
};

const sizeClass: Record<Size, string> = {
  sm: 'btn-sm',
  md: '',
  lg: 'btn-lg',
};

export function Button({
  variant = 'primary',
  size = 'md',
  full = false,
  loading = false,
  disabled,
  className = '',
  children,
  ...props
}: Readonly<ButtonProps>) {
  const classes = [
    'btn',
    variantClass[variant],
    sizeClass[size],
    full ? 'btn-full' : '',
    className,
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <button className={classes} disabled={disabled || loading} {...props}>
      {loading ? (
        <span style={{ fontFamily: 'var(--font-mono)', opacity: 0.8 }}>
          {children}
        </span>
      ) : (
        children
      )}
    </button>
  );
}
