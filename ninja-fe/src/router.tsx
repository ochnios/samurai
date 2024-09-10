import { createBrowserRouter, Navigate } from "react-router-dom";
import { Layout } from "./views/layout/Layout.tsx";
import ChatPage from "./views/pages/ChatPage.tsx";
import DummyPage from "./views/pages/DummyPage.tsx";
import LoginPage from "./views/pages/LoginPage.tsx";
import PageWrapper from "./views/pages/PageWrapper.tsx";

const router = createBrowserRouter([
  { path: "/login", element: <LoginPage /> },
  {
    path: "/",
    element: <Layout />,
    children: [
      {
        index: true,
        element: (
          <PageWrapper title="Default content" content={<DummyPage />} />
        ),
      },
      {
        path: "assistants",
        element: <PageWrapper title="Assistants" content={<DummyPage />} />,
      },
      {
        path: "documents",
        element: <PageWrapper title="Documents" content={<DummyPage />} />,
      },
      {
        path: "statistics",
        element: <PageWrapper title="Statistics" content={<DummyPage />} />,
      },
      {
        path: "models",
        element: <PageWrapper title="Models" content={<DummyPage />} />,
      },
      {
        path: "users",
        element: <PageWrapper title="Users" content={<DummyPage />} />,
      },
      {
        path: "conversations/:id",
        element: <PageWrapper title="Chat" content={<ChatPage />} />,
      },
      {
        path: "account",
        element: <PageWrapper title="Account" content={<DummyPage />} />,
      },
      {
        path: "*",
        element: <Navigate to="/" />,
      },
    ],
  },
]);

export default router;
