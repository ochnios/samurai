import { Anchor, Button, Menu, Text } from "@mantine/core";
import {
  IconArchive,
  IconDownload,
  IconEdit,
  IconFileStack,
  IconReload,
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
  deleteDocument,
  fetchDocuments,
  formatFileSize,
  MAX_FILE_SIZE,
  patchDocument,
  uploadDocument,
} from "../../model/service/documentService.ts";
import {
  defaultMantineTableContainerProps,
  showErrorMessage,
  showInfoMessage,
} from "../../utils.ts";
import DocumentStatusBadge from "../components/document/DocumentStatusBadge.tsx";
import HighlightedText from "../components/table/HiglightedText.tsx";
import { Document } from "../../model/api/document/Document.ts";
import DocumentUploadForm from "../components/document/DocumentUploadForm.tsx";
import { DocumentUpload } from "../../model/api/document/DocumentUpload.ts";
import { modals } from "@mantine/modals";
import { EmptyPage } from "../../model/api/page/EmptyPage.ts";
import DocumentEditForm from "../components/document/DocumentEditForm.tsx";
import { JsonPatch } from "../../model/api/patch/JsonPatch.ts";
import { Link } from "react-router-dom";
import { JsonPatchNodeImpl } from "../../model/api/patch/JsonPatchNodeImpl.ts";

const apiUrl = config.baseUrl;

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
    refreshDocuments();
  }, [pageRequest]);

  useEffect(() => {
    if (
      !page.items.find(
        (d) =>
          d.status === DocumentStatus.UPLOADED ||
          d.status === DocumentStatus.IN_PROGRESS,
      )
    )
      return;

    const interval = setInterval(() => {
      refreshDocuments();
    }, 3000);

    return () => clearInterval(interval);
  }, [page]);

  function refreshDocuments() {
    fetchDocuments(pageRequest)
      .then((response) => setPage(response))
      .catch(() => {
        showErrorMessage("Failed to fetch documents");
      })
      .finally(() => setLoading(false));
  }

  function handleAddDocument(document: DocumentUpload) {
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

  function handleUpdateDocument(id: string, patch: JsonPatch) {
    patchDocument(id, patch)
      .then((response) => {
        setPage({
          ...page,
          items: page.items.map((d) => (d.id != id ? d : response)),
        });
        modals.closeAll();
        showInfoMessage("Changes saved successfully");
      })
      .catch(() => {
        showErrorMessage("Failed to save changes");
      });
  }

  function handleUpdateDocumentStatus(id: string, status: DocumentStatus) {
    handleUpdateDocument(
      id,
      JsonPatch.of(JsonPatchNodeImpl.replace("/status", status)),
    );
  }

  function handleDeleteDocument(id: string) {
    deleteDocument(id)
      .then(() => {
        setPage({
          ...page,
          items: [...page.items.filter((d) => d.id !== id)],
          totalPages: page.totalElements - 1,
        });
        showInfoMessage("Document deleted successfully");
      })
      .catch(() => {
        showErrorMessage("Failed to delete document");
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
        Cell: ({ cell }) => (
          <Anchor
            href={`${apiUrl}/documents/${page.items[cell.row.index].id}/download?inline=true`}
            target="_blank"
            td="underline"
            c="inherit"
            fz="sm"
          >
            <HighlightedText
              text={cell.getValue<string>()}
              phrase={
                tableFilters.globalFilter
                  ? tableFilters.globalFilter
                  : (tableFilters.columnFilters.find((e) => e.id == "name")
                      ?.value as string)
              }
            />
          </Anchor>
        ),
      },
      {
        accessorKey: "user",
        header: "Uploaded by",
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
        accessorKey: "updatedAt",
        header: "Updated at",
        accessorFn: (row) => new Date(row.updatedAt),
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
    renderTopToolbarCustomActions: () => (
      <Button
        onClick={() => {
          modals.open({
            title: (
              <Text fz="h3" fw="bold" span>
                Upload document
              </Text>
            ),
            children: <DocumentUploadForm onSubmit={handleAddDocument} />,
            size: "xl",
          });
        }}
      >
        Upload document
      </Button>
    ),
    renderRowActionMenuItems: ({ row }) => {
      const document = page.items[row.index];
      return (
        <>
          <Menu.Item
            leftSection={<IconEdit />}
            onClick={() => {
              modals.open({
                title: (
                  <Text fz="h3" fw="bold" span>
                    Edit document
                  </Text>
                ),
                children: (
                  <DocumentEditForm
                    current={page.items[row.index]}
                    onSubmit={handleUpdateDocument}
                  />
                ),
                size: "xl",
              });
            }}
          >
            Edit
          </Menu.Item>
          <Menu.Item
            leftSection={<IconFileStack />}
            component={Link}
            to={`/documents/${document.id}/chunks`}
          >
            Edit chunks
          </Menu.Item>
          <Menu.Item
            leftSection={<IconDownload />}
            component="a"
            href={`${apiUrl}/documents/${document?.id}/download`}
            target="_blank"
          >
            Download
          </Menu.Item>
          {(document?.status === DocumentStatus.ACTIVE ||
            document?.status === DocumentStatus.ARCHIVED ||
            document?.status === DocumentStatus.FAILED) && (
            <Menu.Item
              leftSection={<IconReload />}
              onClick={() => {
                modals.openConfirmModal({
                  title: (
                    <Text fz="h3" fw="bold" span>
                      Reload document
                    </Text>
                  ),
                  children: (
                    <Text>
                      Are you sure? The document will be processed again, all
                      changes will be lost. It may also generate additional
                      costs.
                    </Text>
                  ),
                  labels: { confirm: "Reload", cancel: "Cancel" },
                  onConfirm: () =>
                    handleUpdateDocumentStatus(
                      document.id,
                      DocumentStatus.UPLOADED,
                    ),
                  size: "md",
                });
              }}
            >
              Reload
            </Menu.Item>
          )}
          {document?.status === DocumentStatus.ACTIVE && (
            <Menu.Item
              color="yellow"
              leftSection={<IconArchive />}
              onClick={() => {
                handleUpdateDocumentStatus(
                  document.id,
                  DocumentStatus.ARCHIVED,
                );
              }}
            >
              Archive
            </Menu.Item>
          )}
          {document?.status === DocumentStatus.ARCHIVED && (
            <Menu.Item
              color="green"
              leftSection={<IconRestore />}
              onClick={() => {
                handleUpdateDocumentStatus(document.id, DocumentStatus.ACTIVE);
              }}
            >
              Restore
            </Menu.Item>
          )}
          {document?.status !== DocumentStatus.IN_PROGRESS && (
            <Menu.Item
              color="red"
              leftSection={<IconTrash />}
              onClick={() => {
                modals.openConfirmModal({
                  title: (
                    <Text fz="h3" fw="bold" span>
                      Delete document
                    </Text>
                  ),
                  children: (
                    <Text>
                      Are you sure? The document will be permanently deleted,
                      this action cannot be undone
                    </Text>
                  ),
                  labels: { confirm: "Delete", cancel: "Cancel" },
                  onConfirm: () => handleDeleteDocument(document.id),
                  size: "md",
                });
              }}
            >
              Delete
            </Menu.Item>
          )}
        </>
      );
    },
    renderDetailPanel: ({ row }) => (
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
    ),
  });

  return <MantineReactTable table={table} />;
}
