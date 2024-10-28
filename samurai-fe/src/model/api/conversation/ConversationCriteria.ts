import { SearchCriteria } from "../page/SearchCriteria.ts";

export interface ConversationCriteria extends SearchCriteria {
  globalSearch?: string;
  minMessageCount?: number;
  maxMessageCount?: number;
  minCreatedAt?: string;
  maxCreatedAt?: string;
  summary?: string;
  userFullName?: string;
  deleted?: boolean;
}
