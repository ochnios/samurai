import axios from "axios";
import { TableFilters } from "../../hooks/table/useTableFilters.ts";
import { TableState } from "../../hooks/table/useTableState.ts";
import { normalizePostfix } from "../../utils.ts";
import { Document } from "../api/document/Document.ts";
import { DocumentCriteria } from "../api/document/DocumentCriteria.ts";
import { DocumentCriteriaImpl } from "../api/document/DocumentCriteriaImpl.ts";
import { Page } from "../api/page/Page.ts";
import { PageRequest } from "../api/page/PageRequest.ts";
import { PageRequestImpl } from "../api/page/PageRequestImpl.ts";
import { Patch } from "../api/patch/Patch.ts";
import { processSorting } from "./sortService.ts";
import { DocumentUpload } from "../api/document/DocumentUpload.ts";

export const MAX_FILE_SIZE = 52_428_800;
export const MIN_TITLE_LENGTH = 3;
export const MAX_TITLE_LENGTH = 255;
export const MIN_DESCRIPTION_LENGTH = 3;
export const MAX_DESCRIPTION_LENGTH = 2048;

const documentsUrl = "/documents";

export const fetchDocument = async (documentId: string): Promise<Document> => {
  return await axios
    .get<Document>(`${documentsUrl}/${documentId}`)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const fetchDocuments = async (
  pageRequest: PageRequest<DocumentCriteria>,
): Promise<Page<Document>> => {
  const postfix = normalizePostfix(pageRequest.getUrlParams());
  return axios
    .get<Page<Document>>(`${documentsUrl}${postfix}`)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const uploadDocument = async (
  document: DocumentUpload,
): Promise<Document> => {
  return await axios
    .post<Document>(`${documentsUrl}`, document, {
      headers: { "Content-Type": "multipart/form-data" },
    })
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const patchDocument = async (
  documentId: string,
  patch: Patch[],
): Promise<Document> => {
  return await axios
    .patch<Document>(`${documentsUrl}/${documentId}`, patch)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const deleteDocument = async (documentId: string): Promise<void> => {
  return await axios
    .delete<void>(`${documentsUrl}/${documentId}`)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const createPageRequest = (
  tableState: TableState,
  tableFilters: TableFilters,
): PageRequest<DocumentCriteria> => {
  return PageRequestImpl.of(
    DocumentCriteriaImpl.of(
      tableFilters.globalFilter,
      tableFilters.columnFilters,
    ),
    tableState.pagination,
    processSorting(tableState.sorting),
  );
};

export const formatFileSize = (bytes: number): string => {
  const sizes = ["B", "KB", "MB", "GB", "TB"];
  if (bytes === 0) return "0 Bytes";
  const i = Math.floor(Math.log(bytes) / Math.log(1024));
  return parseFloat((bytes / Math.pow(1024, i)).toFixed(2)) + " " + sizes[i];
};
