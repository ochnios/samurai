import { MRT_ColumnFiltersState } from "mantine-react-table";
import { ChunkCriteria } from "./ChunkCriteria.ts";

export class ChunkCriteriaImpl implements ChunkCriteria {
  globalSearch?: string;
  content?: string;
  minPosition?: number;
  maxPosition?: number;
  minLength?: number;
  maxLength?: number;
  minUpdatedAt?: string;
  maxUpdatedAt?: string;

  constructor(init?: Partial<ChunkCriteria>) {
    Object.assign(this, init);
  }

  static of(
    globalFilter: string,
    columnFilters: MRT_ColumnFiltersState,
  ): ChunkCriteriaImpl {
    const criteria = new ChunkCriteriaImpl();
    if (globalFilter) {
      criteria.globalSearch = globalFilter;
    }

    criteria.content = columnFilters.find((e) => e.id == "content")
      ?.value as string;

    const positionFilter = columnFilters.find((e) => e.id == "position")?.value;
    if (positionFilter) {
      const positionFilterValue = positionFilter as string[];
      const minPosition = parseInt(positionFilterValue[0]);
      criteria.minPosition = isNaN(minPosition) ? undefined : minPosition;
      const maxPosition = parseInt(positionFilterValue[1]);
      criteria.maxPosition = isNaN(maxPosition) ? undefined : maxPosition;
    }

    const lengthFilter = columnFilters.find((e) => e.id == "length")?.value;
    if (lengthFilter) {
      const lengthFilterValue = lengthFilter as string[];
      const minLength = parseInt(lengthFilterValue[0]);
      criteria.minLength = isNaN(minLength) ? undefined : minLength;
      const maxLength = parseInt(lengthFilterValue[1]);
      criteria.maxLength = isNaN(maxLength) ? undefined : maxLength;
    }

    const updatedAtFilter = columnFilters.find(
      (e) => e.id == "updatedAt",
    )?.value;
    if (updatedAtFilter) {
      const updatedAtFilterValue = updatedAtFilter as Date[];
      criteria.minUpdatedAt =
        updatedAtFilterValue[0] && updatedAtFilterValue[0]?.toISOString();
      criteria.maxUpdatedAt =
        updatedAtFilterValue[1] && updatedAtFilterValue[1]?.toISOString();
    }

    return criteria;
  }

  getUrlParams(): string {
    const params = new URLSearchParams();
    if (this.globalSearch) params.append("globalSearch", this.globalSearch);
    if (this.content) params.append("content", this.content);
    if (this.minPosition !== undefined) {
      params.append("minSize", this.minPosition.toString());
    }
    if (this.maxPosition !== undefined) {
      params.append("maxSize", this.maxPosition.toString());
    }
    if (this.minLength !== undefined) {
      params.append("minLength", this.minLength.toString());
    }
    if (this.maxLength !== undefined) {
      params.append("maxLength", this.maxLength.toString());
    }
    if (this.minUpdatedAt) params.append("minUpdatedAt", this.minUpdatedAt);
    if (this.maxUpdatedAt) params.append("maxUpdatedAt", this.maxUpdatedAt);
    return params.toString();
  }
}
