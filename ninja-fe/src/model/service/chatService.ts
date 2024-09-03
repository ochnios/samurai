import axios from "axios";
import { ChatRequest } from "../api/ChatRequest.ts";
import { ChatResponse } from "../api/ChatResponse.ts";

const chatUrl = "/chat";

export const sendMessage = async (
  chatRequest: ChatRequest,
): Promise<ChatResponse | void> => {
  return await axios
    .post<ChatResponse>(chatUrl, chatRequest)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};
