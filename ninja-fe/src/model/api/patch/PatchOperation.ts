export interface PatchOperation {
  op: string;
  path: string;
  value?: unknown;
}
