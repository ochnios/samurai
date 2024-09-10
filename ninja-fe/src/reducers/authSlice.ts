import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { Login } from "../model/api/Login.ts";
import { User } from "../model/api/User.ts";
import { loginCall } from "../model/service/authService.ts";

interface AuthState {
  authenticated: boolean;
  user?: User;
  loading: boolean;
  errors?: string;
}

export const initialState: AuthState = {
  authenticated: false,
  user: undefined,
  loading: false,
  errors: undefined,
};

export const authenticate = createAsyncThunk("login", async (login: Login) =>
  loginCall(login),
);

const authSlice = createSlice({
  name: "auth",
  initialState: initialState,
  reducers: {
    unauthenticate: () => {
      return { ...initialState, authenticated: false };
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(authenticate.pending, (state) => {
        state.loading = true;
      })
      .addCase(authenticate.fulfilled, (state, action) => {
        state.loading = false;
        state.authenticated = true;
        state.user = action.payload!;
        state.errors = undefined;
      })
      .addCase(authenticate.rejected, (state, action) => {
        state.loading = false;
        state.errors = (action.payload as string) ?? "login: rejected";
      });
  },
});

export const { unauthenticate } = authSlice.actions;

export default authSlice.reducer;
