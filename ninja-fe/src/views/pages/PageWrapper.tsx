import { Container, Divider, Title } from "@mantine/core";
import { useOutletContext } from "react-router-dom";
import React from "react";

interface PageProps {
  title?: string;
  content?: React.ReactNode;
}

interface OutletContext {
  setTitle: (newTitle?: string) => NonNullable<unknown>;
}

export default function PageWrapper(props: PageProps) {
  const { setTitle } = useOutletContext<OutletContext>();
  setTitle(props.title);

  return (
    <Container m={0} p={0}>
      <Title order={2} hiddenFrom="sm">
        {props.title}
      </Title>
      <Divider hiddenFrom="sm" my="sm" />
      {props.content}
    </Container>
  );
}
