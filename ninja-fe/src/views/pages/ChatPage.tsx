import { Divider, Grid, Image, ScrollArea, Stack } from "@mantine/core";
import classes from "./ChatPage.module.css";
import ChatInput from "../components/ChatInput.tsx";
import ChatMessage, {
  Message,
  MessageType,
} from "../components/ChatMessage.tsx";
import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { setCurrent } from "../../reducers/conversationsSlice.ts";
import axios from "axios";
import { RootState } from "../../store.ts";

export default function ChatPage() {
  const viewport = useRef<HTMLDivElement>(null);
  const { conversationId } = useParams();
  const dispatch = useDispatch();
  const assistant = useSelector((state: RootState) => state.assistant);
  const [messages, setMessages] = useState<Message[]>([]);

  useEffect(() => {
    async function fetchConversation() {
      await axios
        .get(
          `/assistants/${assistant.current!.id}/conversations/${conversationId}`,
        )
        .then((response) => setMessages(response.data.messages));
    }

    if (conversationId && assistant.current) {
      dispatch(setCurrent(conversationId));
      fetchConversation();
    }
  }, [conversationId]);

  useEffect(() => {
    viewport.current!.scrollTo({
      top: viewport.current!.scrollHeight,
      behavior: "smooth",
    });
  }, [messages]);

  const submitMessage = (content: string) => {
    const newMessage: Message = {
      id: "tbd",
      content: content,
      type: MessageType.USER,
      createdAt: "tbd",
    };
    setMessages([...messages, newMessage]);
  };

  return (
    <>
      <Grid className={classes.grid}>
        <Grid.Col span={{ base: 12, lg: 1, xl: 2 }}></Grid.Col>
        <Grid.Col
          span={{ base: 12, lg: 10, xl: 8 }}
          className={classes.messages}
        >
          <ScrollArea
            h="calc(100dvh - 180px)"
            viewportRef={viewport}
            className={classes.scrollArea}
          >
            {messages.length > 0 ? (
              <Stack
                h="100%"
                align="strech"
                justify="flex-end"
                gap="md"
                mb="lg"
              >
                {messages.map((message, index) => (
                  <ChatMessage
                    key={index}
                    id={message.id}
                    content={message.content}
                    type={message.type}
                    createdAt={message.createdAt}
                  />
                ))}
              </Stack>
            ) : (
              <Stack h="100%" align="center" justify="center">
                <Image src="/logo_medium.png" h="100%" w="auto" fit="contain" />
              </Stack>
            )}
          </ScrollArea>
          <Divider my="sm"></Divider>
          <ChatInput submitMessage={(value) => submitMessage(value)} />
        </Grid.Col>
        <Grid.Col span={{ base: 12, lg: 1, xl: 2 }}></Grid.Col>
      </Grid>
    </>
  );
}
