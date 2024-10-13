import { MRT_ColumnFiltersState } from "mantine-react-table";
import { ConversationCriteria } from "./ConversationCriteria.ts";

export class ConversationCriteriaImpl implements ConversationCriteria {
  globalSearch?: string;
  minMessageCount?: number;
  maxMessageCount?: number;
  minCreatedAt?: string;
  maxCreatedAt?: string;
  summary?: string;
  userFullName?: string;
  deleted?: boolean;

  constructor(init?: Partial<ConversationCriteria>) {
    Object.assign(this, init);
  }

  static of(
    globalFilter: string,
    columnFilters: MRT_ColumnFiltersState,
  ): ConversationCriteriaImpl {
    const criteria = new ConversationCriteriaImpl();
    if (globalFilter) {
      criteria.globalSearch = globalFilter;
    }

    const messageCountFilter = columnFilters.find(
      (e) => e.id == "messageCount",
    )?.value;
    if (messageCountFilter) {
      const messageCountFilterValue = messageCountFilter as string[];
      const minMessageCount = parseInt(messageCountFilterValue[0]);
      criteria.minMessageCount = isNaN(minMessageCount)
        ? undefined
        : minMessageCount;
      const maxMessageCount = parseInt(messageCountFilterValue[1]);
      criteria.maxMessageCount = isNaN(maxMessageCount)
        ? undefined
        : maxMessageCount;
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

    criteria.userFullName = columnFilters.find((e) => e.id == "user")
      ?.value as string;
    criteria.summary = columnFilters.find((e) => e.id == "summary")
      ?.value as string;
    criteria.deleted = columnFilters.find((e) => e.id == "deleted")
      ?.value as boolean;
    return criteria;
  }

  getUrl(): string {
    const params = new URLSearchParams();
    if (this.globalSearch) params.append("globalSearch", this.globalSearch);
    if (this.minMessageCount !== undefined)
      params.append("minMessageCount", this.minMessageCount.toString());
    if (this.maxMessageCount !== undefined)
      params.append("maxMessageCount", this.maxMessageCount.toString());
    if (this.minCreatedAt) params.append("minCreatedAt", this.minCreatedAt);
    if (this.maxCreatedAt) params.append("maxCreatedAt", this.maxCreatedAt);
    if (this.summary) params.append("summary", this.summary);
    if (this.userFullName) params.append("userFullName", this.userFullName);
    if (this.deleted !== undefined)
      params.append("deleted", this.deleted.toString());
    return params.toString();
  }
}
