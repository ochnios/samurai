import { useState } from "react";
import { AppShell } from "@mantine/core";
import { Navigate, Outlet, useOutlet } from "react-router-dom";
import Navigation from "./Navigation.tsx";
import Header from "./Header.tsx";

export function Layout() {
  const [opened, setOpened] = useState(false);
  const outlet = useOutlet();
  return (
    <AppShell
      header={{ height: "60px" }}
      navbar={{ width: 300, breakpoint: "sm", collapsed: { mobile: !opened } }}
      padding="md"
    >
      <AppShell.Header>
        <Header opened={opened} setOpened={setOpened} />
      </AppShell.Header>
      <AppShell.Navbar p="md">
        <Navigation />
      </AppShell.Navbar>
      <AppShell.Main>
        {outlet == null && <Navigate to="/" />} <Outlet />
      </AppShell.Main>
    </AppShell>
  );
}
