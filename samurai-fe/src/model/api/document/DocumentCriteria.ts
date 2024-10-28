import { SearchCriteria } from "../page/SearchCriteria.ts";
import { DocumentStatus } from "./DocumentStatus.ts";

export interface DocumentCriteria extends SearchCriteria {
  globalSearch?: string;
  title?: string;
  description?: string;
  userFullName?: string;
  filename?: string;
  minSize?: number;
  maxSize?: number;
  minCreatedAt?: string;
  maxCreatedAt?: string;
  status?: DocumentStatus;
}
