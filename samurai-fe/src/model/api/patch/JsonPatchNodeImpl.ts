import { JsonPatchNode } from "./JsonPatchNode.ts";

export class JsonPatchNodeImpl implements JsonPatchNode {
  op: string;
  path: string;
  value?: unknown;

  constructor(op: string, path: string, value?: unknown) {
    this.op = op;
    this.path = path;
    this.value = value;
  }

  static add(path: string, value: unknown): JsonPatchNode {
    return { op: "add", path, value };
  }

  static replace(path: string, value: unknown): JsonPatchNode {
    return { op: "replace", path, value };
  }

  static remove(path: string): JsonPatchNode {
    return { op: "remove", path };
  }
}
