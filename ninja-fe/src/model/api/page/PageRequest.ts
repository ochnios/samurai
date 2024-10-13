export interface PageRequest {
  page?: number;
  size?: number;
  sortBy?: string[];
  sortDir?: SortDir[];

  getUrl(): string;
}

export enum SortDir {
  ASC = "asc",
  DESC = "desc",
}
