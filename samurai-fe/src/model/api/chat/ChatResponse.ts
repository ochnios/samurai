import { MessageSource } from "../message/MessageSource.ts";

export interface ChatResponse {
  conversationId: string;
  messageId: string;
  completion: string;
  summary?: string;
  sources?: MessageSource[];
}
