import { User } from "../user/User.ts";
import { ConversationSummary } from "./ConversationSummary.ts";

export interface ConversationDetails extends ConversationSummary {
  user: User;
  messageCount: number;
  deleted: boolean;
}
