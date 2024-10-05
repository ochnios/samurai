export interface PatchOperation {
  op: string;
  path: string;
  value?: unknown;
}

export class Patch {
  static add(path: string, value: unknown): PatchOperation[] {
    return [{ op: "add", path, value }];
  }

  static replace(path: string, value: unknown): PatchOperation[] {
    return [{ op: "replace", path, value }];
  }

  static remove(path: string): PatchOperation[] {
    return [{ op: "remove", path }];
  }
}
