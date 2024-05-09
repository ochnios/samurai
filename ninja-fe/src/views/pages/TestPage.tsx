interface TestPageProps {
  title?: string;
}

export default function TestPage({ title }: TestPageProps) {
  return (
    <div>
      <h1>{title ?? "TestPage"}</h1>
      <p>Some content</p>
    </div>
  );
}
