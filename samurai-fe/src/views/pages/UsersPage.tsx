import { ActionIcon, Flex, Tooltip } from "@mantine/core";
import { IconEdit } from "@tabler/icons-react";
import {
  MantineReactTable,
  MRT_ColumnDef,
  MRT_TableOptions,
  useMantineReactTable,
} from "mantine-react-table";
import { useEffect, useMemo, useState } from "react";
import { useTableFilters } from "../../hooks/table/useTableFilters.ts";
import { useTableState } from "../../hooks/table/useTableState.ts";
import { Page } from "../../model/api/page/Page.ts";
import { User } from "../../model/api/user/User.ts";
import {
  createPageRequest,
  fetchUsers,
  patchUser,
} from "../../model/service/userService.ts";
import {
  defaultMantineTableContainerProps,
  showErrorMessage,
} from "../../utils.ts";
import { EmptyPage } from "../../model/api/page/EmptyPage.ts";
import { Role } from "../../model/api/user/Role.ts";
import { JsonPatch } from "../../model/api/patch/JsonPatch.ts";
import { JsonPatchNodeImpl } from "../../model/api/patch/JsonPatchNodeImpl.ts";

export default function UsersPage() {
  const tableState = useTableState("users");
  const tableFilters = useTableFilters();
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState<Page<User>>(EmptyPage.of);
  const [pageRequest, setPageRequest] = useState(
    createPageRequest(tableState, tableFilters),
  );

  useEffect(() => {
    const newPageRequest = createPageRequest(tableState, tableFilters);
    if (pageRequest.getUrlParams() != newPageRequest.getUrlParams()) {
      setPageRequest(newPageRequest);
    }
  }, [
    tableState.pagination,
    tableState.sorting,
    tableFilters.columnFilters,
    tableFilters.globalFilter,
  ]);

  useEffect(() => {
    setLoading(true);
    fetchUsers(pageRequest)
      .then((response) => setPage(response))
      .catch(() => {
        showErrorMessage("Failed to fetch users");
      })
      .finally(() => setLoading(false));
  }, [pageRequest]);

  const handleSaveRow: MRT_TableOptions<User>["onEditingRowSave"] = async ({
    table,
    values,
  }) => {
    const patch = JsonPatch.of(
      JsonPatchNodeImpl.replace("/role", values.role.toString()),
    );
    patchUser(values.username, patch)
      .then((response) => {
        setPage({
          ...page,
          items: page.items.map((u) =>
            u.username != values.username ? u : response,
          ),
        });
        table.setEditingRow(null);
      })
      .catch(() => showErrorMessage("Failed to update user"));
  };

  const columns = useMemo<MRT_ColumnDef<User>[]>(
    () => [
      {
        accessorKey: "firstname",
        header: "Firstname",
        enableEditing: false,
      },
      {
        accessorKey: "lastname",
        header: "Lastname",
        enableEditing: false,
      },
      {
        accessorKey: "username",
        header: "Username",
        enableEditing: false,
      },
      {
        accessorKey: "email",
        header: "Email",
        enableEditing: false,
      },
      {
        accessorKey: "role",
        header: "Rola",
        editVariant: "select",
        mantineEditSelectProps: {
          data: Object.values(Role),
          allowDeselect: false,
        },
      },
      {
        accessorKey: "createdAt",
        header: "Registered",
        accessorFn: (row) => new Date(row.createdAt),
        filterVariant: "date-range",
        Cell: ({ cell }) =>
          `${cell.getValue<Date>().toLocaleDateString()} ${cell
            .getValue<Date>()
            .toLocaleTimeString()}`,
        size: 200,
        grow: false,
        enableEditing: false,
      },
    ],
    [page],
  );

  const table = useMantineReactTable({
    columns: columns,
    data: page.items,
    enableEditing: true,
    editDisplayMode: "row",
    onEditingRowSave: handleSaveRow,
    positionActionsColumn: "last",
    onColumnVisibilityChange: tableState.setColumnVisibility,
    onDensityChange: tableState.setRowDensity,
    manualPagination: true,
    onPaginationChange: tableState.setPagination,
    rowCount: page.totalElements,
    manualSorting: true,
    onSortingChange: tableState.setSorting,
    manualFiltering: true,
    onColumnFiltersChange: tableFilters.setColumnFilters,
    onGlobalFilterChange: tableFilters.setGlobalFilter,
    state: {
      isLoading: loading,
      columnVisibility: tableState.columnVisibility,
      density: tableState.rowDensity,
      pagination: tableState.pagination,
      sorting: tableState.sorting,
      columnFilters: tableFilters.columnFilters,
      globalFilter: tableFilters.globalFilter,
    },
    enableStickyHeader: true,
    enableStickyFooter: true,
    mantineTableContainerProps: defaultMantineTableContainerProps,
    renderRowActions: ({ row }) => (
      <Flex gap="md">
        <Tooltip label="Edit">
          <ActionIcon onClick={() => table.setEditingRow(row)}>
            <IconEdit />
          </ActionIcon>
        </Tooltip>
      </Flex>
    ),
  });

  return <MantineReactTable table={table} />;
}
