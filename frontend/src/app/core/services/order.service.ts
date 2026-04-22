import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order, OrderRequest } from '../models/order.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly url = `${environment.apiUrl}/orders`;
  constructor(private http: HttpClient) {}

  getAll(): Observable<Order[]>           { return this.http.get<Order[]>(this.url); }
  getById(id: number): Observable<Order>  { return this.http.get<Order>(`${this.url}/${id}`); }
  create(req: OrderRequest): Observable<Order> { return this.http.post<Order>(this.url, req); }
  confirm(id: number): Observable<Order>  { return this.http.patch<Order>(`${this.url}/${id}/confirm`, {}); }
  cancel(id: number): Observable<Order>   { return this.http.patch<Order>(`${this.url}/${id}/cancel`, {}); }
}
