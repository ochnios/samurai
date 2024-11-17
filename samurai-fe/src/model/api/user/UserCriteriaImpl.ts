import { UserCriteria } from "./UserCriteria.ts";
import { Role } from "./Role.ts";
import { MRT_ColumnFiltersState } from "mantine-react-table";

export class UserCriteriaImpl implements UserCriteria {
  globalSearch?: string;
  username?: string;
  firstname?: string;
  lastname?: string;
  email?: string;
  role?: Role;
  minCreatedAt?: string;
  maxCreatedAt?: string;

  constructor(init?: Partial<UserCriteria>) {
    Object.assign(this, init);
  }

  static of(
    globalFilter: string,
    columnFilters: MRT_ColumnFiltersState,
  ): UserCriteriaImpl {
    const criteria = new UserCriteriaImpl();
    if (globalFilter) {
      criteria.globalSearch = globalFilter;
    }

    criteria.username = columnFilters.find((e) => e.id == "username")
      ?.value as string;
    criteria.firstname = columnFilters.find((e) => e.id == "firstname")
      ?.value as string;
    criteria.lastname = columnFilters.find((e) => e.id == "lastname")
      ?.value as string;
    criteria.email = columnFilters.find((e) => e.id == "email")
      ?.value as string;
    criteria.role = columnFilters.find((e) => e.id == "role")?.value as Role;

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

    return criteria;
  }

  getUrlParams(): string {
    const params = new URLSearchParams();
    if (this.globalSearch) params.append("globalSearch", this.globalSearch);
    if (this.username) params.append("username", this.username);
    if (this.firstname) params.append("firstname", this.firstname);
    if (this.lastname) params.append("lastname", this.lastname);
    if (this.email) params.append("email", this.email);
    if (this.role) params.append("role", this.role.toString());
    if (this.minCreatedAt) params.append("minCreatedAt", this.minCreatedAt);
    if (this.maxCreatedAt) params.append("maxCreatedAt", this.maxCreatedAt);
    return params.toString();
  }
}
