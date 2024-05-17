import {
  ActionIcon,
  Burger,
  Code,
  Group,
  Image,
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
      <Burger
        opened={toggled}
        onClick={() => {
          toggle();
          setOpened(!toggled);
        }}
        hiddenFrom="sm"
        size="sm"
      />
      <Group justify="space-between" gap="xs">
        <Image src="/logo_small.png" h="35px" fit="contain" />
        <Title order={2}>DocsNinja</Title>
        <Code fw={700} visibleFrom="sm" mb="-5px">
          v0.0.0
        </Code>
      </Group>
      <Group justify="space-between" gap="xs">
        <Link className={classes.actionIcon} to="/">
          <Text size="sm" fw={500} fz="md" visibleFrom="sm" mr="xs">
            Username
          </Text>
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
