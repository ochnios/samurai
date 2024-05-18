import { Text, Title } from "@mantine/core";

interface TestPageProps {
  title?: string;
}

export default function TestPage({ title }: TestPageProps) {
  return (
    <div>
      <Title>{title ?? "TestPage"}</Title>
      <Text fz="lg">Some content</Text>
    </div>
  );
}
