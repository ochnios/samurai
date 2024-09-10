import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./reducers/authSlice.ts";
import conversationsReducer from "./reducers/conversationsSlice.ts";

const store = configureStore({
  reducer: {
    conversations: conversationsReducer,
    auth: authReducer,
  },
});

export default store;
export type AppStore = typeof store;
export type RootState = ReturnType<AppStore["getState"]>;
export type AppDispatch = AppStore["dispatch"];
