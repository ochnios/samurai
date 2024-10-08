import { Role } from "../model/api/User";
import { useAppSelector } from "./useAppSelector.ts";

export function useIsMod() {
  const auth = useAppSelector((state) => state.auth);
  return auth.user?.role === Role.Admin || auth.user?.role === Role.Mod;
}
