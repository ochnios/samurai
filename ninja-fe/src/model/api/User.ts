export interface User {
  username: string;
  firstname: string;
  lastname: string;
  email: string;
  role: Role;
}

export enum Role {
  User = "User",
  Mod = "Mod",
  Admin = "Admin",
}
