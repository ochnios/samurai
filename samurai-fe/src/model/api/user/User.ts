import { Role } from "./Role.ts";

export interface User {
  username: string;
  firstname: string;
  lastname: string;
  email: string;
  role: Role;
  createdAt: string;
}
