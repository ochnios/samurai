import { MessageStatus } from "./MessageStatus.ts";
import { MessageType } from "./MessageType.ts";

export interface Message {
  content?: string;
  type: MessageType;
  status?: MessageStatus;
}
