import { createBrowserRouter, Navigate } from "react-router-dom";
import { Role } from "./model/api/user/Role.ts";
import { Layout } from "./views/layout/Layout.tsx";
import ChatPage from "./views/pages/ChatPage.tsx";
import ConversationsPage from "./views/pages/ConversationsPage.tsx";
import DocumentsPage from "./views/pages/DocumentsPage.tsx";
import DummyPage from "./views/pages/DummyPage.tsx";
import LoginPage from "./views/pages/LoginPage.tsx";
import PageWrapper from "./views/pages/PageWrapper.tsx";
import ChunksPage from "./views/pages/ChunksPage.tsx";

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
        path: "users",
        element: (
          <PageWrapper
            title="Users"
            content={<DummyPage />}
            access={Role.Mod}
          />
        ),
      },
      {
        path: "statistics",
        element: (
          <PageWrapper
            title="Statistics"
            content={<DummyPage />}
            access={Role.Mod}
          />
        ),
      },
      {
        path: "conversations/all",
        element: (
          <PageWrapper
            title="Conversations"
            content={<ConversationsPage />}
            access={Role.Mod}
          />
        ),
      },
      {
        path: "documents",
        element: (
          <PageWrapper
            title="Documents"
            content={<DocumentsPage />}
            access={Role.Mod}
          />
        ),
      },
      {
        path: "documents/:documentId/chunks",
        element: (
          <PageWrapper
            title="Chunks"
            content={<ChunksPage />}
            access={Role.Mod}
          />
        ),
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
