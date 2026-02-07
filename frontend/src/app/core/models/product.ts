export interface Product {
  productId: number;
  sku?: string;
  name: string;
  description?: string;
  unitPrice: number;
  stockQty: number;
  isActive: boolean;
}
