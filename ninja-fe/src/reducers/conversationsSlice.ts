import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";

import axios from "axios";
import { ConversationSummary } from "../model/api/ConversationSummary.ts";

interface ConversationsState {
  currentId?: string;
  conversations: ConversationSummary[];
  loading: boolean;
  errors: string;
}

const initialState: ConversationsState = {
  currentId: undefined,
  conversations: [] as ConversationSummary[],
  loading: false,
  errors: "",
};

export const fetchConversations = createAsyncThunk(
  "fetchConversations",
  async (assistantId: string) => {
    return axios
      .get(`/assistants/${assistantId}/conversations`)
      .then((response) => response.data);
  },
);

const conversationsSlice = createSlice({
  name: "conversations",
  initialState,
  reducers: {
    setActiveConversation: (state, action) => {
      state.currentId = action.payload;
    },
    addConversationSummary: (state, action) => {
      const summary = {
        id: action.payload.id,
        summary: action.payload.summary,
      } as ConversationSummary;
      state.conversations = [summary, ...state.conversations];
    },
    renameConversation: (state, action) => {
      state.conversations = state.conversations.map((conversation) =>
        conversation.id === action.payload.id
          ? { ...conversation, summary: action.payload.newSummary }
          : conversation,
      );
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchConversations.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchConversations.fulfilled, (state, action) => {
        state.loading = false;
        state.conversations = action.payload;
      })
      .addCase(fetchConversations.rejected, (state, action) => {
        state.loading = false;
        state.errors =
          (action.payload as string) ?? "fetchConversations: rejected";
      });
  },
});

export const {
  setActiveConversation,
  addConversationSummary,
  renameConversation,
} = conversationsSlice.actions;

export default conversationsSlice.reducer;
