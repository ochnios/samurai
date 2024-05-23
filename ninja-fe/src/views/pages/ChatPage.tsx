import { Divider, Grid, Image, ScrollArea, Stack } from "@mantine/core";
import classes from "./ChatPage.module.css";
import ChatInput from "../components/ChatInput.tsx";
import ChatMessage, { MessageType } from "../components/ChatMessage.tsx";
import { useEffect, useRef, useState } from "react";

const data = [
  {
    content: "Can you explain how to use feature ABC?",
    type: MessageType.USER,
  },
  {
    content: `Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse in ipsum this \
semper, lobortis nibh ac, facilisis erat. Praesent nec turpis ultricies, commodo augue et, rhoncus \
enim. Fusce sed faucibus dolor. Duis et lacus congue, gravida est a, scelerisque quam. \
Mauris in nisi placerat eros dapibus tempus id et magna.`,
    type: MessageType.ASSISTANT,
  },
  {
    content: "I'm facing an error with the application. It keeps crashing.",
    type: MessageType.USER,
  },
  {
    content:
      "I'm sorry to hear that. Could you provide more details on the error message?",
    type: MessageType.ASSISTANT,
  },
  {
    content: "Yes, the error code is 404. I can't seem to find the page.",
    type: MessageType.USER,
  },
  {
    content:
      "Error 404 indicates the page was not found. Let me help you locate it.",
    type: MessageType.ASSISTANT,
  },
  { content: "Thank you, that would be great!", type: MessageType.USER },
  {
    content:
      "No problem. Please check if the URL you are trying to access is correct.",
    type: MessageType.ASSISTANT,
  },
  {
    content: "Can you tell me how to reset my password?",
    type: MessageType.USER,
  },
  {
    content:
      "Absolutely. Just go to the settings page and click 'Reset Password'.",
    type: MessageType.ASSISTANT,
  },
  {
    content: "Got it. I've managed to reset my password.",
    type: MessageType.USER,
  },
  {
    content:
      "Glad I could help! Is there anything else you need assistance with?",
    type: MessageType.ASSISTANT,
  },
];

export default function ChatPage() {
  const viewport = useRef<HTMLDivElement>(null);
  const [messages, setMessages] = useState(data);
  useEffect(() => {
    viewport.current!.scrollTo({
      top: viewport.current!.scrollHeight,
      behavior: "smooth",
    });
  }, [messages]);

  const submitMessage = (content: string) => {
    const newMessage = {
      content: content,
      type: MessageType.USER,
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
                    content={message.content}
                    type={message.type}
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
