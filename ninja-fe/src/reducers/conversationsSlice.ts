import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { ConversationSummary } from "../model/api/ConversationSummary.ts";
import { PageRequestImpl, SortDir } from "../model/api/PageRequest.ts";
import { fetchConversationsSummaries } from "../model/service/conversationService.ts";

interface ConversationsState {
  currentId?: string;
  conversations: ConversationSummary[];
  loading: boolean;
  errors?: string;
}

const initialState: ConversationsState = {
  currentId: undefined,
  conversations: [] as ConversationSummary[],
  loading: false,
  errors: undefined,
};

export const fetchConversations = createAsyncThunk(
  "fetchConversations",
  async () => {
    // TODO Add "Load more" button in the future
    const pageRequest = new PageRequestImpl(0, 100, "createdAt", SortDir.DESC);
    return fetchConversationsSummaries(pageRequest);
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
      const summary: ConversationSummary = {
        id: action.payload.id,
        summary: action.payload.summary,
      };
      state.conversations = [summary, ...state.conversations];
    },
    renameConversation: (state, action) => {
      state.conversations = state.conversations.map((conversation) =>
        conversation.id === action.payload.id
          ? { ...conversation, summary: action.payload.newSummary }
          : conversation,
      );
    },
    resetConversations: () => {
      return initialState;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchConversations.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchConversations.fulfilled, (state, action) => {
        state.loading = false;
        state.conversations = action.payload ? action.payload.items : [];
        state.errors = undefined;
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
  resetConversations,
} = conversationsSlice.actions;

export default conversationsSlice.reducer;
