import { MRT_SortingState } from "mantine-react-table";
import React, { useEffect, useState } from "react";

const defaultSorting: MRT_SortingState = [{ id: "createdAt", desc: true }];

export function useSortingState(
  tableName: string,
): [MRT_SortingState, React.Dispatch<React.SetStateAction<MRT_SortingState>>] {
  const savedSorting = localStorage.getItem(`mrt_${tableName}_sorting`);
  const [sorting, setSorting] = useState<MRT_SortingState>(
    savedSorting ? JSON.parse(savedSorting) : defaultSorting,
  );

  useEffect(() => {
    localStorage.setItem(`mrt_${tableName}_sorting`, JSON.stringify(sorting));
  }, [tableName, sorting]);

  return [sorting, setSorting];
}
