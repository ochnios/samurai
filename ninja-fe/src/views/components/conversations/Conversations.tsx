import { Center, Loader, Stack } from "@mantine/core";
import { useEffect } from "react";
import { useSelector } from "react-redux";
import { useAppDispatch } from "../../../hooks.ts";
import { fetchAvailableAssistants } from "../../../reducers/assistantSlice.ts";
import { fetchConversations } from "../../../reducers/conversationsSlice.ts";
import { RootState } from "../../../store.ts";
import Summary from "./Summary.tsx";

export default function Conversations() {
  const dispatch = useAppDispatch();
  const assistant = useSelector((state: RootState) => state.assistant);
  const conversations = useSelector((state: RootState) => state.conversations);

  useEffect(() => {
    dispatch(fetchAvailableAssistants());
  }, []);

  useEffect(() => {
    assistant.current && dispatch(fetchConversations(assistant.current.id));
  }, [assistant.current]);

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
