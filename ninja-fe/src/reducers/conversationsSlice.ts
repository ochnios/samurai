import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";

import axios from "axios";

interface ConversationSummary {
  id: string;
  summary: string;
}

interface ConversationsState {
  currentId?: string;
  conversations: ConversationSummary[];
  loading: boolean;
  errors: string;
}

const initialState: ConversationsState = {
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
    setCurrent: (state, action) => {
      state.currentId = action.payload;
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
        state.errors = action.payload as string;
      });
  },
});

export const { setCurrent } = conversationsSlice.actions;
export default conversationsSlice.reducer;
