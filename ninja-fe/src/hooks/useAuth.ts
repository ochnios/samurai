import { useAppSelector } from "./useAppSelector.ts";

export function useAuth() {
  return useAppSelector((state) => state.auth);
}
