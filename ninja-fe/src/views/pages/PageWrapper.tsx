import { Container } from "@mantine/core";
import React, { useEffect } from "react";
import { useOutletContext } from "react-router-dom";

interface PageProps {
  title?: string;
  content?: React.ReactNode;
}

interface OutletContext {
  setTitle: (newTitle?: string) => NonNullable<unknown>;
}

export default function PageWrapper(props: PageProps) {
  const { setTitle } = useOutletContext<OutletContext>();
  useEffect(() => {
    setTitle(props.title);
  }, [props.title]);

  return (
    <Container fluid m={0} p={0} pt="sm" h="calc(100dvh - 92px)">
      {props.content}
    </Container>
  );
}
