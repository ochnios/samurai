import { Badge, Loader } from "@mantine/core";
import { DocumentStatus } from "../../../model/api/document/DocumentStatus.ts";

export interface DocumentStatusBadgeProps {
  status: DocumentStatus;
}

function getBadgeColor(status: DocumentStatus): string {
  switch (status) {
    case DocumentStatus.ACTIVE:
      return "green";
    case DocumentStatus.IN_PROGRESS:
      return "orange";
    case DocumentStatus.ARCHIVED:
      return "yellow";
    case DocumentStatus.FAILED:
      return "red";
    case DocumentStatus.UPLOADED:
      return "violet";
  }
}

export default function DocumentStatusBadge(props: DocumentStatusBadgeProps) {
  const color = getBadgeColor(props.status);
  return (
    <Badge color={color} w="100%">
      {props.status.toString()}{" "}
      {props.status === DocumentStatus.IN_PROGRESS && (
        <Loader size={10} color="white" />
      )}
    </Badge>
  );
}
