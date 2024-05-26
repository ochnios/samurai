import { configureStore } from "@reduxjs/toolkit";
import assistantReducer from "./reducers/assistantSlice.ts";
import conversationsReducer from "./reducers/conversationsSlice.ts";

const store = configureStore({
  reducer: {
    assistant: assistantReducer,
    conversations: conversationsReducer,
  },
});

export default store;
export type AppStore = typeof store;
export type RootState = ReturnType<AppStore["getState"]>;
export type AppDispatch = AppStore["dispatch"];
