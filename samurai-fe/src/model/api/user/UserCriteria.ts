import { SearchCriteria } from "../page/SearchCriteria.ts";
import { Role } from "./Role.ts";

export interface UserCriteria extends SearchCriteria {
  globalSearch?: string;
  username?: string;
  firstname?: string;
  lastname?: string;
  email?: string;
  role?: Role;
  minCreatedAt?: string;
  maxCreatedAt?: string;
}
