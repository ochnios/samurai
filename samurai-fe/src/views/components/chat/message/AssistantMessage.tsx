import { Alert, Box, Flex, Image, Loader, Stack } from "@mantine/core";
import { Message } from "../../../../model/api/message/Message.ts";
import { MessageStatus } from "../../../../model/api/message/MessageStatus.ts";
import FormattedText from "../FormattedText.tsx";

export default function AssistantMessage(props: Message) {
  return (
    <Stack w="100%" align="flex-start" justify="center">
      <Flex gap="xs" align="flex-start" maw="80%">
        <Image src="/logo_small.png" fit="contain" w={32} />
        <Alert
          variant="default"
          radius="lg"
          p="xs"
          color={props.status === MessageStatus.ERROR ? "red" : ""}
        >
          {props.status === MessageStatus.LOADING ? (
            <Box px="sm">
              <Loader type="dots" size="sm" />
            </Box>
          ) : (
            <Box c="light-dark(var(--mantine-color-gray-7), var(--mantine-color-dark-0))">
              <FormattedText markdown={props.content} />
            </Box>
          )}
        </Alert>
      </Flex>
    </Stack>
  );
}
