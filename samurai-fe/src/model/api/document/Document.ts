import { File } from "../file/File.ts";
import { User } from "../user/User.ts";
import { DocumentStatus } from "./DocumentStatus.ts";

export interface Document extends File {
  user: User;
  title: string;
  description: string;
  status: DocumentStatus;
  updatedAt: string;
}
