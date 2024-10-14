import { MRT_ColumnFiltersState } from "mantine-react-table";
import React, { useState } from "react";

export interface TableFilters {
  columnFilters: MRT_ColumnFiltersState;
  setColumnFilters: React.Dispatch<
    React.SetStateAction<MRT_ColumnFiltersState>
  >;
  globalFilter: string;
  setGlobalFilter: React.Dispatch<React.SetStateAction<string>>;
}

export function useTableFilters(): TableFilters {
  const [columnFilters, setColumnFilters] = useState<MRT_ColumnFiltersState>(
    [],
  );
  const [globalFilter, setGlobalFilter] = useState("");

  return {
    columnFilters,
    setColumnFilters,
    globalFilter,
    setGlobalFilter,
  };
}
