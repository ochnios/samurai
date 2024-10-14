import {
  MRT_DensityState,
  MRT_PaginationState,
  MRT_SortingState,
  MRT_VisibilityState,
} from "mantine-react-table";
import React from "react";
import { useColumnVisibilityState } from "./useColumnVisibilityState.ts";
import { usePaginationState } from "./usePaginationState.ts";
import { useRowDensityState } from "./useRowDensityState.ts";
import { useSortingState } from "./useSortingState.ts";

export interface TableState {
  columnVisibility: MRT_VisibilityState;
  setColumnVisibility: React.Dispatch<
    React.SetStateAction<MRT_VisibilityState>
  >;
  rowDensity: MRT_DensityState;
  setRowDensity: React.Dispatch<React.SetStateAction<MRT_DensityState>>;
  pagination: MRT_PaginationState;
  setPagination: React.Dispatch<React.SetStateAction<MRT_PaginationState>>;
  sorting: MRT_SortingState;
  setSorting: React.Dispatch<React.SetStateAction<MRT_SortingState>>;
}

export function useTableState(tableName: string): TableState {
  const [columnVisibility, setColumnVisibility] =
    useColumnVisibilityState(tableName);
  const [rowDensity, setRowDensity] = useRowDensityState(tableName);
  const [pagination, setPagination] = usePaginationState(tableName);
  const [sorting, setSorting] = useSortingState(tableName);

  return {
    columnVisibility,
    setColumnVisibility,
    rowDensity,
    setRowDensity,
    pagination,
    setPagination,
    sorting,
    setSorting,
  };
}
