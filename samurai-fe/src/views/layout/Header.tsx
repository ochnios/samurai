import {
  ActionIcon,
  Badge,
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
import { useEffect } from "react";
import { Link, useLocation } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth.ts";
import { Role } from "../../model/api/user/Role.ts";
import classes from "./Header.module.css";

interface HeaderProps {
  opened: boolean;
  setOpened: (state: boolean) => void;
  title: string;
}

export default function Header(props: HeaderProps) {
  const location = useLocation();
  const [toggled, { toggle }] = useDisclosure(props.opened);
  const { colorScheme, setColorScheme } = useMantineColorScheme({
    keepTransitions: true,
  });
  const auth = useAuth();

  useEffect(() => {
    if (props.opened) {
      toggle();
      props.setOpened(!toggled);
    }
  }, [location, auth]);

  return (
    <Group h="100%" px="sm" py="sm" justify="space-between">
      <Burger
        opened={toggled}
        onClick={() => {
          toggle();
          props.setOpened(!toggled);
        }}
        hiddenFrom="sm"
        size="sm"
      />
      <Group justify="space-between" gap="xs">
        <Link to="/" style={{ textDecoration: "none", color: "inherit" }}>
          <Group justify="space-between" gap="xs">
            <Image src="/logo_medium.png" h="35px" fit="contain" />
            <Title order={3}>SamurAI</Title>
          </Group>
        </Link>
        <Code fw={700} visibleFrom="sm" mb="-5px">
          v0.0.1
        </Code>
        <Title order={2} ml={85} visibleFrom="sm">
          {props.title}
        </Title>
      </Group>
      <Group justify="space-between" gap="xs">
        {auth.user?.role == Role.Admin && <Badge size="xs">ADMIN</Badge>}
        {auth.user?.role == Role.Mod && <Badge size="xs">MODERATOR</Badge>}
        <Link className={classes.actionIcon} to="/account">
          <Text size="sm" fw={500} fz="md" visibleFrom="sm" mr="xs">
            {`${auth.user?.firstname} ${auth.user?.lastname}`}
          </Text>
          <IconUserCircle className={classes.icon} stroke={1.5} size={26} />
        </Link>
        <ActionIcon
          className={classes.actionIcon}
          onClick={() =>
            setColorScheme(colorScheme === "light" ? "dark" : "light")
          }
          variant="transparent"
          aria-label="Przełącz motyw"
        >
          {colorScheme === "dark" && <IconSun stroke={1.5} size={26} />}
          {colorScheme === "light" && <IconMoon stroke={1.5} size={26} />}
        </ActionIcon>
      </Group>
    </Group>
  );
}
