import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="navbar">
      <span class="brand">🛒 E-Commerce</span>
      <div class="nav-links">
        <a routerLink="/products" routerLinkActive="active">Produits</a>
        <a routerLink="/orders" routerLinkActive="active">Commandes</a>
      </div>
    </nav>
    <main>
      <router-outlet />
    </main>
  `,
  styles: [`
    .navbar { background:#1a6bad; color:#fff; padding:0 24px; height:56px; display:flex; align-items:center; justify-content:space-between; }
    .brand { font-weight:700; font-size:1.1rem; }
    .nav-links { display:flex; gap:24px; }
    .nav-links a { color:#fff; text-decoration:none; font-size:.95rem; opacity:.85; }
    .nav-links a.active, .nav-links a:hover { opacity:1; border-bottom:2px solid #fff; padding-bottom:2px; }
    main { min-height:calc(100vh - 56px); }
  `]
})
export class AppComponent {}
