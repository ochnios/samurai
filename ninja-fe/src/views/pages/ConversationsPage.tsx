import { ActionIcon, Box, Flex, Tooltip } from "@mantine/core";
import { IconEye } from "@tabler/icons-react";
import {
  MantineReactTable,
  MRT_ColumnDef,
  useMantineReactTable,
} from "mantine-react-table";
import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import {
  ConversationCriteriaImpl,
  ConversationDetails,
} from "../../model/api/Conversation.ts";
import { Page } from "../../model/api/Page.ts";
import { PageRequestImpl } from "../../model/api/PageRequest.ts";
import { User } from "../../model/api/User.ts";
import { fetchConversations } from "../../model/service/conversationService.ts";
import { showErrorMessage } from "../../utils.ts";

export default function ConversationsPage() {
  const [loading, setLoading] = useState(false);
  const [conversations, setConversations] =
    useState<Page<ConversationDetails>>();

  useEffect(() => {
    setLoading(true);
    const criteria = new ConversationCriteriaImpl({});
    const pageRequest = new PageRequestImpl();
    fetchConversations(criteria, pageRequest)
      .then((response) => setConversations(response))
      .catch(() => {
        showErrorMessage("Failed to fetch conversations");
      })
      .finally(() => setLoading(false));
  }, []);

  const columns = useMemo<MRT_ColumnDef<ConversationDetails>[]>(
    () => [
      {
        accessorKey: "deleted",
        header: "Deleted",
        enableEditing: false,
        accessorFn: (row) => (row.deleted ? "Yes" : "No"),
        filterVariant: "select",
        mantineFilterSelectProps: {
          data: ["Yes", "No"],
        },
      },
      {
        accessorKey: "summary",
        header: "Summary",
        enableEditing: false,
      },
      {
        accessorKey: "messageCount",
        header: "Message count",
        enableEditing: false,
        filterVariant: "range",
        filterFn: "betweenInclusive",
      },
      {
        accessorKey: "user",
        header: "User",
        enableEditing: false,
        filterVariant: "multi-select",
        mantineFilterSelectProps: {
          data: [
            ...new Set(
              conversations?.items.map(
                (c) => `${c.user.firstname} ${c.user.lastname}`,
              ),
            ),
          ],
        },
        Cell: ({ cell }) =>
          `${cell.getValue<User>().firstname} ${cell.getValue<User>().lastname}`,
      },
      {
        accessorKey: "createdAt",
        header: "Started",
        enableEditing: false,
        accessorFn: (row) => new Date(row.createdAt),
        filterVariant: "date-range",
        Cell: ({ cell }) =>
          `${cell.getValue<Date>().toLocaleDateString()} ${cell
            .getValue<Date>()
            .toLocaleTimeString()}`,
      },
    ],
    [conversations],
  );

  const table = useMantineReactTable({
    columns: columns,
    data: conversations?.items ?? [],
    editDisplayMode: "row",
    enableEditing: true,
    enableMultiSort: true,
    renderRowActions: ({ row }) => (
      <Flex gap="md">
        <Tooltip label="Preview">
          <Link
            to={`/conversations/${conversations?.items[row.index].id}?preview=1`}
          >
            <ActionIcon>
              <IconEye />
            </ActionIcon>
          </Link>
        </Tooltip>
      </Flex>
    ),
    state: {
      isLoading: loading,
    },
  });

  return (
    <Box>
      <MantineReactTable table={table} />
    </Box>
  );
}
