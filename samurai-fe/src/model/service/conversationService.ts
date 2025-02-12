import axios from "axios";
import { TableFilters } from "../../hooks/table/useTableFilters.ts";
import { TableState } from "../../hooks/table/useTableState.ts";
import { normalizePostfix } from "../../utils.ts";
import { Conversation } from "../api/conversation/Conversation.ts";
import { ConversationCriteria } from "../api/conversation/ConversationCriteria.ts";
import { ConversationCriteriaImpl } from "../api/conversation/ConversationCriteriaImpl.ts";
import { ConversationDetails } from "../api/conversation/ConversationDetails.ts";
import { ConversationSummary } from "../api/conversation/ConversationSummary.ts";
import { EmptyCriteria } from "../api/page/EmptyCriteria.ts";
import { Page } from "../api/page/Page.ts";
import { PageRequest } from "../api/page/PageRequest.ts";
import { PageRequestImpl } from "../api/page/PageRequestImpl.ts";
import { JsonPatch } from "../api/patch/JsonPatch.ts";
import { processUserSorting } from "./sortService.ts";

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
  pageRequest: PageRequest<EmptyCriteria>,
): Promise<Page<ConversationSummary>> => {
  const postfix = normalizePostfix(pageRequest.getUrlParams());
  return await axios
    .get<Page<ConversationSummary>>(`${conversationsUrl}/summaries${postfix}`)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const fetchConversations = async (
  pageRequest: PageRequest<ConversationCriteria>,
): Promise<Page<ConversationDetails>> => {
  const postfix = normalizePostfix(pageRequest.getUrlParams());
  return await axios
    .get<Page<ConversationDetails>>(`${conversationsUrl}${postfix}`)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const patchConversation = async (
  conversationId: string,
  jsonPatch: JsonPatch,
): Promise<Conversation> => {
  return await axios
    .patch<Conversation>(
      `${conversationsUrl}/${conversationId}`,
      jsonPatch.nodes,
    )
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

export const createPageRequest = (
  tableState: TableState,
  tableFilters: TableFilters,
): PageRequest<ConversationCriteria> => {
  return PageRequestImpl.of(
    ConversationCriteriaImpl.of(
      tableFilters.globalFilter,
      tableFilters.columnFilters,
    ),
    tableState.pagination,
    processUserSorting(tableState.sorting),
  );
};
