import { Message } from "../message/Message.ts";
import { ConversationSummary } from "./ConversationSummary.ts";

export interface Conversation extends ConversationSummary {
  messages: Message[];
}
