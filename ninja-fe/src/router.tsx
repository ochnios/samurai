import { createBrowserRouter, Navigate } from "react-router-dom";
import TestPage from "./views/pages/TestPage.tsx";
import { Layout } from "./views/layout/Layout.tsx";

export default function router() {
  return createBrowserRouter([
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
}
