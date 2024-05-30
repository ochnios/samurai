import { Box, Divider, ScrollArea } from "@mantine/core";
import { useElementSize } from "@mantine/hooks";
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
import Conversations from "../components/conversations/Conversations.tsx";
import NavLink from "../components/navigation/NavLink.tsx";
import SelectAssistant from "../components/navigation/SelectAssistant.tsx";
import classes from "./Navigation.module.css";

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

  return (
    <Box className={classes.navbar}>
      <Box>
        <SelectAssistant />
      </Box>
      <Divider my="md"></Divider>
      <Box className={classes.links}>
        {data.map((item) => (
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
        <NavLink link="/logout" icon={IconLogout}>
          Logout
        </NavLink>
      </Box>
    </Box>
  );
}
