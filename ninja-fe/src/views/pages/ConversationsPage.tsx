import { ActionIcon, Box, Flex, Tooltip } from "@mantine/core";
import { IconEye } from "@tabler/icons-react";
import {
  MantineReactTable,
  MRT_ColumnDef,
  MRT_ColumnFiltersState,
  MRT_PaginationState,
  MRT_SortingState,
  useMantineReactTable,
} from "mantine-react-table";
import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";

import { ConversationCriteriaImpl } from "../../model/api/conversation/ConversationCriteriaImpl.ts";
import { ConversationDetails } from "../../model/api/conversation/ConversationDetails.ts";
import { Page } from "../../model/api/page/Page.ts";

import { PageRequestImpl } from "../../model/api/page/PageRequestImpl.ts";
import { User } from "../../model/api/user/User.ts";
import { fetchConversations } from "../../model/service/conversationService.ts";
import { showErrorMessage } from "../../utils.ts";
import HighlightedText from "../components/table/HiglightedText.tsx";

export default function ConversationsPage() {
  const [columnFilters, setColumnFilters] = useState<MRT_ColumnFiltersState>(
    [],
  );
  const [globalFilter, setGlobalFilter] = useState("");
  const [pagination, setPagination] = useState<MRT_PaginationState>({
    pageIndex: 0,
    pageSize: 5,
  });
  const [sorting, setSorting] = useState<MRT_SortingState>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState<Page<ConversationDetails>>();

  useEffect(() => {
    setLoading(true);

    const processedSorting = sorting.flatMap((s) =>
      s.id == "user"
        ? [
            { id: "user.lastname", desc: s.desc },
            { id: "user.firstname", desc: s.desc },
          ]
        : [s],
    );

    console.log(globalFilter);
    const criteria = ConversationCriteriaImpl.of(globalFilter, columnFilters);
    const pageRequest = PageRequestImpl.of(pagination, processedSorting);

    fetchConversations(criteria, pageRequest)
      .then((response) => setPage(response))
      .catch(() => {
        showErrorMessage("Failed to fetch conversations");
      })
      .finally(() => setLoading(false));
  }, [columnFilters, globalFilter, pagination, sorting]);

  const columns = useMemo<MRT_ColumnDef<ConversationDetails>[]>(
    () => [
      {
        accessorKey: "summary",
        header: "Summary",
        enableEditing: false,
      },
      {
        accessorKey: "user",
        header: "User",
        enableEditing: false,
        Cell: ({ cell }) => (
          <HighlightedText
            text={`${cell.getValue<User>().lastname} ${cell.getValue<User>().firstname}`}
            phrase={
              globalFilter
                ? globalFilter
                : (columnFilters.find((e) => e.id == "user")?.value as string)
            }
          />
        ),
      },
      {
        accessorKey: "messageCount",
        header: "Message count",
        enableEditing: false,
        filterVariant: "range",
        filterFn: "betweenInclusive",
      },
      {
        accessorKey: "deleted",
        header: "Deleted",
        enableEditing: false,
        accessorFn: (row) => (row.deleted ? "Yes" : "No"),
        filterVariant: "select",
        mantineFilterSelectProps: {
          data: [
            { label: "Yes", value: "true" },
            { label: "No", value: "false" },
          ],
        },
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
    [page],
  );

  const table = useMantineReactTable({
    columns: columns,
    data: page?.items ?? [],
    editDisplayMode: "row",
    enableEditing: true,
    renderRowActions: ({ row }) => (
      <Flex gap="md">
        <Tooltip label="Preview">
          <Link to={`/conversations/${page?.items[row.index].id}?preview=1`}>
            <ActionIcon>
              <IconEye />
            </ActionIcon>
          </Link>
        </Tooltip>
      </Flex>
    ),
    manualFiltering: true,
    onColumnFiltersChange: setColumnFilters,
    onGlobalFilterChange: setGlobalFilter,
    manualPagination: true,
    onPaginationChange: setPagination,
    rowCount: page?.totalElements,
    manualSorting: true,
    onSortingChange: setSorting,
    state: {
      isLoading: loading,
      columnFilters: columnFilters,
      globalFilter: globalFilter,
      pagination: pagination,
      sorting: sorting,
    },
  });

  return (
    <Box>
      <MantineReactTable table={table} />
    </Box>
  );
}
