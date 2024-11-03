import { MRT_ColumnFiltersState } from "mantine-react-table";
import { DocumentCriteria } from "./DocumentCriteria.ts";
import { DocumentStatus } from "./DocumentStatus.ts";

export class DocumentCriteriaImpl implements DocumentCriteria {
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

  constructor(init?: Partial<DocumentCriteria>) {
    Object.assign(this, init);
  }

  static of(
    globalFilter: string,
    columnFilters: MRT_ColumnFiltersState,
  ): DocumentCriteriaImpl {
    const criteria = new DocumentCriteriaImpl();
    if (globalFilter) {
      criteria.globalSearch = globalFilter;
    }

    const sizeFilter = columnFilters.find((e) => e.id == "size")?.value;
    if (sizeFilter) {
      const sizeFilterValue = sizeFilter as string[];
      const minSize = parseInt(sizeFilterValue[0]);
      criteria.minSize = isNaN(minSize) ? undefined : minSize;
      const maxSize = parseInt(sizeFilterValue[1]);
      criteria.maxSize = isNaN(maxSize) ? undefined : maxSize;
    }

    const createdAtFilter = columnFilters.find(
      (e) => e.id == "createdAt",
    )?.value;
    if (createdAtFilter) {
      const createdAtFilterValue = createdAtFilter as Date[];
      criteria.minCreatedAt =
        createdAtFilterValue[0] && createdAtFilterValue[0]?.toISOString();
      criteria.maxCreatedAt =
        createdAtFilterValue[1] && createdAtFilterValue[1]?.toISOString();
    }

    criteria.title = columnFilters.find((e) => e.id == "title")
      ?.value as string;
    criteria.description = columnFilters.find((e) => e.id == "description")
      ?.value as string;
    criteria.filename = columnFilters.find((e) => e.id == "filename")
      ?.value as string;
    criteria.userFullName = columnFilters.find((e) => e.id == "user")
      ?.value as string;
    criteria.status = columnFilters.find((e) => e.id == "status")
      ?.value as DocumentStatus;

    return criteria;
  }

  getUrlParams(): string {
    const params = new URLSearchParams();
    if (this.globalSearch) params.append("globalSearch", this.globalSearch);
    if (this.title) params.append("title", this.title);
    if (this.description) params.append("description", this.description);
    if (this.filename) params.append("filename", this.filename);
    if (this.minSize !== undefined) {
      params.append("minSize", this.minSize.toString());
    }
    if (this.maxSize !== undefined) {
      params.append("maxSize", this.maxSize.toString());
    }
    if (this.minCreatedAt) params.append("minCreatedAt", this.minCreatedAt);
    if (this.maxCreatedAt) params.append("maxCreatedAt", this.maxCreatedAt);
    if (this.userFullName) params.append("userFullName", this.userFullName);
    if (this.minCreatedAt) params.append("minCreatedAt", this.minCreatedAt);
    if (this.status) params.append("status", this.status.toString());
    return params.toString();
  }
}
