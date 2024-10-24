import { Box, Menu, Text } from "@mantine/core";
import {
  IconArchive,
  IconDownload,
  IconFileStack,
  IconRestore,
  IconTrash,
} from "@tabler/icons-react";
import {
  MantineReactTable,
  MRT_ColumnDef,
  useMantineReactTable,
} from "mantine-react-table";
import { useEffect, useMemo, useState } from "react";
import config from "../../config.ts";
import { useTableFilters } from "../../hooks/table/useTableFilters.ts";
import { useTableState } from "../../hooks/table/useTableState.ts";
import { DocumentStatus } from "../../model/api/document/DocumentStatus.ts";
import { Page } from "../../model/api/page/Page.ts";
import { User } from "../../model/api/user/User.ts";
import {
  createPageRequest,
  fetchDocuments,
  formatFileSize,
} from "../../model/service/documentService.ts";
import { showErrorMessage } from "../../utils.ts";
import DocumentStatusBadge from "../components/document/DocumentStatusBadge.tsx";
import HighlightedText from "../components/table/HiglightedText.tsx";
import { Document } from "../../model/api/document/Document.ts";

export default function DocumentsPage() {
  const tableState = useTableState("documents");
  const tableFilters = useTableFilters();
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState<Page<Document>>();
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
    fetchDocuments(pageRequest)
      .then((response) => setPage(response))
      .catch(() => {
        showErrorMessage("Failed to fetch documents");
      })
      .finally(() => setLoading(false));
  }, [pageRequest]);

  const columns = useMemo<MRT_ColumnDef<Document>[]>(
    () => [
      {
        accessorKey: "title",
        header: "Title",
      },
      {
        accessorKey: "name",
        header: "Filename",
        enableEditing: false,
      },
      {
        accessorKey: "user",
        header: "Uploaded by",
        enableEditing: false,
        Cell: ({ cell }) => (
          <HighlightedText
            text={`${cell.getValue<User>().lastname} ${cell.getValue<User>().firstname}`}
            phrase={
              tableFilters.globalFilter
                ? tableFilters.globalFilter
                : (tableFilters.columnFilters.find((e) => e.id == "user")
                    ?.value as string)
            }
            fontSize="xs"
          />
        ),
      },
      {
        accessorKey: "size",
        header: "Size",
        enableEditing: false,
        filterVariant: "range-slider",
        filterFn: "betweenInclusive",
        Cell: ({ cell }) => formatFileSize(cell.getValue<number>()),
        mantineFilterRangeSliderProps: {
          max: 52_428_800, // 50MB
          min: 0,
          step: 1000,
          label: (value) => `${value}B`,
        },
      },
      {
        accessorKey: "status",
        header: "Status",
        enableEditing: false,
        filterVariant: "select",
        mantineFilterSelectProps: {
          data: Object.values(DocumentStatus),
        },
        Cell: ({ cell }) => (
          <DocumentStatusBadge status={cell.getValue<DocumentStatus>()} />
        ),
      },
      {
        accessorKey: "createdAt",
        header: "Uploaded at",
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
    positionActionsColumn: "last",
    onColumnVisibilityChange: tableState.setColumnVisibility,
    onDensityChange: tableState.setRowDensity,
    manualPagination: true,
    onPaginationChange: tableState.setPagination,
    rowCount: page?.totalElements,
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
    renderRowActionMenuItems: ({ row }) => {
      const apiUrl = config.baseUrl;
      const document = page?.items[row.index];
      return (
        <>
          <Menu.Item leftSection={<IconFileStack />}>View chunks</Menu.Item>
          <Menu.Item
            leftSection={<IconDownload />}
            component="a"
            href={`${apiUrl}/documents/${document?.id}/download`}
            target="_blank"
          >
            Download
          </Menu.Item>
          {document?.status === DocumentStatus.ACTIVE && (
            <Menu.Item color="yellow" leftSection={<IconArchive />}>
              Archive
            </Menu.Item>
          )}
          {document?.status === DocumentStatus.ARCHIVED && (
            <Menu.Item color="green" leftSection={<IconRestore />}>
              Restore
            </Menu.Item>
          )}
          <Menu.Item color="red" leftSection={<IconTrash />}>
            Delete
          </Menu.Item>
        </>
      );
    },
    renderDetailPanel: ({ row }) => (
      <Box>
        <Text fz="sm">
          <Text fz="sm" fw="bold" span>
            Description:{" "}
          </Text>
          <HighlightedText
            text={page?.items[row.index].description}
            phrase={
              tableFilters.globalFilter
                ? tableFilters.globalFilter
                : (tableFilters.columnFilters.find((e) => e.id == "user")
                    ?.value as string)
            }
          />
        </Text>
      </Box>
    ),
  });

  return (
    <Box>
      <MantineReactTable table={table} />
    </Box>
  );
}
