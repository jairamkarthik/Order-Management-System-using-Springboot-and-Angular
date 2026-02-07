import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { TokenStorageService } from './token-storage.service';
import { tap } from 'rxjs/operators';

type AuthResponse = { token: string; role: 'ADMIN' | 'USER' };

@Injectable({ providedIn: 'root' })
export class AuthService {
  private base = environment.apiBaseUrl;

  constructor(private http: HttpClient, private storage: TokenStorageService) {}

  login(username: string, password: string) {
    return this.http
      .post<AuthResponse>(`${this.base}/api/auth/login`, { username, password })
      .pipe(tap(res => this.saveToken(res.token)));   // ✅ save token immediately
  }

  register(username: string, password: string) {
    return this.http
      .post<AuthResponse>(`${this.base}/api/auth/register`, { username, password })
      .pipe(tap(res => this.saveToken(res.token)));   // ✅ save token immediately
  }

  saveToken(token: string) {
    this.storage.setToken(token);
  }

  logout() {
    this.storage.clear();
  }

  isLoggedIn(): boolean {
    return !!this.storage.getToken();
  }

  getToken(): string | null {
    return this.storage.getToken();
  }

  private parseJwt(): any | null {
    const token = this.storage.getToken();
    if (!token) return null;
    const parts = token.split('.');
    if (parts.length !== 3) return null;

    try {
      const b64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
      // handle base64 padding
      const padded = b64 + '='.repeat((4 - (b64.length % 4)) % 4);
      return JSON.parse(atob(padded));
    } catch {
      return null;
    }
  }

  currentUsername(): string {
    return this.parseJwt()?.sub ?? '—';
  }

  currentRole(): 'ADMIN' | 'USER' | null {
    const role = this.parseJwt()?.role;
    return role === 'ADMIN' || role === 'USER' ? role : null;
  }

  isAdmin(): boolean {
    return this.currentRole() === 'ADMIN';
  }
}