export type Role = 'ADMIN' | 'USER';

export interface AppUser {
  userId: number;
  username: string;
  role: Role;
  createdAt: string;
}
