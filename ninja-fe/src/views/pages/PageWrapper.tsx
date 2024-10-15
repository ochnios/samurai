import { Container } from "@mantine/core";
import React, { useEffect } from "react";
import { useNavigate, useOutletContext } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth.ts";
import { useIsAdmin } from "../../hooks/useIsAdmin.ts";
import { useIsMod } from "../../hooks/useIsMod.ts";
import { Role } from "../../model/api/user/Role.ts";
import { showErrorMessage } from "../../utils.ts";

interface PageProps {
  title?: string;
  content?: React.ReactNode;
  access?: Role;
}

interface OutletContext {
  setTitle: (newTitle?: string) => NonNullable<unknown>;
}

export default function PageWrapper(props: PageProps) {
  const { setTitle } = useOutletContext<OutletContext>();
  const navigate = useNavigate();
  const auth = useAuth();
  const isAdmin = useIsAdmin();
  const isMod = useIsMod();

  useEffect(() => {
    setTitle(props.title);
  }, [props.title]);

  useEffect(() => {
    if (!auth.authenticated) {
      navigate("/login");
    } else if (
      (!isMod && props.access === Role.Mod) ||
      (!isAdmin && props.access === Role.Admin)
    ) {
      showErrorMessage("You don't have access to this page!");
    }
  }, [navigate, props.access, auth]);

  return (
    <Container fluid m={0} p={0} pt="sm">
      {props.content}
    </Container>
  );
}
