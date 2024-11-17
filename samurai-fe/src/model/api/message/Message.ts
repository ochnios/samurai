import { MessageStatus } from "./MessageStatus.ts";
import { MessageType } from "./MessageType.ts";
import { MessageSource } from "./MessageSource.ts";

export interface Message {
  id: string;
  content: string;
  type: MessageType;
  status?: MessageStatus;
  sources?: MessageSource[];
}
