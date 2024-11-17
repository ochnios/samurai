import axios from "axios";
import { TableFilters } from "../../hooks/table/useTableFilters.ts";
import { TableState } from "../../hooks/table/useTableState.ts";
import { normalizePostfix } from "../../utils.ts";
import { User } from "../api/user/User.ts";
import { UserCriteria } from "../api/user/UserCriteria.ts";
import { UserCriteriaImpl } from "../api/user/UserCriteriaImpl.ts";
import { Page } from "../api/page/Page.ts";
import { PageRequest } from "../api/page/PageRequest.ts";
import { PageRequestImpl } from "../api/page/PageRequestImpl.ts";
import { JsonPatch } from "../api/patch/JsonPatch.ts";

const usersUrl = "/users";

export const fetchUser = async (username: string): Promise<User> => {
  return await axios
    .get<User>(`${usersUrl}/${username}`)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const fetchUsers = async (
  pageRequest: PageRequest<UserCriteria>,
): Promise<Page<UserDetails>> => {
  const postfix = normalizePostfix(pageRequest.getUrlParams());
  return await axios
    .get<Page<UserDetails>>(`${usersUrl}${postfix}`)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const patchUser = async (
  username: string,
  jsonPatch: JsonPatch,
): Promise<User> => {
  return await axios
    .patch<User>(`${usersUrl}/${username}`, jsonPatch.nodes)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      throw error;
    });
};

export const createPageRequest = (
  tableState: TableState,
  tableFilters: TableFilters,
): PageRequest<UserCriteria> => {
  return PageRequestImpl.of(
    UserCriteriaImpl.of(tableFilters.globalFilter, tableFilters.columnFilters),
    tableState.pagination,
    tableState.sorting,
  );
};
