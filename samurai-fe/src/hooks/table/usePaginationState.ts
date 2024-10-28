import { MRT_PaginationState } from "mantine-react-table";
import React, { useEffect, useState } from "react";

const defaultPagination: MRT_PaginationState = { pageIndex: 0, pageSize: 10 };

export function usePaginationState(
  tableName: string,
): [
  MRT_PaginationState,
  React.Dispatch<React.SetStateAction<MRT_PaginationState>>,
] {
  const savedPagination = localStorage.getItem(`mrt_${tableName}_pagination`);
  const [pagination, setPagination] = useState<MRT_PaginationState>(
    savedPagination ? JSON.parse(savedPagination) : defaultPagination,
  );

  useEffect(() => {
    localStorage.setItem(
      `mrt_${tableName}_pagination`,
      JSON.stringify({ ...pagination, pageIndex: 0 }),
    );
  }, [tableName, pagination]);

  return [pagination, setPagination];
}
