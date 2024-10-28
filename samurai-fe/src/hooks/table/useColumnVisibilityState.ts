import { MRT_VisibilityState } from "mantine-react-table";
import React, { useEffect, useState } from "react";

const defaultColumnVisibility: MRT_VisibilityState = {};

export function useColumnVisibilityState(
  tableName: string,
): [
  MRT_VisibilityState,
  React.Dispatch<React.SetStateAction<MRT_VisibilityState>>,
] {
  const savedColumnVisibility = localStorage.getItem(
    `mrt_${tableName}_col_visibility`,
  );
  const [columnVisibility, setColumnVisibility] = useState<MRT_VisibilityState>(
    savedColumnVisibility
      ? JSON.parse(savedColumnVisibility)
      : defaultColumnVisibility,
  );

  useEffect(() => {
    localStorage.setItem(
      `mrt_${tableName}_col_visibility`,
      JSON.stringify(columnVisibility),
    );
  }, [tableName, columnVisibility]);

  return [columnVisibility, setColumnVisibility];
}
