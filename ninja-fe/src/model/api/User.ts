export interface User {
  username: string;
  email: string;
  role: Role;
}

export enum Role {
  User = "ROLE_USER",
  Mod = "ROLE_MOD",
  Admin = "ROLE_ADMIN",
}
