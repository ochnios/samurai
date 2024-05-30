import axios from "axios";
import { Conversation } from "../api/Conversation.ts";

export const fetchConversation = async (
  assistantId: string,
  conversationId: string,
): Promise<Conversation | void> => {
  return await axios
    .get<Conversation>(
      `/assistants/${assistantId}/conversations/${conversationId}`,
    )
    .then((response) => {
      return response.data;
    })
    .catch((error) => {
      console.error(error);
    });
};
