import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import axios from "axios";
import { validate } from "uuid";

interface Assistant {
  id: string;
  name: string;
}

interface AssistantState {
  currentId?: string;
  available: Assistant[];
  loading: boolean;
  errors?: string;
}

const initialState: AssistantState = {
  currentId: undefined,
  available: [] as Assistant[],
  loading: false,
  errors: undefined,
};

export const fetchAvailableAssistants = createAsyncThunk(
  "fetchAvailableAssistants",
  async () => {
    return axios.get("/assistants/available").then((response) => response.data);
  },
);

const assistantSlice = createSlice({
  name: "assistant",
  initialState: initialState,
  reducers: {
    setActiveAssistant: (state, action) => {
      state.currentId = validate(action.payload) ? action.payload : undefined;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchAvailableAssistants.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchAvailableAssistants.fulfilled, (state, action) => {
        state.loading = false;
        state.available = action.payload;
        state.currentId =
          state.currentId ?? state.available
            ? state.available[0].id
            : undefined;
        state.errors = undefined;
      })
      .addCase(fetchAvailableAssistants.rejected, (state, action) => {
        state.loading = false;
        state.available = [] as Assistant[];
        state.errors = action.payload as string;
      });
  },
});

export const { setActiveAssistant } = assistantSlice.actions;

export default assistantSlice.reducer;
