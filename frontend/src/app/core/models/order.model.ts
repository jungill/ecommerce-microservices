export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED';

export interface Order {
  id?: number;
  productId: number;
  productName?: string;
  quantity: number;
  totalPrice?: number;
  status?: OrderStatus;
  createdAt?: string;
}

export interface OrderRequest {
  productId: number;
  quantity: number;
}
