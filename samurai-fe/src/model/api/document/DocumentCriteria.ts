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
  minUpdatedAt?: string;
  maxUpdatedAt?: string;
  status?: DocumentStatus;
}
