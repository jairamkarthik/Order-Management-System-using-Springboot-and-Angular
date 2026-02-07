
import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ProductsService } from '../../core/services/products.service';
import { Product } from '../../core/models/product';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html'
})
export class ProductsComponent implements OnInit {
  // Data
  allProducts: Product[] = []; // Store full list
  paginatedProducts: Product[] = []; // Store current page

  // Pagination & Search State
  currentPage = 1;
  pageSize = 10;
  totalPages = 1;
  searchTerm = '';
  showLowStockOnly = false;

  loading = true;
  error: string | null = null;

  editing: Product | null = null;
  saving = false;

  form = this.fb.group({
    sku: [''],
    name: ['', [Validators.required]],
    description: [''],
    unitPrice: [0, [Validators.required, Validators.min(0.01)]],
    stockQty: [0, [Validators.required, Validators.min(0)]],
    isActive: [true]
  });

  constructor(
    private fb: FormBuilder,
    private api: ProductsService,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.showLowStockOnly = params['lowStock'] === 'true';
      if (this.allProducts.length > 0) {
        this.updatePagination();
      }
    });
    this.refresh();
  }

  refresh() {
    this.loading = true;
    this.error = null;
    this.api.list().subscribe({
      next: (res) => {
        this.allProducts = res;
        this.updatePagination();
      },
      error: (e) => this.error = e?.error?.message ?? 'Failed to load products',
      complete: () => this.loading = false
    });
  }

  search(term: string) {
    this.searchTerm = term.toLowerCase();
    this.currentPage = 1; // Reset to first page on search
    this.updatePagination();
  }

  updatePagination() {
    // 1. Filter
    let filtered = this.allProducts;

    if (this.showLowStockOnly) {
      filtered = filtered.filter(p => p.stockQty <= 5);
    }

    if (this.searchTerm) {
      filtered = filtered.filter(p =>
        p.name.toLowerCase().includes(this.searchTerm) ||
        (p.sku && p.sku.toLowerCase().includes(this.searchTerm))
      );
    }

    // 2. Paginate
    this.totalPages = Math.ceil(filtered.length / this.pageSize) || 1;
    if (this.currentPage > this.totalPages) this.currentPage = this.totalPages;
    if (this.currentPage < 1) this.currentPage = 1;

    const start = (this.currentPage - 1) * this.pageSize;
    const end = start + this.pageSize;
    this.paginatedProducts = filtered.slice(start, end);
  }

  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.updatePagination();
    }
  }

  prevPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.updatePagination();
    }
  }

  startCreate() {
    this.editing = null;
    this.form.reset({ sku: '', name: '', description: '', unitPrice: 0, stockQty: 0, isActive: true });
  }

  startEdit(p: Product) {
    this.editing = p;
    this.form.reset({
      sku: p.sku ?? '',
      name: p.name,
      description: p.description ?? '',
      unitPrice: p.unitPrice,
      stockQty: p.stockQty,
      isActive: p.isActive
    });
  }

  save() {
    if (this.form.invalid) return;
    this.saving = true;
    this.error = null;

    const payload = this.form.getRawValue();

    const obs = this.editing
      ? this.api.update(this.editing.productId, payload as any)
      : this.api.create(payload as any);

    obs.subscribe({
      next: () => {
        this.startCreate();
        this.refresh();
      },
      error: (e) => this.error = e?.error?.message ?? 'Save failed',
      complete: () => this.saving = false
    });
  }

  remove(p: Product) {
    if (!confirm(`Delete product #${p.productId} (${p.name})?`)) return;
    this.api.delete(p.productId).subscribe({
      next: () => this.refresh(),
      error: (e) => this.error = e?.error?.message ?? 'Delete failed'
    });
  }

  clearLowStockFilter() {
    this.showLowStockOnly = false;
    this.updatePagination();
    // remove query param from url without reloading
    const url = new URL(window.location.href);
    url.searchParams.delete('lowStock');
    window.history.pushState({}, '', url);
  }
}
