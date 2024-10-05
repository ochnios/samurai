import { Container } from "@mantine/core";
import React, { useEffect } from "react";
import { useNavigate, useOutletContext } from "react-router-dom";

import { useAppSelector } from "../../hooks/useAppSelector.ts";
import { RootState } from "../../store.ts";

interface PageProps {
  title?: string;
  content?: React.ReactNode;
}

interface OutletContext {
  setTitle: (newTitle?: string) => NonNullable<unknown>;
}

export default function PageWrapper(props: PageProps) {
  const navigate = useNavigate();
  const auth = useAppSelector((state: RootState) => state.auth);
  const { setTitle } = useOutletContext<OutletContext>();

  useEffect(() => {
    if (!auth.authenticated) navigate("/login");
  }, [auth.authenticated]);

  useEffect(() => {
    setTitle(props.title);
  }, [props.title]);

  return (
    <Container fluid m={0} p={0} pt="sm" h="calc(100dvh - 92px)">
      {props.content}
    </Container>
  );
}
