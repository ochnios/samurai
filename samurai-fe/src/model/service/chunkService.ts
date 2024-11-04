import axios from "axios";
import { TableFilters } from "../../hooks/table/useTableFilters.ts";
import { TableState } from "../../hooks/table/useTableState.ts";
import { normalizePostfix } from "../../utils.ts";
import { Page } from "../api/page/Page.ts";
import { PageRequest } from "../api/page/PageRequest.ts";
import { PageRequestImpl } from "../api/page/PageRequestImpl.ts";
import { JsonPatch } from "../api/patch/JsonPatch.ts";
import { ChunkCriteria } from "../api/document/chunk/ChunkCriteria.ts";
import { Chunk } from "../api/document/chunk/Chunk.ts";
import { ChunkCriteriaImpl } from "../api/document/chunk/ChunkCriteriaImpl.ts";
import { UploadChunk } from "../api/document/chunk/UploadChunk.ts";
import { MRT_SortingState } from "mantine-react-table";

export const MIN_CONTENT_LENGTH = 20;
export const MAX_CONTENT_LENGTH = 8192;

const chunksUrl = "/documents";

export const fetchChunks = async (
  documentId: string,
  pageRequest: PageRequest<ChunkCriteria>,
): Promise<Page<Chunk>> => {
  const postfix = normalizePostfix(pageRequest.getUrlParams());
  return axios
    .get<Page<Chunk>>(`${chunksUrl}/${documentId}/chunks${postfix}`)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const addChunk = async (
  documentId: string,
  chunk: UploadChunk,
): Promise<Chunk> => {
  return await axios
    .post<Chunk>(`${chunksUrl}/${documentId}/chunks`, chunk)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const patchChunk = async (
  documentId: string,
  chunkId: string,
  jsonPatch: JsonPatch,
): Promise<Chunk> => {
  return await axios
    .patch<Chunk>(
      `${chunksUrl}/${documentId}/chunks/${chunkId}`,
      jsonPatch.nodes,
    )
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const deleteChunk = async (
  documentId: string,
  chunkId: string,
): Promise<void> => {
  return await axios
    .delete<void>(`${chunksUrl}/${documentId}/chunks/${chunkId}`)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

const processSorting = (sorting: MRT_SortingState): MRT_SortingState => {
  return sorting.map((s) =>
    s.id == "createdAt" ? { ...s, id: "updatedAt" } : s,
  );
};

export const createPageRequest = (
  tableState: TableState,
  tableFilters: TableFilters,
): PageRequest<ChunkCriteria> => {
  return PageRequestImpl.of(
    ChunkCriteriaImpl.of(tableFilters.globalFilter, tableFilters.columnFilters),
    tableState.pagination,
    processSorting(tableState.sorting),
  );
};

export const validatePosition = (value: number | undefined): string | null => {
  return value && value > 0 ? null : "Chunk position cannot be negative";
};

export const validateContent = (value: string | undefined): string | null => {
  const contentLength = value?.trim().length;
  return contentLength &&
    contentLength >= MIN_CONTENT_LENGTH &&
    contentLength < MAX_CONTENT_LENGTH
    ? null
    : `Chunk content should be between ${MIN_CONTENT_LENGTH} and ${MAX_CONTENT_LENGTH} characters`;
};
