import {
  ActionIcon,
  Burger,
  Code,
  Group,
  Text,
  Title,
  useMantineColorScheme,
} from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import { IconMoon, IconSun, IconUserCircle } from "@tabler/icons-react";
import classes from "./Header.module.css";
import { Link } from "react-router-dom";

interface HeaderProps {
  opened: boolean;
  setOpened: (state: boolean) => void;
}

export default function Header({ opened, setOpened }: HeaderProps) {
  const [toggled, { toggle }] = useDisclosure(opened);
  const { colorScheme, setColorScheme } = useMantineColorScheme({
    keepTransitions: true,
  });
  return (
    <Group h="100%" px="sm" py="sm" justify="space-between">
      <Group justify="space-between">
        <Burger
          opened={toggled}
          onClick={() => {
            toggle();
            setOpened(!toggled);
          }}
          hiddenFrom="sm"
          size="sm"
        />
        <Title order={2}>DocsNinja</Title>
        <Code fw={700} visibleFrom="sm">
          v0.0.0
        </Code>
      </Group>
      <Group justify="space-between" gap="xs">
        <Text size="sm" fw={500} fz="md" visibleFrom="sm">
          Username
        </Text>
        <Link className={classes.actionIcon} to="/">
          <IconUserCircle className={classes.icon} stroke={1.5} size={26} />
        </Link>
        <ActionIcon
          className={classes.actionIcon}
          onClick={() =>
            setColorScheme(colorScheme === "light" ? "dark" : "light")
          }
          variant="transparent"
          aria-label="Toggle color scheme"
        >
          {colorScheme === "dark" && <IconSun stroke={1.5} size={26} />}
          {colorScheme === "light" && <IconMoon stroke={1.5} size={26} />}
        </ActionIcon>
      </Group>
    </Group>
  );
}
