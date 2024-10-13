import axios from "axios";
import { mergeParameters } from "../../utils.ts";
import { Conversation } from "../api/conversation/Conversation.ts";
import { ConversationCriteria } from "../api/conversation/ConversationCriteria.ts";
import { ConversationDetails } from "../api/conversation/ConversationDetails.ts";
import { ConversationSummary } from "../api/conversation/ConversationSummary.ts";
import { Page } from "../api/page/Page.ts";
import { PageRequest } from "../api/page/PageRequest.ts";
import { Patch } from "../api/patch/Patch.ts";

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
  const params = mergeParameters(pageRequestParams);
  return await axios
    .get<Page<ConversationSummary>>(`${conversationsUrl}/summaries${params}`)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const fetchConversations = async (
  criteria: ConversationCriteria,
  pageRequest: PageRequest,
): Promise<Page<ConversationDetails>> => {
  const criteriaParams = criteria.getUrl();
  const pageRequestParams = pageRequest.getUrl();
  const params = mergeParameters(criteriaParams, pageRequestParams);
  return await axios
    .get<Page<ConversationDetails>>(`${conversationsUrl}${params}`)
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
