import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { OrderService } from '../../core/services/order.service';
import { ProductService } from '../../core/services/product.service';
import { Product } from '../../core/models/product.model';

@Component({
  selector: 'app-order-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <div class="form-page">
      <h1>Nouvelle commande</h1>
      <form [formGroup]="form" (ngSubmit)="submit()" class="form-card">
        <div class="field">
          <label>Produit *</label>
          <select formControlName="productId" class="input">
            <option value="">-- Choisir un produit --</option>
            <option *ngFor="let p of products" [value]="p.id">
              {{ p.name }} — {{ p.price | number:'1.2-2' }} € (stock: {{ p.stock }})
            </option>
          </select>
        </div>
        <div class="field">
          <label>Quantité *</label>
          <input formControlName="quantity" type="number" min="1" class="input" />
        </div>
        <div class="actions">
          <a routerLink="/orders" class="btn btn-secondary">Annuler</a>
          <button type="submit" [disabled]="form.invalid" class="btn btn-primary">Passer la commande</button>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .form-page { max-width:480px; margin:40px auto; padding:24px; }
    h1 { font-size:1.6rem; color:#1a6bad; margin-bottom:24px; }
    .form-card { background:#fff; padding:28px; border-radius:12px; box-shadow:0 2px 8px #0001; }
    .field { margin-bottom:16px; }
    label { display:block; font-size:.85rem; font-weight:600; margin-bottom:4px; color:#444; }
    .input { width:100%; padding:10px 12px; border:1px solid #ddd; border-radius:8px; font-size:14px; }
    .actions { display:flex; gap:12px; justify-content:flex-end; margin-top:20px; }
    .btn { padding:10px 20px; border:none; border-radius:8px; cursor:pointer; font-size:.9rem; text-decoration:none; display:inline-block; }
    .btn-primary { background:#1a6bad; color:#fff; }
    .btn-primary:disabled { opacity:.5; }
    .btn-secondary { background:#eee; color:#333; }
  `]
})
export class OrderFormComponent implements OnInit {
  form = this.fb.group({
    productId: ['', Validators.required],
    quantity: [1, [Validators.required, Validators.min(1)]]
  });
  products: Product[] = [];

  constructor(
    private fb: FormBuilder,
    private orderService: OrderService,
    private productService: ProductService,
    private router: Router
  ) {}

  ngOnInit() {
    this.productService.getAll().subscribe(p => this.products = p);
  }

  submit() {
    if (this.form.invalid) return;
    const v = this.form.value;
    this.orderService.create({ productId: +v.productId!, quantity: v.quantity! })
      .subscribe(() => this.router.navigate(['/orders']));
  }
}
