import { createBrowserRouter, Navigate } from "react-router-dom";
import Layout from "./views/layout/Layout.tsx";
import TestPage from "./views/pages/TestPage.tsx";

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
