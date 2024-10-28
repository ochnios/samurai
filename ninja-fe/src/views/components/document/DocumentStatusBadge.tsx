import { Badge } from "@mantine/core";
import { DocumentStatus } from "../../../model/api/document/DocumentStatus.ts";

export interface DocumentStatusBadgeProps {
  status: DocumentStatus;
}

function getBadgeColor(status: DocumentStatus) {
  switch (status) {
    case DocumentStatus.ACTIVE:
      return "green";
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
  return <Badge color={color}>{props.status.toString()}</Badge>;
}
