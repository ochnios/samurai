import {
  Anchor,
  Badge,
  Flex,
  Loader,
  Popover,
  ScrollAreaAutosize,
} from "@mantine/core";
import FormattedText from "../FormattedText.tsx";
import { MessageSource } from "../../../../model/api/message/MessageSource.ts";
import config from "../../../../config.ts";
import { useState } from "react";
import { showErrorMessage } from "../../../../utils.ts";
import { fetchDocumentContent } from "../../../../model/service/documentService.ts";

const apiUrl = config.baseUrl;

function getBadgeColor(source: MessageSource): string {
  return source.deleted
    ? "gray"
    : source.updated
      ? "gray"
      : "var(--mantine-primary-color-filled)";
}

export default function MessageSourcePopover(props: MessageSource) {
  const [loading, setLoading] = useState(false);
  const [content, setContent] = useState("");

  function loadContent() {
    if (props.documentId && content.length == 0) {
      setLoading(true);
      fetchDocumentContent(props.documentId)
        .then((response) => setContent(response.content))
        .catch(() => {
          showErrorMessage("Failed to fetch document content");
        })
        .finally(() => setLoading(false));
    }
  }

  return (
    <Popover
      position="top"
      shadow="md"
      withArrow
      arrowSize={12}
      onOpen={() => loadContent()}
    >
      <Popover.Target>
        <Badge
          size="sm"
          style={{ cursor: "pointer" }}
          color={getBadgeColor(props)}
        >
          {props.originalTitle}
          {props.deleted && " (deleted)"}
          {props.updated && " (updated)"}
        </Badge>
      </Popover.Target>
      <Popover.Dropdown>
        <ScrollAreaAutosize maw="33vw" mah="40vh">
          {props.deleted ? (
            "Document has been deleted"
          ) : loading ? (
            <Loader />
          ) : (
            <FormattedText markdown={content} />
          )}
        </ScrollAreaAutosize>
        {props.documentId && (
          <Flex justify="flex-end" pt="sm">
            <Anchor
              href={`${apiUrl}/documents/${props.documentId}/download?inline=true`}
              target="_blank"
              td="underline"
              fz="sm"
            >
              Open document &rarr;
            </Anchor>
          </Flex>
        )}
      </Popover.Dropdown>
    </Popover>
  );
}
