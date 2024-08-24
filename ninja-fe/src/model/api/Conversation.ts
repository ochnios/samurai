import { Message } from "./Message.ts";

export interface Conversation {
  id: string;
  messages: Message[];
  assistantId: string;
  userId?: string;
  createdAt: string;
}
