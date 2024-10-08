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
  deleted: boolean;
}

export interface ConversationCriteria {
  globalSearch?: string;
  minMessageCount?: number;
  maxMessageCount?: number;
  minCreatedAt?: string;
  maxCreatedAt?: string;
  summary?: string;
  userFirstname?: string;
  userLastname?: string;
  deleted?: boolean;

  getUrl(): string;
}

export class ConversationCriteriaImpl implements ConversationCriteria {
  globalSearch?: string;
  minMessageCount?: number;
  maxMessageCount?: number;
  minCreatedAt?: string;
  maxCreatedAt?: string;
  summary?: string;
  userFirstname?: string;
  userLastname?: string;
  deleted?: boolean;

  constructor(init?: Partial<ConversationCriteria>) {
    Object.assign(this, init);
  }

  getUrl(): string {
    const params = new URLSearchParams();
    if (this.globalSearch) params.append("globalSearch", this.globalSearch);
    if (this.minMessageCount !== undefined)
      params.append("minMessageCount", this.minMessageCount.toString());
    if (this.maxMessageCount !== undefined)
      params.append("maxMessageCount", this.maxMessageCount.toString());
    if (this.minCreatedAt) params.append("minCreatedAt", this.minCreatedAt);
    if (this.maxCreatedAt) params.append("maxCreatedAt", this.maxCreatedAt);
    if (this.summary) params.append("summary", this.summary);
    if (this.userFirstname) params.append("userFirstname", this.userFirstname);
    if (this.userLastname) params.append("userLastname", this.userLastname);
    if (this.deleted !== undefined)
      params.append("deleted", this.deleted.toString());
    return params.toString();
  }
}
