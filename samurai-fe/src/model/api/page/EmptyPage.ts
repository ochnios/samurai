import { Page } from "./Page.ts";

export class EmptyPage<T> implements Page<T> {
  items: T[] = [];
  pageNumber = 0;
  totalElements = 0;
  totalPages = 0;

  static of<T>(): EmptyPage<T> {
    return new EmptyPage<T>();
  }
}
