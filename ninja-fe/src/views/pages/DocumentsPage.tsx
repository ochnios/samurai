import { Box, Button, Menu, Text } from "@mantine/core";
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
  MAX_FILE_SIZE,
  uploadDocument,
} from "../../model/service/documentService.ts";
import { showErrorMessage, showInfoMessage } from "../../utils.ts";
import DocumentStatusBadge from "../components/document/DocumentStatusBadge.tsx";
import HighlightedText from "../components/table/HiglightedText.tsx";
import { Document } from "../../model/api/document/Document.ts";
import DocumentForm from "../components/document/DocumentForm.tsx";
import { DocumentUpload } from "../../model/api/document/DocumentUpload.ts";
import { modals } from "@mantine/modals";
import { EmptyPage } from "../../model/api/page/EmptyPage.ts";

export default function DocumentsPage() {
  const tableState = useTableState("documents");
  const tableFilters = useTableFilters();
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState<Page<Document>>(EmptyPage.of);
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

  function addDocument(document: DocumentUpload) {
    uploadDocument(document)
      .then((response) => {
        setPage({
          ...page,
          items: [response, ...page.items],
          totalPages: page.totalElements + 1,
        });
        modals.closeAll();
        showInfoMessage("Document uploaded successfully");
      })
      .catch(() => {
        showErrorMessage("Failed to upload document");
      });
  }

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
          />
        ),
        size: 180,
        grow: false,
      },
      {
        accessorKey: "size",
        header: "Size",
        enableEditing: false,
        filterVariant: "range-slider",
        filterFn: "betweenInclusive",
        Cell: ({ cell }) => formatFileSize(cell.getValue<number>()),
        mantineFilterRangeSliderProps: {
          max: MAX_FILE_SIZE,
          min: 0,
          step: 1000,
          label: (value) => `${value}B`,
        },
        size: 120,
        grow: false,
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
        size: 140,
        grow: false,
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
        size: 200,
        grow: false,
      },
    ],
    [page],
  );

  const table = useMantineReactTable({
    columns: columns,
    data: page.items,
    layoutMode: "grid",
    enableEditing: true,
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
    renderTopToolbarCustomActions: () => (
      <Button
        onClick={() => {
          modals.open({
            title: <Text>Upload document</Text>,
            children: <DocumentForm onSubmit={addDocument} />,
            size: "xl",
          });
        }}
      >
        Upload document
      </Button>
    ),
    renderRowActionMenuItems: ({ row }) => {
      const apiUrl = config.baseUrl;
      const document = page.items[row.index];
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
            text={page.items[row.index].description}
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
