import { ActionIcon, Image, rem, Text, Textarea } from "@mantine/core";
import { IconSend } from "@tabler/icons-react";
import { useEffect, useRef, useState } from "react";
import { useLocation } from "react-router-dom";
import { validateChatMessage } from "../../../model/service/chatService.ts";

interface ChatInputProps {
  disabled: boolean;
  submitMessage: (content: string) => void;
}

export default function ChatInput(props: ChatInputProps) {
  const location = useLocation();
  const textareaRef = useRef<HTMLTextAreaElement>(null);
  const [error, setError] = useState("");
  const [refresh, setRefresh] = useState(true);

  useEffect(() => {
    setError("");
    textareaRef.current!.value = "";
  }, [location]);

  const isInputValid = () => {
    return textareaRef.current && textareaRef.current.value.trim().length >= 3;
  };

  const submitMessage = (e: any) => {
    e.preventDefault();
    if (validateChatMessage(textareaRef.current?.value)) {
      props.submitMessage(textareaRef.current!.value);
      textareaRef.current!.value = "";
    } else {
      setError(
        "Message should be at least 3 characters long excluding trailing whitespaces",
      );
    }
  };

  return (
    <form onSubmit={submitMessage}>
      <Textarea
        autosize
        maxRows={20}
        style={{ whiteSpace: "pre-wrap" }}
        ref={textareaRef}
        error={
          error && (
            <Text size="sm" ta="center" span>
              {error}
            </Text>
          )
        }
        onInput={() => error && isInputValid() && setError("")}
        radius="xl"
        size="md"
        ta="center"
        placeholder="How can I help you today?"
        rightSectionWidth={42}
        disabled={props.disabled}
        leftSection={
          <Image
            src="/logo_small.png"
            fit="contain"
            style={{ width: rem(24), height: rem(24) }}
          />
        }
        rightSection={
          <ActionIcon
            onClick={submitMessage}
            size={32}
            radius="xl"
            variant="filled"
            disabled={props.disabled}
          >
            <IconSend
              stroke={1.5}
              style={{ width: rem(18), height: rem(18) }}
            />
          </ActionIcon>
        }
        onKeyDown={(e) => {
          if (!textareaRef.current) return;
          if (e.code === "Enter") {
            if (e.ctrlKey || e.shiftKey) {
              textareaRef.current.value += "\n";
              setRefresh(!refresh);
            } else submitMessage(e);
          }
        }}
      />
    </form>
  );
}
