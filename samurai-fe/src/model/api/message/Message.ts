import { MessageStatus } from "./MessageStatus.ts";
import { MessageType } from "./MessageType.ts";

export interface Message {
  id: string;
  content: string;
  type: MessageType;
  status?: MessageStatus;
}
