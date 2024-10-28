import { useSelector } from "react-redux";
import type { RootState } from "../store.ts";

export const useAppSelector = useSelector.withTypes<RootState>();