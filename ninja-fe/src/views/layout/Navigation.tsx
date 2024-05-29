import {
  IconChartBar,
  IconFile,
  IconLogout,
  IconMessageChatbot,
  IconMessagePlus,
  IconMessages,
  IconRobot,
  IconUserCircle,
  IconUsers,
} from "@tabler/icons-react";
import classes from "./Navigation.module.css";
import { Box, Divider, ScrollArea } from "@mantine/core";
import NavLink from "../components/NavLink.tsx";
import { useElementSize } from "@mantine/hooks";
import Conversations from "./Conversations.tsx";

const data = [
  { link: "/assistants", label: "Assistants", icon: IconMessageChatbot },
  { link: "/documents", label: "Documents", icon: IconFile },
  { link: "/statistics", label: "Statistics", icon: IconChartBar },
  { link: "/models", label: "Models", icon: IconRobot },
  { link: "/users", label: "Users", icon: IconUsers },
  { link: "/conversations/new", label: "New chat", icon: IconMessagePlus },
];

export default function Navigation() {
  const { ref, height } = useElementSize();
  const links = data.map((item) => (
    <NavLink link={item.link} icon={item.icon} key={item.link}>
      {item.label}
    </NavLink>
  ));

  return (
    <Box className={classes.navbar}>
      <Box className={classes.links}>{links}</Box>
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
        <NavLink link="/logout" icon={IconLogout}>
          Logout
        </NavLink>
      </Box>
    </Box>
  );
}
