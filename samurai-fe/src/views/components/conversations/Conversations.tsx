import { Center, Loader, Stack, Text } from "@mantine/core";
import { useEffect } from "react";
import { Link, useParams } from "react-router-dom";
import { useAppDispatch } from "../../../hooks/useAppDispatch.ts";
import { useAppSelector } from "../../../hooks/useAppSelector.ts";
import { fetchConversations } from "../../../reducers/conversationsSlice.ts";
import { RootState } from "../../../store.ts";
import { showErrorMessage } from "../../../utils.ts";
import Summary from "./Summary.tsx";

export default function Conversations() {
  const { id } = useParams();
  const dispatch = useAppDispatch();
  const conversations = useAppSelector(
    (state: RootState) => state.conversations,
  );

  useEffect(() => {
    dispatch(fetchConversations());
  }, [dispatch]);

  useEffect(() => {
    if (conversations.errors) showErrorMessage("Failed to fetch conversations");
  }, [conversations.errors]);

  return (
    <Stack align="stretch" justify="flex-start" gap="xs" pr="md">
      {conversations.loading ? (
        <Center>
          <Loader />
        </Center>
      ) : conversations.conversations.length > 0 ? (
        conversations.conversations.map((_, index) => (
          <Summary
            key={index}
            id={_.id}
            summary={_.summary}
            active={_.id === id}
          />
        ))
      ) : (
        <Center>
          <Text size="sm">
            Such empty here! Let's start{" "}
            <Link
              to="conversations/new"
              style={{ color: "var(--mantine-color-anchor)" }}
            >
              new chat
            </Link>
          </Text>
        </Center>
      )}
    </Stack>
  );
}
