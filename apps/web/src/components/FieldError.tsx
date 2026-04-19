export function FieldError({ msg }: Readonly<{ msg?: string }>) {
  if (!msg) return null;
  return <p className="field-error">{msg}</p>;
}
