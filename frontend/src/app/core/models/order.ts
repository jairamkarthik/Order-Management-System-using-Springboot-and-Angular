import { Product } from './product';

export type OrderStatus = 'PLACED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

export interface ShippingAddress {
  fullName: string;
  phone: string;
  line1: string;
  line2?: string;
  city: string;
  state: string;
  pincode: string;
  country: string;
}

export interface OrderItem {
  orderItemId: number;
  product: Product;
  qty: number;
  unitPriceAtPurchase: number;
}

export interface PurchaseOrder {
  orderId: number;
  status: OrderStatus;
  createdAt: string;
  items: OrderItem[];
  shippingAddress?: ShippingAddress;
  placedBy?: { userId: number; username: string; role: string };
}
