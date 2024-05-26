import { Center, Flex, Loader, Stack, TextInput } from "@mantine/core";
import { IconEdit, IconTrash } from "@tabler/icons-react";
import classes from "./Conversations.module.css";
import { useSelector } from "react-redux";
import { RootState } from "../../store.ts";
import { useEffect } from "react";
import { fetchConversations } from "../../reducers/conversationsSlice.ts";
import { useAppDispatch } from "../../hooks.ts";
import { fetchAvailableAssistants } from "../../reducers/assistantSlice.ts";
import { useNavigate } from "react-router-dom";

export default function Conversations() {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const assistant = useSelector((state: RootState) => state.assistant);
  const conversations = useSelector((state: RootState) => state.conversations);

  useEffect(() => {
    dispatch(fetchAvailableAssistants());
  }, []);

  useEffect(() => {
    if (assistant.current) dispatch(fetchConversations(assistant.current.id));
  }, [assistant.current]);

  const isCurrent = (conversationId: string) => {
    return conversations.currentId === conversationId;
  };

  return (
    <Stack align="stretch" justify="flex-start" gap="xs" pr="md">
      {conversations.loading ? (
        <Center>
          <Loader />
        </Center>
      ) : (
        conversations.conversations.map((_, index) => (
          <TextInput
            key={index}
            type="text"
            size="sm"
            placeholder={_.summary}
            defaultValue={_.summary}
            readOnly={!isCurrent(_.id)}
            rightSectionWidth={50}
            rightSection={
              isCurrent(_.id) && (
                <Flex>
                  <IconEdit size={20} cursor="pointer" />
                  <IconTrash size={20} cursor="pointer" />
                </Flex>
              )
            }
            className={classes.conversationInput}
            onClick={(e) => {
              e.preventDefault();
              console.log("conversation click");
              navigate(`/conversations/${_.id}`);
            }}
          />
        ))
      )}
    </Stack>
  );
}
