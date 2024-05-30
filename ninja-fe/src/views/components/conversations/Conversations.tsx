import { Center, Loader, Stack } from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { useEffect } from "react";
import { useSelector } from "react-redux";
import { useAppDispatch } from "../../../hooks.ts";
import { fetchConversations } from "../../../reducers/conversationsSlice.ts";
import { RootState } from "../../../store.ts";
import Summary from "./Summary.tsx";

export default function Conversations() {
  const dispatch = useAppDispatch();
  const assistant = useSelector((state: RootState) => state.assistant);
  const conversations = useSelector((state: RootState) => state.conversations);

  useEffect(() => {
    assistant.currentId && dispatch(fetchConversations(assistant.currentId));
  }, [assistant.currentId]);

  useEffect(() => {
    if (conversations.errors) {
      console.error(conversations.errors);
      notifications.show({
        color: "red",
        title: "Error",
        message: "Failed to fetch conversations",
      });
    }
  }, [conversations]);

  return (
    <Stack align="stretch" justify="flex-start" gap="xs" pr="md">
      {conversations.loading ? (
        <Center>
          <Loader />
        </Center>
      ) : (
        conversations.conversations.map((_, index) => (
          <Summary key={index} summary={_} />
        ))
      )}
    </Stack>
  );
}
