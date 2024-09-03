import { Message } from "./Message.ts";

export interface Conversation {
  id: string;
  username: string;
  messages: Message[];
  summary: string;
  createdAt: string;
}
