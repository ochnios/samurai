import { useState } from "react";
import { AppShell } from "@mantine/core";
import { Navigate, Outlet, useOutlet } from "react-router-dom";
import Navigation from "./Navigation.tsx";
import Header from "./Header.tsx";
import { useDocumentTitle } from "@mantine/hooks";

export function Layout() {
  const outlet = useOutlet();
  const [opened, setOpened] = useState(false);
  const [title, setTitle] = useState("");
  useDocumentTitle(title + " | DocsNinja");

  return (
    <AppShell
      header={{ height: "60px" }}
      navbar={{ width: 300, breakpoint: "sm", collapsed: { mobile: !opened } }}
      padding="md"
    >
      <AppShell.Header>
        <Header title={title} opened={opened} setOpened={setOpened} />
      </AppShell.Header>
      <AppShell.Navbar p="md">
        <Navigation />
      </AppShell.Navbar>
      <AppShell.Main>
        {outlet == null && <Navigate to="/" />}{" "}
        <Outlet context={{ setTitle }} />
      </AppShell.Main>
    </AppShell>
  );
}
