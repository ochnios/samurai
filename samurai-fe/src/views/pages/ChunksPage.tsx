import { Box, Button, Menu, Text } from "@mantine/core";
import {
  IconArrowDown,
  IconArrowUp,
  IconEdit,
  IconPlus,
  IconTrash,
} from "@tabler/icons-react";
import {
  MantineReactTable,
  MRT_ColumnDef,
  useMantineReactTable,
} from "mantine-react-table";
import { useEffect, useMemo, useState } from "react";
import { useTableFilters } from "../../hooks/table/useTableFilters.ts";
import { useTableState } from "../../hooks/table/useTableState.ts";
import { Page } from "../../model/api/page/Page.ts";
import {
  addChunk,
  createPageRequest,
  deleteChunk,
  fetchChunks,
  MAX_CONTENT_LENGTH,
  patchChunk,
} from "../../model/service/chunkService.ts";
import {
  defaultMantineTableContainerProps,
  showErrorMessage,
  showInfoMessage,
} from "../../utils.ts";
import HighlightedText from "../components/table/HiglightedText.tsx";
import { modals } from "@mantine/modals";
import { EmptyPage } from "../../model/api/page/EmptyPage.ts";
import { JsonPatch } from "../../model/api/patch/JsonPatch.ts";
import { showChunkEditForm } from "../components/document/chunk/ChunkEditForm.tsx";
import { useNavigate, useParams } from "react-router-dom";
import { Chunk } from "../../model/api/document/chunk/Chunk.ts";
import { UploadChunk } from "../../model/api/document/chunk/UploadChunk.ts";
import { JsonPatchNodeImpl } from "../../model/api/patch/JsonPatchNodeImpl.ts";

export default function ChunksPage() {
  const navigate = useNavigate();
  const { documentId } = useParams();
  const tableState = useTableState("chunks");
  const tableFilters = useTableFilters();
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState<Page<Chunk>>(EmptyPage.of);
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
    if (!documentId) {
      navigate("/documents");
      return;
    }

    setLoading(true);
    fetchChunks(documentId, pageRequest)
      .then((response) => setPage(response))
      .catch(() => {
        showErrorMessage("Failed to fetch chunks");
      })
      .finally(() => setLoading(false));
  }, [pageRequest]);

  function refreshChunks() {
    fetchChunks(documentId!, pageRequest)
      .then((response) => setPage(response))
      .catch(() => {
        showErrorMessage("Failed to refresh chunks page");
      });
  }

  function handleAddChunk(chunk: UploadChunk) {
    addChunk(documentId!, chunk)
      .then(() => {
        refreshChunks();
        modals.closeAll();
        showInfoMessage("Chunk uploaded successfully");
      })
      .catch(() => {
        showErrorMessage("Failed to upload chunk");
      });
  }

  function handleUpdateChunk(id: string, patchArray: JsonPatch) {
    patchChunk(documentId!, id, patchArray)
      .then(() => {
        refreshChunks();
        modals.closeAll();
        showInfoMessage("Changes saved successfully");
      })
      .catch(() => {
        showErrorMessage("Failed to save changes");
      });
  }

  function handleDeleteChunk(id: string) {
    deleteChunk(documentId!, id)
      .then(() => {
        refreshChunks();
        showInfoMessage("Chunk deleted successfully");
      })
      .catch(() => {
        showErrorMessage("Failed to delete chunk");
      });
  }

  const columns = useMemo<MRT_ColumnDef<Chunk>[]>(
    () => [
      {
        accessorKey: "position",
        header: "Position",
        Cell: ({ cell }) => {
          return cell.getValue<number>() + 1;
        },
        size: 120,
        grow: false,
      },
      {
        accessorKey: "content",
        header: "Content",
        Cell: ({ cell }) => (
          <HighlightedText
            text={cell.getValue<string>()}
            phrase={
              tableFilters.globalFilter
                ? tableFilters.globalFilter
                : (tableFilters.columnFilters.find((e) => e.id == "content")
                    ?.value as string)
            }
          />
        ),
        grow: true,
      },
      {
        accessorKey: "length",
        header: "Length",
        filterVariant: "range-slider",
        filterFn: "betweenInclusive",
        mantineFilterRangeSliderProps: {
          max: MAX_CONTENT_LENGTH,
          min: 0,
          step: 100,
          label: (value) => `${value} characters`,
        },
        size: 120,
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
        onClick={() =>
          showChunkEditForm({
            onSubmitAdd: handleAddChunk,
            defaultPosition: page.totalElements,
            maxPosition: page.totalElements,
          })
        }
      >
        Add chunk
      </Button>
    ),
    renderRowActionMenuItems: ({ row }) => {
      const chunk = page.items[row.index];
      return (
        <>
          <Menu.Item
            leftSection={<IconEdit />}
            onClick={() =>
              showChunkEditForm({
                current: chunk,
                onSubmitPatch: handleUpdateChunk,
                maxPosition: page.totalElements + 1,
              })
            }
          >
            Edit
          </Menu.Item>
          {chunk.position > 0 && (
            <Menu.Item
              leftSection={<IconArrowUp />}
              onClick={() =>
                handleUpdateChunk(
                  chunk.id,
                  JsonPatch.of(
                    JsonPatchNodeImpl.replace("/position", chunk.position - 1),
                  ),
                )
              }
            >
              Move up
            </Menu.Item>
          )}
          {chunk.position < page.items.length - 1 && (
            <Menu.Item
              leftSection={<IconArrowDown />}
              onClick={() =>
                handleUpdateChunk(
                  chunk.id,
                  JsonPatch.of(
                    JsonPatchNodeImpl.replace("/position", chunk.position + 1),
                  ),
                )
              }
            >
              Move down
            </Menu.Item>
          )}
          <Menu.Item
            leftSection={<IconPlus />}
            onClick={() =>
              showChunkEditForm({
                onSubmitAdd: handleAddChunk,
                defaultPosition: chunk.position,
                maxPosition: page.totalElements + 1,
              })
            }
          >
            Add before
          </Menu.Item>
          <Menu.Item
            leftSection={<IconPlus />}
            onClick={() =>
              showChunkEditForm({
                onSubmitAdd: handleAddChunk,
                defaultPosition: chunk.position + 1,
                maxPosition: page.totalElements + 1,
              })
            }
          >
            Add after
          </Menu.Item>
          <Menu.Item
            color="red"
            leftSection={<IconTrash />}
            onClick={() => {
              modals.openConfirmModal({
                title: (
                  <Text fz="h3" fw="bold" span>
                    Delete chunk
                  </Text>
                ),
                children: (
                  <Text>
                    Are you sure? The chunk will be permanently deleted, this
                    action cannot be undone
                  </Text>
                ),
                labels: { confirm: "Delete", cancel: "Cancel" },
                onConfirm: () => handleDeleteChunk(page.items[row.index].id),
                size: "md",
              });
            }}
          >
            Delete
          </Menu.Item>
        </>
      );
    },
  });

  return (
    <Box>
      <MantineReactTable table={table} />
    </Box>
  );
}
