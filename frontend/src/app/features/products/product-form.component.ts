import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { ProductService } from '../../core/services/product.service';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <div class="form-page">
      <h1>{{ isEdit ? 'Modifier' : 'Nouveau' }} produit</h1>
      <form [formGroup]="form" (ngSubmit)="submit()" class="form-card">
        <div class="field">
          <label>Nom *</label>
          <input formControlName="name" class="input" placeholder="Nom du produit" />
          <span class="err" *ngIf="form.get('name')?.invalid && form.get('name')?.touched">Obligatoire</span>
        </div>
        <div class="field">
          <label>Description</label>
          <textarea formControlName="description" class="input" rows="3"></textarea>
        </div>
        <div class="row">
          <div class="field">
            <label>Prix (€) *</label>
            <input formControlName="price" type="number" step="0.001" class="input" />
          </div>
          <div class="field">
            <label>Stock *</label>
            <input formControlName="stock" type="number" class="input" />
          </div>
        </div>
        <div class="field">
          <label>Catégorie</label>
          <input formControlName="category" class="input" placeholder="ex: Électronique" />
        </div>
        <div class="actions">
          <a routerLink="/products" class="btn btn-secondary">Annuler</a>
          <button type="submit" [disabled]="form.invalid" class="btn btn-primary">
            {{ isEdit ? 'Mettre à jour' : 'Créer' }}
          </button>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .form-page { max-width:560px; margin:40px auto; padding:24px; }
    h1 { font-size:1.6rem; color:#1a6bad; margin-bottom:24px; }
    .form-card { background:#fff; padding:28px; border-radius:12px; box-shadow:0 2px 8px #0001; }
    .field { margin-bottom:16px; }
    label { display:block; font-size:.85rem; font-weight:600; margin-bottom:4px; color:#444; }
    .input { width:100%; padding:10px 12px; border:1px solid #ddd; border-radius:8px; font-size:14px; }
    .row { display:grid; grid-template-columns:1fr 1fr; gap:12px; }
    .err { color:#e53e3e; font-size:.8rem; }
    .actions { display:flex; gap:12px; justify-content:flex-end; margin-top:20px; }
    .btn { padding:10px 20px; border:none; border-radius:8px; cursor:pointer; font-size:.9rem; text-decoration:none; display:inline-block; }
    .btn-primary { background:#1a6bad; color:#fff; }
    .btn-primary:disabled { opacity:.5; }
    .btn-secondary { background:#eee; color:#333; }
  `]
})
export class ProductFormComponent implements OnInit {
  form = this.fb.group({
    name: ['', Validators.required],
    description: [''],
    price: [null as number | null, [Validators.required, Validators.min(0)]],
    stock: [null as number | null, [Validators.required, Validators.min(0)]],
    category: ['']
  });
  isEdit = false;
  private id?: number;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.id = this.route.snapshot.params['id'];
    if (this.id) {
      this.isEdit = true;
      this.productService.getById(this.id).subscribe(p => this.form.patchValue(p));
    }
  }

  submit() {
    if (this.form.invalid) return;
    const data = this.form.value as any;
    console.log('data envoyé :', data);
    const obs = this.isEdit
      ? this.productService.update(this.id!, data)
      : this.productService.create(data);
    obs.subscribe(() => this.router.navigate(['/products']));
  }
}
