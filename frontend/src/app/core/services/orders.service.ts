import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { PurchaseOrder, OrderStatus, ShippingAddress } from '../models/order';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class OrdersService {
  private base = environment.apiBaseUrl;

  constructor(private http: HttpClient, private auth: AuthService) {}

  // USER endpoints
  myOrders(filters?: { status?: OrderStatus | null; q?: string | null }) {
    let params = new HttpParams();
    if (filters?.status) params = params.set('status', filters.status);
    if (filters?.q) params = params.set('q', filters.q);
    return this.http.get<PurchaseOrder[]>(`${this.base}/api/me/orders`, { params });
  }

  myOrder(orderId: number) {
    return this.http.get<PurchaseOrder>(`${this.base}/api/me/orders/${orderId}`);
  }

  placeOrder(items: { productId: number; qty: number }[], address: ShippingAddress) {
    return this.http.post<PurchaseOrder>(`${this.base}/api/me/orders`, { items, address });
  }

  updateMyOrder(orderId: number, items: { productId: number; qty: number }[], address: ShippingAddress) {
    return this.http.put<PurchaseOrder>(`${this.base}/api/me/orders/${orderId}`, { items, address });
  }

  cancelMyOrder(orderId: number) {
    return this.http.patch<PurchaseOrder>(`${this.base}/api/me/orders/${orderId}/cancel`, {});
  }

  // ADMIN endpoints
  adminOrders(filters?: { status?: OrderStatus | null; q?: string | null }) {
    let params = new HttpParams();
    if (filters?.status) params = params.set('status', filters.status);
    if (filters?.q) params = params.set('q', filters.q);
    return this.http.get<PurchaseOrder[]>(`${this.base}/api/admin/orders`, { params });
  }

  adminUpdateStatus(orderId: number, status: OrderStatus) {
    return this.http.patch<PurchaseOrder>(`${this.base}/api/admin/orders/${orderId}/status`, { status });
  }

  isAdmin(): boolean {
    return this.auth.isAdmin();
  }
}
