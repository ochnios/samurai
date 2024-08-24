export interface Message {
  content?: string;
  type: MessageType;
  status?: MessageStatus;
}

export enum MessageStatus {
  LOADING = "LOADING",
  ERROR = "ERROR",
}

export enum MessageType {
  USER = "USER",
  ASSISTANT = "ASSISTANT",
}
