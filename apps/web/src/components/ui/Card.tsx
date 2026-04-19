import React from 'react';

interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  variant?: 'default' | 'accent' | 'warning' | 'danger';
  children: React.ReactNode;
}

const variantClass = {
  default: 'card',
  accent: 'card-accent',
  warning: 'card-warning',
  danger: 'card-danger',
};

export function Card({
  variant = 'default',
  className = '',
  children,
  ...props
}: Readonly<CardProps>) {
  return (
    <div className={`${variantClass[variant]} ${className}`} {...props}>
      {children}
    </div>
  );
}
