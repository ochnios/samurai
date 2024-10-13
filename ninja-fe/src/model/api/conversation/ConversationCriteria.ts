export interface ConversationCriteria {
  globalSearch?: string;
  minMessageCount?: number;
  maxMessageCount?: number;
  minCreatedAt?: string;
  maxCreatedAt?: string;
  summary?: string;
  userFullName?: string;
  deleted?: boolean;

  getUrl(): string;
}
