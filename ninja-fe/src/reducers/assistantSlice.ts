import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import axios from "axios";

interface Assistant {
  id: string;
  name: string;
}

interface AssistantState {
  current?: Assistant;
  available?: Assistant[];
  loading: boolean;
  errors?: string;
}

const initialState: AssistantState = {
  current: undefined,
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
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchAvailableAssistants.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchAvailableAssistants.fulfilled, (state, action) => {
        state.loading = false;
        state.available = action.payload;
        // TODO selecting from GUI
        state.current = state.available ? state.available[0] : undefined;
        state.errors = undefined;
      })
      .addCase(fetchAvailableAssistants.rejected, (state, action) => {
        state.loading = false;
        state.available = undefined;
        state.errors = action.payload as string;
      });
  },
});

export default assistantSlice.reducer;
