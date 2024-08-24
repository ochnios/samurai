import { configureStore } from "@reduxjs/toolkit";
import conversationsReducer from "./reducers/conversationsSlice.ts";

const store = configureStore({
  reducer: {
    conversations: conversationsReducer,
  },
});

export default store;
export type AppStore = typeof store;
export type RootState = ReturnType<AppStore["getState"]>;
export type AppDispatch = AppStore["dispatch"];
