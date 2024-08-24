import axios from "axios";
import { Conversation } from "../api/Conversation.ts";

const conversationsUrl = "/conversations";

export const fetchConversation = async (
  conversationId: string,
): Promise<Conversation | void> => {
  return await axios
    .get<Conversation>(`${conversationsUrl}/${conversationId}`)
    .then((response) => response.data as Conversation)
    .catch((error) => console.error(error));
};
