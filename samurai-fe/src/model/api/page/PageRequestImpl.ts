import { MRT_PaginationState, MRT_SortingState } from "mantine-react-table";
import { PageRequest, SortDir } from "./PageRequest.ts";
import { SearchCriteria } from "./SearchCriteria.ts";

export class PageRequestImpl<T extends SearchCriteria>
  implements PageRequest<T>
{
  criteria?: T;
  page?: number;
  size?: number;
  sortBy?: string[];
  sortDir?: SortDir[];

  constructor(
    criteria?: T,
    page?: number,
    size?: number,
    sortBy?: string[],
    sortDir?: SortDir[],
  ) {
    this.criteria = criteria;
    this.page = page;
    this.size = size;
    this.sortBy = sortBy;
    this.sortDir = sortDir;
  }

  static of<U extends SearchCriteria>(
    criteria?: U,
    pagination?: MRT_PaginationState,
    sorting?: MRT_SortingState,
  ): PageRequestImpl<U> {
    return new PageRequestImpl(
      criteria,
      pagination?.pageIndex,
      pagination?.pageSize,
      sorting?.map((e) => e.id ?? ""),
      sorting?.map((e) => (e.desc ? SortDir.DESC : SortDir.ASC)),
    );
  }

  getUrlParams(): string {
    const params = new URLSearchParams();
    if (this.page !== undefined) params.append("page", this.page.toString());
    if (this.size !== undefined) params.append("size", this.size.toString());
    if (this.sortBy) {
      this.sortBy.forEach((e) => params.append("sortBy", e));
    }
    if (this.sortDir) {
      this.sortDir.forEach((e) => params.append("sortDir", e));
    }

    if (this.criteria) {
      const criteriaParams = this.criteria.getUrlParams();
      if (criteriaParams) {
        return `${criteriaParams}&${params.toString()}`;
      }
    }

    return params.toString();
  }
}
