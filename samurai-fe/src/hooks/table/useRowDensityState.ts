import { MRT_DensityState } from "mantine-react-table";
import React, { useEffect, useState } from "react";

const defaultDensity: MRT_DensityState = "xs";

export function useRowDensityState(
  tableName: string,
): [MRT_DensityState, React.Dispatch<React.SetStateAction<MRT_DensityState>>] {
  const savedDensity = localStorage.getItem(`mrt_${tableName}_density`);
  const [density, setDensity] = useState<MRT_DensityState>(
    savedDensity ? JSON.parse(savedDensity) : defaultDensity,
  );

  useEffect(() => {
    localStorage.setItem(`mrt_${tableName}_density`, JSON.stringify(density));
  }, [tableName, density]);

  return [density, setDensity];
}
