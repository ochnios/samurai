export interface Page<T> {
  items: T[];
  pageNumber: number;
  totalElements: number;
  totalPages: number;
}
