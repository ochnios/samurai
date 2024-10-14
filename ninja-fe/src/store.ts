import { configureStore } from "@reduxjs/toolkit";
import authReducer, {
  initialState as authInitialState,
} from "./reducers/authSlice.ts";
import conversationsReducer, {
  initialState as conversationsInitialState,
} from "./reducers/conversationsSlice.ts";

const reduxState = JSON.parse(localStorage.getItem("redux_state") || "null");
const authState = reduxState?.auth
  ? { ...reduxState.auth, errors: undefined }
  : authInitialState;

const preloadedState = {
  auth: authState,
  conversations: conversationsInitialState,
};

const store = configureStore({
  reducer: {
    conversations: conversationsReducer,
    auth: authReducer,
  },
  preloadedState: preloadedState,
});

store.subscribe(() => {
  const { auth } = store.getState();
  localStorage.setItem("redux_state", JSON.stringify({ auth }));
});

export default store;
export type AppStore = typeof store;
export type RootState = ReturnType<AppStore["getState"]>;
export type AppDispatch = AppStore["dispatch"];
