export interface ChatResponse {
  conversationId: string;
  messageId: string;
  completion: string;
  summary?: string;
}
