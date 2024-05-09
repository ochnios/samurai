import "@mantine/core/styles.css";
import { MantineProvider } from "@mantine/core";
import { theme } from "./theme";
import {
  createBrowserRouter,
  Navigate,
  RouterProvider,
} from "react-router-dom";
import Layout from "./views/layout/Layout.tsx";
import TestPage from "./views/pages/TestPage.tsx";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      { index: true, element: <TestPage title="Default page" /> },
      {
        path: "assistants",
        element: <TestPage title="Assistants" />,
      },
      {
        path: "documents",
        element: <TestPage title="Documents" />,
      },
      {
        path: "statistics",
        element: <TestPage title="Statistics" />,
      },
      {
        path: "models",
        element: <TestPage title="Models" />,
      },
      {
        path: "users",
        element: <TestPage title="Users" />,
      },
      {
        path: "account",
        element: <TestPage title="Account" />,
      },
      {
        path: "*",
        element: <Navigate to="/" />,
      },
    ],
  },
]);

export default function App() {
  return (
    <MantineProvider theme={theme}>
      <RouterProvider router={router} />
    </MantineProvider>
  );
}
