import { ActionIcon, Flex, Tooltip } from "@mantine/core";
import { IconEye } from "@tabler/icons-react";
import {
  MantineReactTable,
  MRT_ColumnDef,
  useMantineReactTable,
} from "mantine-react-table";
import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { useTableFilters } from "../../hooks/table/useTableFilters.ts";
import { useTableState } from "../../hooks/table/useTableState.ts";
import { ConversationDetails } from "../../model/api/conversation/ConversationDetails.ts";
import { Page } from "../../model/api/page/Page.ts";
import { User } from "../../model/api/user/User.ts";
import {
  createPageRequest,
  fetchConversations,
} from "../../model/service/conversationService.ts";
import {
  defaultMantineTableContainerProps,
  showErrorMessage,
} from "../../utils.ts";
import HighlightedText from "../components/table/HiglightedText.tsx";
import { EmptyPage } from "../../model/api/page/EmptyPage.ts";

export default function ConversationsPage() {
  const tableState = useTableState("conversations");
  const tableFilters = useTableFilters();
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState<Page<ConversationDetails>>(EmptyPage.of);
  const [pageRequest, setPageRequest] = useState(
    createPageRequest(tableState, tableFilters),
  );

  useEffect(() => {
    const newPageRequest = createPageRequest(tableState, tableFilters);
    if (pageRequest.getUrlParams() != newPageRequest.getUrlParams()) {
      setPageRequest(newPageRequest);
    }
  }, [
    tableState.pagination,
    tableState.sorting,
    tableFilters.columnFilters,
    tableFilters.globalFilter,
  ]);

  useEffect(() => {
    setLoading(true);
    fetchConversations(pageRequest)
      .then((response) => setPage(response))
      .catch(() => {
        showErrorMessage("Failed to fetch conversations");
      })
      .finally(() => setLoading(false));
  }, [pageRequest]);

  const columns = useMemo<MRT_ColumnDef<ConversationDetails>[]>(
    () => [
      {
        accessorKey: "summary",
        header: "Summary",
      },
      {
        accessorKey: "user",
        header: "User",
        Cell: ({ cell }) => (
          <HighlightedText
            text={`${cell.getValue<User>().lastname} ${cell.getValue<User>().firstname}`}
            phrase={
              tableFilters.globalFilter
                ? tableFilters.globalFilter
                : (tableFilters.columnFilters.find((e) => e.id == "user")
                    ?.value as string)
            }
          />
        ),
        size: 180,
        grow: false,
      },
      {
        accessorKey: "messageCount",
        header: "Message count",
        filterVariant: "range",
        filterFn: "betweenInclusive",
        size: 120,
        grow: false,
      },
      {
        accessorKey: "deleted",
        header: "Deleted",
        accessorFn: (row) => (row.deleted ? "Yes" : "No"),
        filterVariant: "select",
        mantineFilterSelectProps: {
          data: [
            { label: "Yes", value: "true" },
            { label: "No", value: "false" },
          ],
        },
        size: 120,
        grow: false,
      },
      {
        accessorKey: "createdAt",
        header: "Started",
        accessorFn: (row) => new Date(row.createdAt),
        filterVariant: "date-range",
        Cell: ({ cell }) =>
          `${cell.getValue<Date>().toLocaleDateString()} ${cell
            .getValue<Date>()
            .toLocaleTimeString()}`,
        size: 200,
        grow: false,
      },
    ],
    [page],
  );

  const table = useMantineReactTable({
    columns: columns,
    data: page.items,
    enableEditing: false,
    enableRowActions: true,
    positionActionsColumn: "last",
    onColumnVisibilityChange: tableState.setColumnVisibility,
    onDensityChange: tableState.setRowDensity,
    manualPagination: true,
    onPaginationChange: tableState.setPagination,
    rowCount: page.totalElements,
    manualSorting: true,
    onSortingChange: tableState.setSorting,
    manualFiltering: true,
    onColumnFiltersChange: tableFilters.setColumnFilters,
    onGlobalFilterChange: tableFilters.setGlobalFilter,
    state: {
      isLoading: loading,
      columnVisibility: tableState.columnVisibility,
      density: tableState.rowDensity,
      pagination: tableState.pagination,
      sorting: tableState.sorting,
      columnFilters: tableFilters.columnFilters,
      globalFilter: tableFilters.globalFilter,
    },
    enableStickyHeader: true,
    enableStickyFooter: true,
    mantineTableContainerProps: defaultMantineTableContainerProps,
    renderRowActions: ({ row }) => (
      <Flex gap="md">
        <Tooltip label="Preview">
          <Link to={`/conversations/${page.items[row.index].id}?preview=1`}>
            <ActionIcon>
              <IconEye />
            </ActionIcon>
          </Link>
        </Tooltip>
      </Flex>
    ),
  });

  return <MantineReactTable table={table} />;
}
