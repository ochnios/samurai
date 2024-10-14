import { SearchCriteria } from "./SearchCriteria.ts";

export interface PageRequest<T extends SearchCriteria> {
  criteria?: T;
  page?: number;
  size?: number;
  sortBy?: string[];
  sortDir?: SortDir[];

  getUrlParams(): string;
}

export enum SortDir {
  ASC = "asc",
  DESC = "desc",
}
