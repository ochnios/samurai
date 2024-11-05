import { SearchCriteria } from "../../page/SearchCriteria.ts";

export interface ChunkCriteria extends SearchCriteria {
  globalSearch?: string;
  content?: string;
  minPosition?: number;
  maxPosition?: number;
  minLength?: number;
  maxLength?: number;
  minUpdatedAt?: string;
  maxUpdatedAt?: string;
}
