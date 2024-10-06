import axios from "axios";
import { Conversation, ConversationSummary } from "../api/Conversation.ts";
import { Page } from "../api/Page.ts";
import { PageRequest } from "../api/PageRequest.ts";
import { Patch } from "../api/Patch.ts";

const conversationsUrl = "/conversations";

export const fetchConversation = async (
  conversationId: string,
): Promise<Conversation> => {
  return await axios
    .get<Conversation>(`${conversationsUrl}/${conversationId}`)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const fetchConversationsSummaries = async (
  pageRequest: PageRequest,
): Promise<Page<ConversationSummary>> => {
  const pageRequestParams = pageRequest.getUrl();
  return await axios
    .get<Page<ConversationSummary>>(
      `${conversationsUrl}/summaries?${pageRequestParams}`,
    )
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const patchConversation = async (
  conversationId: string,
  patch: Patch[],
): Promise<Conversation> => {
  return await axios
    .patch<Conversation>(`${conversationsUrl}/${conversationId}`, patch)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const deleteConversation = async (
  conversationId: string,
): Promise<void> => {
  return await axios
    .delete<void>(`${conversationsUrl}/${conversationId}`)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const validateSummary = (summary: string): string => {
  const len = summary.trim().length;
  return len >= 3 && len <= 32 ? "" : "Must be between 3 and 32 characters";
};
