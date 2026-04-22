import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../core/services/product.service';
import { Product } from '../../core/models/product.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="page">
      <div class="page-header">
        <h1>Produits</h1>
        <a routerLink="/products/new" class="btn btn-primary">+ Nouveau produit</a>
      </div>

      <div class="search-bar">
        <input [(ngModel)]="searchTerm" (ngModelChange)="onSearch()"
               placeholder="Rechercher un produit..." class="input" />
      </div>

      <div *ngIf="loading" class="loading">Chargement...</div>

      <div class="card-grid" *ngIf="!loading">
        <div class="card" *ngFor="let p of products">
          <div class="card-body">
            <div class="card-title">{{ p.name }}</div>
            <div class="card-sub">{{ p.category }}</div>
            <p class="card-desc">{{ p.description }}</p>
            <div class="card-footer">
              <span class="price">{{ p.price | number:'1.3-3' }} €</span>
              <span class="stock" [class.low]="(p.stock ?? 0) < 5">Stock : {{ p.stock }}</span>
            </div>
          </div>
          <div class="card-actions">
            <a [routerLink]="['/products', p.id, 'edit']" class="btn btn-sm">Modifier</a>
            <button (click)="delete(p.id!)" class="btn btn-sm btn-danger">Supprimer</button>
            <button (click)="order(p)" class="btn btn-sm btn-success">Commander</button>
          </div>
        </div>
        <div *ngIf="products.length === 0" class="empty">Aucun produit trouvé.</div>
      </div>
    </div>
  `,
  styles: [`
    .page { padding: 24px; max-width: 1100px; margin: 0 auto; }
    .page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:20px; }
    h1 { font-size:1.8rem; color:#1a6bad; }
    .search-bar { margin-bottom:20px; }
    .input { width:100%; max-width:400px; padding:10px 14px; border:1px solid #ddd; border-radius:8px; font-size:14px; }
    .card-grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(280px,1fr)); gap:16px; }
    .card { background:#fff; border-radius:12px; box-shadow:0 2px 8px #0001; overflow:hidden; }
    .card-body { padding:16px; }
    .card-title { font-weight:700; font-size:1.05rem; margin-bottom:4px; }
    .card-sub { font-size:.8rem; color:#888; margin-bottom:8px; }
    .card-desc { font-size:.85rem; color:#555; margin-bottom:12px; }
    .card-footer { display:flex; justify-content:space-between; }
    .price { font-weight:700; color:#1a6bad; }
    .stock { font-size:.85rem; color:#555; }
    .stock.low { color:#e53e3e; font-weight:600; }
    .card-actions { display:flex; gap:8px; padding:12px 16px; background:#f9f9f9; }
    .btn { padding:8px 14px; border:none; border-radius:8px; cursor:pointer; font-size:.85rem; text-decoration:none; display:inline-block; background:#1a6bad; color:#fff; }
    .btn-primary { background:#1a6bad; }
    .btn-sm { padding:6px 10px; }
    .btn-danger { background:#e53e3e; }
    .btn-success { background:#38a169; }
    .loading, .empty { text-align:center; padding:40px; color:#888; }
  `]
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  loading = false;
  searchTerm = '';

  constructor(private productService: ProductService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.productService.getAll().subscribe({
      next: p => { this.products = p; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  onSearch() {
    if (!this.searchTerm.trim()) { this.load(); return; }
    this.productService.search(this.searchTerm).subscribe(p => this.products = p);
  }

  delete(id: number) {
    if (!confirm('Supprimer ce produit ?')) return;
    this.productService.delete(id).subscribe(() => this.load());
  }

  order(p: Product) {
    // TODO Tâche 14 : naviguer vers /orders/new?productId=p.id
    alert('Fonctionnalité à implémenter (Tâche 14)');
  }
}
