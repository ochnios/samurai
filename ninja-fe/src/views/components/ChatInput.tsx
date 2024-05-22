import {
  ActionIcon,
  Image,
  rem,
  TextInput,
  TextInputProps,
  useMantineTheme,
} from "@mantine/core";
import { IconSend } from "@tabler/icons-react";

export default function ChatInput(props: TextInputProps) {
  const theme = useMantineTheme();

  return (
    <TextInput
      radius="xl"
      size="md"
      placeholder="How can I assist you today?"
      rightSectionWidth={42}
      leftSection={
        <Image
          src="/logo_small.png"
          fit="contain"
          style={{ width: rem(24), height: rem(24) }}
        />
      }
      rightSection={
        <ActionIcon
          size={32}
          radius="xl"
          color={theme.primaryColor}
          variant="filled"
        >
          <IconSend stroke={1.5} style={{ width: rem(18), height: rem(18) }} />
        </ActionIcon>
      }
      {...props}
    />
  );
}
