import { Divider, Grid, Image, ScrollArea, Stack } from "@mantine/core";
import { notifications } from "@mantine/notifications";
import classes from "./ChatPage.module.css";
import ChatInput from "../components/ChatInput.tsx";
import ChatMessage, {
  Message,
  MessageStatus,
  MessageType,
} from "../components/ChatMessage.tsx";
import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import {
  addConversationSummary,
  setActiveConversation,
} from "../../reducers/conversationsSlice.ts";
import axios from "axios";
import { RootState } from "../../store.ts";
import { validate } from "uuid";

interface ConversationDto {
  id: string;
  messages: Message[];
  assistantId: string;
  userId?: string;
  createdAt: string;
}

interface ChatRequestDto {
  conversationId?: string;
  question: string;
}

interface ChatResponseDto {
  conversationId: string;
  completion: string;
}

const fetchConversation = async (
  assistantId: string,
  conversationId: string,
): Promise<ConversationDto | void> => {
  return await axios
    .get<ConversationDto>(
      `/assistants/${assistantId}/conversations/${conversationId}`,
    )
    .then((response) => {
      return response.data;
    })
    .catch((error) => {
      console.error(error);
      notifications.show({
        color: "red",
        title: "Error",
        message: "Failed to fetch given conversation",
      });
    });
};

export default function ChatPage() {
  const viewport = useRef<HTMLDivElement>(null);
  const { id } = useParams();
  const dispatch = useDispatch();
  const assistant = useSelector((state: RootState) => state.assistant);
  const [conversationId, setConversationId] = useState("");
  const [messages, setMessages] = useState<Message[]>([]);

  useEffect(() => {
    if (assistant.current) {
      if (validate(id ?? "")) {
        setConversationId(id!);
        fetchConversation(assistant.current!.id, id!).then((c) =>
          setMessages(c ? c.messages : []),
        );
      } else {
        setConversationId("");
        setMessages([]);
      }
    } else {
      notifications.show({
        color: "orange",
        title: "Warning",
        message: "Please select active assistant first",
      });
    }
  }, [id]);

  useEffect(() => {
    dispatch(setActiveConversation(conversationId));
  }, [conversationId]);

  useEffect(() => {
    viewport.current!.scrollTo({
      top: viewport.current!.scrollHeight,
      behavior: "smooth",
    });
  }, [messages]);

  const sendMessage = async (message: Message): Promise<Message> => {
    return await axios
      .post<ChatResponseDto>(`/assistants/${assistant.current!.id}/chat`, {
        conversationId: conversationId,
        question: message.content,
      } as ChatRequestDto)
      .then((response) => {
        if (!conversationId) {
          setConversationId(response.data.conversationId);
          dispatch(
            addConversationSummary({
              id: response.data.conversationId,
              summary: "New conversation",
            }),
          );
        }
        return {
          content: response.data.completion,
          type: MessageType.ASSISTANT,
        };
      })
      .catch((error) => {
        console.error(error);
        notifications.show({
          color: "red",
          title: "Error",
          message: "Failed to send the message",
        });
        return {
          content:
            "Sorry, I am not able to answer your question right now, but I am working to fix it",
          type: MessageType.ASSISTANT,
          status: MessageStatus.ERROR,
        };
      });
  };

  const submitMessage = (content: string) => {
    const userMessage: Message = {
      content: content,
      type: MessageType.USER,
    };

    const answerPlaceholder: Message = {
      type: MessageType.ASSISTANT,
      status: MessageStatus.LOADING,
    };

    setMessages((prevMessages) => [
      ...prevMessages,
      userMessage,
      answerPlaceholder,
    ]);

    sendMessage(userMessage).then((answer) =>
      setMessages((prevMessages) => [
        ...prevMessages.slice(0, prevMessages.length - 1),
        answer,
      ]),
    );
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
                    content={message.content}
                    type={message.type}
                    status={message.status}
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
