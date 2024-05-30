import axios from "axios";
import { ChatRequest } from "../api/ChatRequest.ts";
import { ChatResponse } from "../api/ChatResponse.ts";

export const sendMessage = async (
  assistantId: string,
  chatRequest: ChatRequest,
): Promise<ChatResponse | void> => {
  return await axios
    .post<ChatResponse>(`/assistants/${assistantId}/chat`, chatRequest)
    .then((response) => {
      return response.data;
    })
    .catch((error) => {
      console.error(error);
    });
};
