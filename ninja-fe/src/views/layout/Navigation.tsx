import {
  IconChartBar,
  IconFile,
  IconLogout,
  IconMessageChatbot,
  IconRobot,
  IconUserCircle,
  IconUsers,
} from "@tabler/icons-react";
import classes from "./Navigation.module.css";
import { Link, useLocation } from "react-router-dom";

const data = [
  { link: "/assistants", label: "Assistants", icon: IconMessageChatbot },
  { link: "/documents", label: "Documents", icon: IconFile },
  { link: "/statistics", label: "Statistics", icon: IconChartBar },
  { link: "/models", label: "Models", icon: IconRobot },
  { link: "/users", label: "Users", icon: IconUsers },
];

export default function Navigation() {
  const location = useLocation();

  const links = data.map((item) => (
    <Link
      className={classes.link}
      data-active={location.pathname === item.link || undefined}
      to={item.link}
      key={item.label}
    >
      <item.icon className={classes.linkIcon} stroke={1.5} />
      <span>{item.label}</span>
    </Link>
  ));

  return (
    <nav className={classes.navbar}>
      <div className={classes.links}>{links}</div>
      <div className={classes.footer}>
        <Link
          to="/account"
          className={classes.link}
          data-active={location.pathname === "/account" || undefined}
        >
          <IconUserCircle className={classes.linkIcon} stroke={1.5} />
          <span>Account</span>
        </Link>
        <Link
          to="#"
          className={classes.link}
          onClick={(event) => event.preventDefault()}
        >
          <IconLogout className={classes.linkIcon} stroke={1.5} />
          <span>Logout</span>
        </Link>
      </div>
    </nav>
  );
}
