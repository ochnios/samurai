import { Alert, Box, Flex, rem, Stack } from "@mantine/core";
import { IconUserCircle } from "@tabler/icons-react";
import { Message } from "../../../../model/api/message/Message.ts";
import FormattedText from "../FormattedText.tsx";

export default function UserMessage(props: Message) {
  return (
    <Stack w="100%" align="flex-end" justify="center">
      <Flex gap="xs" align="flex-start" maw="80%">
        <Alert variant="filled" radius="lg" p="xs">
          <FormattedText markdown={props.content} />
        </Alert>
        <Box>
          <IconUserCircle
            stroke={1.5}
            style={{ width: rem(32), height: rem(32) }}
          />
        </Box>
      </Flex>
    </Stack>
  );
}
