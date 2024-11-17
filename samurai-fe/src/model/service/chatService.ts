import axios from "axios";
import { ChatRequest } from "../api/chat/ChatRequest.ts";
import { ChatResponse } from "../api/chat/ChatResponse.ts";

const chatUrl = "/chat";

export const sendMessage = async (
  chatRequest: ChatRequest,
): Promise<ChatResponse> => {
  return await axios
    .post<ChatResponse>(chatUrl, chatRequest)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const validateChatMessage = (message?: string) => {
  return message && message.trim().length >= 3;
};
