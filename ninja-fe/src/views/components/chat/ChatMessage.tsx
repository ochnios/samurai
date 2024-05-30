import {
  Alert,
  Box,
  Flex,
  Image,
  Loader,
  rem,
  Stack,
  Text,
} from "@mantine/core";
import { IconUserCircle } from "@tabler/icons-react";

export enum MessageType {
  USER = "USER",
  ASSISTANT = "ASSISTANT",
}

export enum MessageStatus {
  LOADING = "LOADING",
  ERROR = "ERROR",
}

export interface Message {
  content?: string;
  type: MessageType;
  status?: MessageStatus;
}

export default function ChatMessage(props: Message) {
  return (
    <Stack
      w="100%"
      align={props.type === MessageType.ASSISTANT ? "flex-start" : "flex-end"}
      justify="center"
    >
      <Flex gap="xs" align="flex-start" style={{ maxWidth: "80%" }}>
        {props.type === MessageType.USER ? (
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
        ) : (
          <>
            <Image
              src="/logo_small.png"
              fit="contain"
              style={{ width: rem(40), height: rem(40) }}
            />
            <Alert
              variant={
                props.status === MessageStatus.ERROR ? "outline" : "default"
              }
              radius="lg"
              p="xs"
              color={props.status === MessageStatus.ERROR ? "red" : ""}
            >
              {props.status === MessageStatus.LOADING ? (
                <Box px="sm">
                  <Loader type="dots" size="sm" />
                </Box>
              ) : (
                <Text fz="1.1em">{props.content}</Text>
              )}
            </Alert>
          </>
        )}
      </Flex>
    </Stack>
  );
}
