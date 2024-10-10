import { MRT_PaginationState, MRT_SortingState } from "mantine-react-table";

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

export class PageRequestImpl implements PageRequest {
  page?: number;
  size?: number;
  sortBy?: string[];
  sortDir?: SortDir[];

  constructor(
    page?: number,
    size?: number,
    sortBy?: string[],
    sortDir?: SortDir[],
  ) {
    this.page = page;
    this.size = size;
    this.sortBy = sortBy;
    this.sortDir = sortDir;
  }

  static of(
    pagination?: MRT_PaginationState,
    sorting?: MRT_SortingState,
  ): PageRequestImpl {
    return new PageRequestImpl(
      pagination?.pageIndex,
      pagination?.pageSize,
      sorting?.map((e) => e.id ?? ""),
      sorting?.map((e) => (e.desc ? SortDir.DESC : SortDir.ASC)),
    );
  }

  getUrl(): string {
    const params = new URLSearchParams();
    if (this.page !== undefined) params.append("page", this.page.toString());
    if (this.size !== undefined) params.append("size", this.size.toString());
    if (this.sortBy) {
      this.sortBy.forEach((e) => params.append("sortBy", e));
    }
    if (this.sortDir) {
      this.sortDir.forEach((e) => params.append("sortDir", e));
    }
    return params.toString();
  }
}
