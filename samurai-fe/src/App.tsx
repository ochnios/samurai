import "@mantine/core/styles.css";
import "@mantine/dates/styles.css";
import "@mantine/notifications/styles.css";
import "mantine-react-table/styles.css";
import "./custom.css";
import { MantineProvider } from "@mantine/core";
import { Notifications } from "@mantine/notifications";
import axios from "axios";
import { Provider } from "react-redux";
import { RouterProvider } from "react-router-dom";
import config from "./config.ts";
import { unauthenticate } from "./reducers/authSlice.ts";
import router from "./router.tsx";
import store from "./store.ts";
import { theme } from "./theme";
import { ModalsProvider } from "@mantine/modals";

axios.defaults.baseURL = config.baseUrl;
axios.defaults.withCredentials = true;
axios.defaults.headers.common["Content-Type"] = "application/json";
axios.defaults.headers.common["Accept"] = "application/json";

axios.interceptors.response.use(
  function (response) {
    return response;
  },
  function (error) {
    if (error.response?.status === 401) {
      store.dispatch(unauthenticate());
      console.error("401 Unauthorized:", error);
    }
    return Promise.reject(error.response?.data);
  },
);

axios.interceptors.request.use(
  (config) => {
    if (config.method?.toLowerCase() === "patch") {
      config.headers["Content-Type"] = "application/json-patch+json";
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

export default function App() {
  return (
    <Provider store={store}>
      <MantineProvider theme={theme}>
        <Notifications position="top-right" autoClose={10000} />
        <ModalsProvider>
          <RouterProvider router={router} />
        </ModalsProvider>
      </MantineProvider>
    </Provider>
  );
}
