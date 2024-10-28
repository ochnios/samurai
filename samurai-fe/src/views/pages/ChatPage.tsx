import {
  Box,
  Divider,
  Grid,
  Image,
  Loader,
  ScrollArea,
  Stack,
} from "@mantine/core";
import { useElementSize } from "@mantine/hooks";
import { useEffect, useRef, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { validate } from "uuid";
import { useAppDispatch } from "../../hooks/useAppDispatch.ts";
import { Message } from "../../model/api/message/Message.ts";
import { MessageStatus } from "../../model/api/message/MessageStatus.ts";
import { MessageType } from "../../model/api/message/MessageType.ts";
import { sendMessage } from "../../model/service/chatService.ts";
import { fetchConversation } from "../../model/service/conversationService.ts";
import {
  addConversationSummary,
  setActiveConversation,
} from "../../reducers/conversationsSlice.ts";
import { showErrorMessage } from "../../utils.ts";
import ChatInput from "../components/chat/ChatInput.tsx";
import ChatMessage from "../components/chat/message/ChatMessage.tsx";
import classes from "./ChatPage.module.css";

export default function ChatPage() {
  const viewport = useRef<HTMLDivElement>(null);
  const { ref, height } = useElementSize();

  const { id } = useParams();
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
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
      id: messages.length.toString(),
      content: content,
      type: MessageType.USER,
    };

    const answerPlaceholder: Message = {
      id: "placeholder",
      content: "placeholder",
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
          id: response.messageId,
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
          navigate(`/conversations/${response.conversationId}`);
        }
      } else {
        answerMessage = {
          id: "error",
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
      <Grid p={0}>
        <Grid.Col span={{ base: 12, lg: 1 }} p={0}></Grid.Col>
        <Grid.Col span={{ base: 12, lg: 10 }} p={0}>
          <ScrollArea
            h={window.innerHeight - height - 105}
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
                  <ChatMessage key={index} {...message} />
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
          <Box ref={ref}>
            <ChatInput
              disabled={queryParams.get("preview") === "1"}
              submitMessage={(value) => submitMessage(value)}
            />
          </Box>
        </Grid.Col>
        <Grid.Col span={{ base: 12, lg: 1 }} p={0}></Grid.Col>
      </Grid>
    </>
  );
}
