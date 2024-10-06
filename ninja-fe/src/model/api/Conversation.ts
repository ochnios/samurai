import { Message } from "./Message.ts";
import { User } from "./User.ts";

export interface ConversationSummary {
  id: string;
  summary: string;
  createdAt: string;
}

export interface Conversation extends ConversationSummary {
  messages: Message[];
}

export interface ConversationDetails extends ConversationSummary {
  user: User;
  messageCount: number;
}
