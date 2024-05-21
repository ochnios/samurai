import { createBrowserRouter, Navigate } from "react-router-dom";
import PageWrapper from "./views/pages/PageWrapper.tsx";
import { Layout } from "./views/layout/Layout.tsx";
import ChatPage from "./views/pages/ChatPage.tsx";
import TestPage from "./views/pages/TestPage.tsx";

export default function router() {
  return createBrowserRouter([
    {
      path: "/",
      element: <Layout />,
      children: [
        {
          index: true,
          element: (
            <PageWrapper title="Default content" content={<TestPage />} />
          ),
        },
        {
          path: "assistants",
          element: <PageWrapper title="Assistants" content={<TestPage />} />,
        },
        {
          path: "documents",
          element: <PageWrapper title="Documents" content={<TestPage />} />,
        },
        {
          path: "statistics",
          element: <PageWrapper title="Statistics" content={<TestPage />} />,
        },
        {
          path: "models",
          element: <PageWrapper title="Models" content={<TestPage />} />,
        },
        {
          path: "users",
          element: <PageWrapper title="Users" content={<TestPage />} />,
        },
        {
          path: "chat",
          element: <PageWrapper title="Chat" content={<ChatPage />} />,
        },
        {
          path: "account",
          element: <PageWrapper title="Account" content={<TestPage />} />,
        },
        {
          path: "*",
          element: <Navigate to="/" />,
        },
      ],
    },
  ]);
}
