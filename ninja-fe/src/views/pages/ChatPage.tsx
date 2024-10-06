import { Divider, Grid, Image, Loader, ScrollArea, Stack } from "@mantine/core";
import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { validate } from "uuid";
import { useAppDispatch } from "../../hooks/useAppDispatch.ts";
import {
  Message,
  MessageStatus,
  MessageType,
} from "../../model/api/Message.ts";
import { sendMessage } from "../../model/service/chatService.ts";
import { fetchConversation } from "../../model/service/conversationService.ts";
import {
  addConversationSummary,
  setActiveConversation,
} from "../../reducers/conversationsSlice.ts";
import { showErrorMessage } from "../../utils.ts";
import ChatInput from "../components/chat/ChatInput.tsx";
import ChatMessage from "../components/chat/ChatMessage.tsx";
import classes from "./ChatPage.module.css";

export default function ChatPage() {
  const viewport = useRef<HTMLDivElement>(null);
  const { id } = useParams();
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [conversationId, setConversationId] = useState("");
  const [messages, setMessages] = useState<Message[]>([]);

  useEffect(() => {
    viewport.current!.scrollTo({
      top: viewport.current!.scrollHeight,
      behavior: "smooth",
    });
  }, [messages]);

  useEffect(() => {
    dispatch(setActiveConversation(conversationId));
  }, [conversationId]);

  useEffect(() => {
    if (validate(id ?? "")) {
      setLoading(true);
      setConversationId(id!);
      fetchConversation(id!)
        .then((conversation) => {
          if (conversation) setMessages(conversation.messages);
          else setMessages([]);
        })
        .catch(() => {
          showErrorMessage("Failed to fetch selected conversation");
          navigate("/conversations/new");
        })
        .finally(() => setLoading(false));
    } else {
      if (id != "new") navigate("/conversations/new");
      setConversationId("");
      setMessages([]);
    }
  }, [id]);

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

    sendMessage({
      conversationId: conversationId,
      question: content,
    }).then((response) => {
      let answerMessage: Message;
      if (response) {
        answerMessage = {
          content: response.completion,
          type: MessageType.ASSISTANT,
        };
        if (!conversationId) {
          setConversationId(response.conversationId);
          dispatch(
            addConversationSummary({
              id: response.conversationId,
              summary: response.summary!,
            }),
          );
        }
      } else {
        answerMessage = {
          content:
            "Sorry, I am not able to answer your question right now but I am working to fix it",
          type: MessageType.ASSISTANT,
          status: MessageStatus.ERROR,
        };
        showErrorMessage("Failed to send the message");
      }

      setMessages((prevMessages) => [
        ...prevMessages.slice(0, prevMessages.length - 1),
        answerMessage,
      ]);
    });
  };

  return (
    <>
      <Grid className={classes.grid}>
        <Grid.Col span={{ base: 12, lg: 1, xl: 2 }} p={0}></Grid.Col>
        <Grid.Col
          span={{ base: 12, lg: 10, xl: 8 }}
          className={classes.messages}
        >
          <ScrollArea
            h="calc(100dvh - 180px)"
            viewportRef={viewport}
            className={classes.scrollArea}
          >
            {!loading && messages.length > 0 ? (
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
                {loading ? (
                  <Loader />
                ) : (
                  <Image
                    src="/logo_medium.png"
                    h="100%"
                    w="auto"
                    fit="contain"
                  />
                )}
              </Stack>
            )}
          </ScrollArea>
          <Divider my="sm"></Divider>
          <ChatInput submitMessage={(value) => submitMessage(value)} />
        </Grid.Col>
        <Grid.Col span={{ base: 12, lg: 1, xl: 2 }} p={0}></Grid.Col>
      </Grid>
    </>
  );
}
