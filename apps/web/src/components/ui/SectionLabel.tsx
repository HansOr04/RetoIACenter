import React from 'react';

interface SectionLabelProps extends React.HTMLAttributes<HTMLParagraphElement> {
  children: React.ReactNode;
  className?: string;
}

export function SectionLabel({ children, className = '', ...props }: Readonly<SectionLabelProps>) {
  return (
    <p className={`section-label ${className}`} {...props}>
      {children}
    </p>
  );
}
