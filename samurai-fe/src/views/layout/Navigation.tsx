import { Box, Divider, ScrollArea } from "@mantine/core";
import { useElementSize } from "@mantine/hooks";
import {
  IconFiles,
  IconLogout,
  IconMessagePlus,
  IconMessages,
  IconUserCircle,
  IconUsers,
} from "@tabler/icons-react";

import { useAppDispatch } from "../../hooks/useAppDispatch.ts";
import { useIsMod } from "../../hooks/useIsMod.ts";
import { logout } from "../../reducers/authSlice.ts";
import Conversations from "../components/conversations/Conversations.tsx";
import NavLink from "../components/navigation/NavLink.tsx";
import classes from "./Navigation.module.css";
import { useIsAdmin } from "../../hooks/useIsAdmin.ts";

const adminLinks = [{ link: "/users", label: "Users", icon: IconUsers }];

const modLinks = [
  { link: "/documents", label: "Documents", icon: IconFiles },
  { link: "/conversations/all", label: "Conversations", icon: IconMessages },
];

const links = [
  { link: "/conversations/new", label: "New chat", icon: IconMessagePlus },
];

export default function Navigation() {
  const { ref, height } = useElementSize();
  const dispatch = useAppDispatch();
  const isAdmin = useIsAdmin();
  const isMod = useIsMod();

  const handleLogout = (e: any) => {
    e.preventDefault();
    dispatch(logout());
  };

  return (
    <Box className={classes.navbar}>
      <Box className={classes.links}>
        {isAdmin &&
          adminLinks.map((item) => (
            <NavLink link={item.link} icon={item.icon} key={item.link}>
              {item.label}
            </NavLink>
          ))}
        {isMod &&
          modLinks.map((item) => (
            <NavLink link={item.link} icon={item.icon} key={item.link}>
              {item.label}
            </NavLink>
          ))}
        {links.map((item) => (
          <NavLink link={item.link} icon={item.icon} key={item.link}>
            {item.label}
          </NavLink>
        ))}
      </Box>
      <Divider
        my="md"
        labelPosition="center"
        label={
          <>
            <IconMessages size={16} />
            <Box fz="xs" ml={5}>
              Your conversations
            </Box>
          </>
        }
      ></Divider>
      <Box flex={1} ref={ref}>
        <ScrollArea h={height}>
          <Conversations />
        </ScrollArea>
      </Box>
      <Divider my="md"></Divider>
      <Box>
        <NavLink link="/account" icon={IconUserCircle}>
          Account
        </NavLink>
        <NavLink link="/logout" icon={IconLogout} onClick={handleLogout}>
          Logout
        </NavLink>
      </Box>
    </Box>
  );
}
