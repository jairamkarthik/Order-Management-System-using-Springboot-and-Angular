import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Product } from '../models/product';

@Injectable({ providedIn: 'root' })
export class ProductsService {
  private base = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  list() {
    return this.http.get<Product[]>(`${this.base}/api/products`);
  }

  create(payload: Partial<Product>) {
    return this.http.post<Product>(`${this.base}/api/products`, payload);
  }

  update(id: number, payload: Partial<Product>) {
    return this.http.put<Product>(`${this.base}/api/products/${id}`, payload);
  }

  delete(id: number) {
    return this.http.delete<void>(`${this.base}/api/products/${id}`);
  }
}
