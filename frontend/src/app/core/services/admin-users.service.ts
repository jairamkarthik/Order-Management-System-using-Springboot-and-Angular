import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AppUser, Role } from '../models/user';

@Injectable({ providedIn: 'root' })
export class AdminUsersService {
  private base = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  list(q?: string | null) {
    let params = new HttpParams();
    if (q) params = params.set('q', q);
    return this.http.get<AppUser[]>(`${this.base}/api/admin/users`, { params });
  }

  create(username: string, password: string, role: Role) {
    return this.http.post<AppUser>(`${this.base}/api/admin/users`, { username, password, role });
  }

  update(userId: number, patch: { role?: Role | null; password?: string | null }) {
    return this.http.patch<AppUser>(`${this.base}/api/admin/users/${userId}`, patch);
  }

  delete(userId: number) {
    return this.http.delete<void>(`${this.base}/api/admin/users/${userId}`);
  }
}
