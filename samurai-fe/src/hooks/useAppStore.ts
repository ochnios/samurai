import { useStore } from "react-redux";
import type { AppStore } from "../store.ts";

export const useAppStore = useStore.withTypes<AppStore>();