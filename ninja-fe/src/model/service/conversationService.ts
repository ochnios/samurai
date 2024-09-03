import axios from "axios";
import { Conversation } from "../api/Conversation.ts";
import { ConversationSummary } from "../api/ConversationSummary.ts";
import { Page } from "../api/Page.ts";
import { PageRequest } from "../api/PageRequest.ts";

const conversationsUrl = "/conversations";

export const fetchConversation = async (
  conversationId: string,
): Promise<Conversation | void> => {
  return await axios
    .get<Conversation>(`${conversationsUrl}/${conversationId}`)
    .then((response) => response.data)
    .catch((error) => console.error(error));
};

export const fetchConversationsSummaries = async (
  pageRequest: PageRequest,
): Promise<Page<ConversationSummary> | void> => {
  const pageRequestParams = pageRequest.getUrl();
  console.log(pageRequestParams);
  return await axios
    .get<Page<ConversationSummary>>(`${conversationsUrl}?${pageRequestParams}`)
    .then((response) => response.data)
    .catch((error) => console.error(error));
};
