import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { OrderService } from '../../core/services/order.service';
import { Order } from '../../core/models/order.model';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="page">
      <div class="page-header">
        <h1>Commandes</h1>
        <a routerLink="/orders/new" class="btn btn-primary">+ Nouvelle commande</a>
      </div>

      <div *ngIf="loading" class="loading">Chargement...</div>

      <div class="table-wrapper">
        <table class="table" *ngIf="!loading && orders.length > 0">
          <thead>
            <tr>
              <th>#</th><th>Produit</th><th>Qté</th><th>Total</th><th>Statut</th><th>Date</th><th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let o of orders">
              <td>{{ o.id }}</td>
              <td>{{ o.productName }}</td>
              <td>{{ o.quantity }}</td>
              <td>{{ o.totalPrice | number:'1.2-2' }} €</td>
              <td><span class="badge" [ngClass]="o.status?.toLowerCase()">{{ o.status }}</span></td>
              <td>{{ o.createdAt | date:'dd/MM/yyyy HH:mm' }}</td>
              <td>
                <button *ngIf="o.status === 'PENDING'" (click)="confirm(o.id!)" class="btn btn-sm btn-success">Confirmer</button>
                <button *ngIf="o.status !== 'CANCELLED'" (click)="cancel(o.id!)" class="btn btn-sm btn-danger">Annuler</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div *ngIf="!loading && orders.length === 0" class="empty">Aucune commande.</div>
    </div>
  `,
  styles: [`
    .page { padding:24px; max-width:1100px; margin:0 auto; }
    .table-wrapper { overflow-x: auto; }
    .page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:20px; }
    h1 { font-size:1.8rem; color:#1a6bad; }
    .table { width:100%; border-collapse:collapse; background:#fff; border-radius:12px; overflow:hidden; box-shadow:0 2px 8px #0001; }
    th { background:#1a6bad; color:#fff; padding:12px 16px; text-align:left; font-size:.85rem; }
    td { padding:12px 16px; border-bottom:1px solid #f0f0f0; font-size:.9rem; }
    tr:last-child td { border-bottom:none; }
    .badge { padding:4px 10px; border-radius:20px; font-size:.75rem; font-weight:600; }
    .badge.pending { background:#fef3cd; color:#856404; }
    .badge.confirmed { background:#d1e7dd; color:#155724; }
    .badge.cancelled { background:#f8d7da; color:#842029; }
    .btn { padding:8px 14px; border:none; border-radius:8px; cursor:pointer; font-size:.85rem; text-decoration:none; display:inline-block; background:#1a6bad; color:#fff; }
    .btn-sm { padding:5px 10px; font-size:.8rem; }
    .btn-primary { background:#1a6bad; }
    .btn-success { background:#38a169; }
    .btn-danger { background:#e53e3e; }
    .loading, .empty { text-align:center; padding:40px; color:#888; }
  `]
})
export class OrderListComponent implements OnInit {
  orders: Order[] = [];
  loading = false;

  constructor(private orderService: OrderService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.orderService.getAll().subscribe({
      next: o => { this.orders = o; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  confirm(id: number) {
    this.orderService.confirm(id).subscribe(() => this.load());
  }

  cancel(id: number) {
    if (!confirm('Annuler cette commande ?')) return;
    this.orderService.cancel(id).subscribe(() => this.load());
  }
}
