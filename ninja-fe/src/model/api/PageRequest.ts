export interface PageRequest {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: string;

  getUrl(): string;
}

export enum SortDir {
  ASC = "asc",
  DESC = "desc",
}

export class PageRequestImpl implements PageRequest {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: string;

  constructor(
    page?: number,
    size?: number,
    sortBy?: string,
    sortDir?: SortDir,
  ) {
    this.page = page;
    this.size = size;
    this.sortBy = sortBy;
    this.sortDir = sortDir;
  }

  getUrl(): string {
    const params = new URLSearchParams();
    if (this.page !== undefined) params.append("page", this.page.toString());
    if (this.size !== undefined) params.append("size", this.size.toString());
    if (this.sortBy) params.append("sortBy", this.sortBy);
    if (this.sortDir) params.append("sortDir", this.sortDir);
    return params.toString();
  }
}
