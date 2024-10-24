import { MRT_SortingState } from "mantine-react-table";

export const processSorting = (sorting: MRT_SortingState): MRT_SortingState => {
  return sorting.flatMap((s) =>
    s.id == "user"
      ? [
          { id: "user.lastname", desc: s.desc },
          { id: "user.firstname", desc: s.desc },
        ]
      : [s],
  );
};
