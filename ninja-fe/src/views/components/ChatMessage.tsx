import { Alert, Flex, Image, rem, Stack, Text } from "@mantine/core";
import { IconUserCircle } from "@tabler/icons-react";

export enum MessageType {
  USER,
  ASSISTANT,
}

interface Message {
  content: string;
  type: MessageType;
}

export default function ChatMessage(props: Message) {
  return (
    <Stack
      w="100%"
      align={props.type === MessageType.ASSISTANT ? "flex-start" : "flex-end"}
      justify="center"
    >
      <Flex gap="xs" align="flex-start" style={{ maxWidth: "80%" }}>
        {props.type === MessageType.ASSISTANT ? (
          <>
            <Image
              src="/logo_small.png"
              fit="contain"
              style={{ width: rem(40), height: rem(40) }}
            />
            <Alert variant="default" radius="lg" p="xs">
              <Text fz="1.1em">{props.content}</Text>
            </Alert>
          </>
        ) : (
          <>
            <Alert variant="filled" radius="lg" p="xs">
              <Text fz="1.1em">{props.content}</Text>
            </Alert>
            <IconUserCircle
              stroke={1.5}
              radius="xl"
              style={{ width: rem(40), height: rem(40) }}
            />
          </>
        )}
      </Flex>
    </Stack>
  );
}
