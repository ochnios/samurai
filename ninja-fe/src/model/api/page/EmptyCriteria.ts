import { SearchCriteria } from "./SearchCriteria.ts";

export class EmptyCriteria implements SearchCriteria {
  getUrlParams(): string {
    return "";
  }
}
