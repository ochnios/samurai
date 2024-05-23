import { ActionIcon, Image, rem, Text, TextInput } from "@mantine/core";
import { IconSend } from "@tabler/icons-react";
import { useEffect, useRef, useState } from "react";

interface ChatInputProps {
  submitMessage: (content: string) => void;
}

export default function ChatInput(props: ChatInputProps) {
  const inputRef = useRef<HTMLInputElement>(null);
  const [error, setError] = useState("");

  useEffect(() => {}, [error]);

  const isInputValid = () => {
    return inputRef.current && inputRef.current.value.trim().length >= 3;
  };

  const submitMessage = (e: any) => {
    e.preventDefault();
    if (isInputValid()) {
      props.submitMessage(inputRef.current!.value);
      inputRef.current!.value = "";
    } else {
      setError(
        "Message should be at least 3 characters long excluding trailing whitespaces",
      );
    }
  };

  return (
    <form onSubmit={submitMessage}>
      <TextInput
        ref={inputRef}
        error={
          error && (
            <Text size="sm" ta="center">
              {error}
            </Text>
          )
        }
        onInput={() => error && isInputValid() && setError("")}
        radius="xl"
        size="md"
        ta="center"
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
            variant="filled"
            onClick={submitMessage}
          >
            <IconSend
              stroke={1.5}
              style={{ width: rem(18), height: rem(18) }}
            />
          </ActionIcon>
        }
      />
    </form>
  );
}
