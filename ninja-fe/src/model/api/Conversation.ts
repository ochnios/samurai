import { Message } from "../helper/Message.ts";

export interface Conversation {
  id: string;
  messages: Message[];
  assistantId: string;
  userId?: string;
  createdAt: string;
}
