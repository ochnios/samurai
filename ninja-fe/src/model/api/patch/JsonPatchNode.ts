export interface JsonPatchNode {
  op: string;
  path: string;
  value?: unknown;
}
