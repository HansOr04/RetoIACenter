export function FieldError({ msg }: Readonly<{ msg?: string }>) {
  if (!msg) return null;
  return <p className="text-xs text-danger mt-1">{msg}</p>;
}
