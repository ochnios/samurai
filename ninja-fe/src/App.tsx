import "@mantine/core/styles.css";
import { MantineProvider } from "@mantine/core";
import { theme } from "./theme";
import { RouterProvider } from "react-router-dom";
import router from "./router.tsx";

export default function App() {
  return (
    <MantineProvider theme={theme}>
      <RouterProvider router={router()} />
    </MantineProvider>
  );
}
