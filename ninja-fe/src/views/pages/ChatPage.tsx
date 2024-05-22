import { Divider, Grid, ScrollArea, Stack, Title } from "@mantine/core";
import classes from "./ChatPage.module.css";
import ChatInput from "../components/ChatInput.tsx";
import ChatMessage, { MessageType } from "../components/ChatMessage.tsx";

const messages = [
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
  return (
    <>
      <Grid className={classes.grid}>
        <Grid.Col
          span={{ base: 12, md: 8, lg: 9 }}
          className={classes.messages}
        >
          <ScrollArea h="calc(100vh - 180px)" visibleFrom="sm">
            <Stack align="strech" gap="md" h="100%" mb="lg" justify="flex-end">
              {messages.map(
                (message, index) =>
                  index < 20 && (
                    <ChatMessage
                      key={index}
                      content={message.content}
                      type={message.type}
                    />
                  ),
              )}
            </Stack>
          </ScrollArea>
          <Divider my="sm"></Divider>
          <ChatInput className={classes.input} />
        </Grid.Col>
        <Grid.Col
          span={{ base: 12, md: 4, lg: 3 }}
          className={classes.conversations}
          visibleFrom="md" // TODO button to open conversations on mobile
        >
          <Title order={4}>Conversations</Title>
        </Grid.Col>
      </Grid>
    </>
  );
}
