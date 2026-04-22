export interface Product {
  id?: number;
  name: string;
  description?: string;
  price: number | null;
  stock: number | null;
  category?: string;
}
