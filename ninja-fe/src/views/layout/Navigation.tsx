import { useState } from "react";
import { Code, Group, Title } from "@mantine/core";
import {
  IconChartBar,
  IconFile,
  IconLogout,
  IconMessageChatbot,
  IconRobot,
  IconSettings,
  IconUsers,
} from "@tabler/icons-react";
import classes from "./Navigation.module.css";
import { Link } from "react-router-dom";

const data = [
  { link: "/assistants", label: "Assistants", icon: IconMessageChatbot },
  { link: "/documents", label: "Documents", icon: IconFile },
  { link: "/statistics", label: "Statistics", icon: IconChartBar },
  { link: "/models", label: "Models", icon: IconRobot },
  { link: "/users", label: "Users", icon: IconUsers },
];

export default function Navigation() {
  const [active, setActive] = useState("Assistants");

  const links = data.map((item) => (
    <Link
      className={classes.link}
      data-active={item.label === active || undefined}
      to={item.link}
      key={item.label}
      onClick={() => {
        setActive(item.label);
      }}
    >
      <item.icon className={classes.linkIcon} stroke={1.5} />
      <span>{item.label}</span>
    </Link>
  ));

  return (
    <nav className={classes.navbar}>
      <div className={classes.navbarMain}>
        <Group className={classes.header} justify="space-between">
          <Title order={3}>DocsNinja</Title>
          <Code fw={700}>v0.0.0</Code>
        </Group>
        {links}
      </div>

      <div className={classes.footer}>
        <Link to="/account" className={classes.link}>
          <IconSettings className={classes.linkIcon} stroke={1.5} />
          <span>My account</span>
        </Link>
        <a
          href="#"
          className={classes.link}
          onClick={(event) => event.preventDefault()}
        >
          <IconLogout className={classes.linkIcon} stroke={1.5} />
          <span>Logout</span>
        </a>
      </div>
    </nav>
  );
}
