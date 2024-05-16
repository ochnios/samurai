import { AppShell, Burger, Code, Group, Title } from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import { Navigate, Outlet, useOutlet } from "react-router-dom";
import Navigation from "./Navigation.tsx";

export function Layout() {
  const [opened, { toggle }] = useDisclosure();
  const outlet = useOutlet();

  return (
    <AppShell
      header={{ height: 60 }}
      navbar={{ width: 300, breakpoint: "sm", collapsed: { mobile: !opened } }}
      padding="md"
    >
      <AppShell.Header>
        <Group h="100%" p="md">
          <Burger opened={opened} onClick={toggle} hiddenFrom="sm" size="sm" />
          <Group justify="space-between">
            <Title order={2}>DocsNinja</Title>
            <Code fw={700}>v0.0.0</Code>
          </Group>
        </Group>
      </AppShell.Header>
      <AppShell.Navbar p="md">
        <Navigation />
      </AppShell.Navbar>
      <AppShell.Main>
        {outlet == null && <Navigate to="/" />}
        <Outlet />
      </AppShell.Main>
    </AppShell>
  );
}
