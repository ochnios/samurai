import { JsonPatchNode } from "./JsonPatchNode.ts";

export class JsonPatch {
  nodes: JsonPatchNode[] = [];

  static of(...patches: JsonPatchNode[]): JsonPatch {
    const instance = new JsonPatch();
    instance.nodes.push(...patches);
    return instance;
  }

  static empty(): JsonPatch {
    return new JsonPatch();
  }

  add(patch: JsonPatchNode): JsonPatch {
    this.nodes.push(patch);
    return this;
  }
}
