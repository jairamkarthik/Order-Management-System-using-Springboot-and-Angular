import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../core/services/dashboard.service';
import { ProductsService } from '../../core/services/products.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  stats: any = null;
  lowStockCount = 0;
  loading = true;
  error: string | null = null;

  constructor(
    private dash: DashboardService,
    private productsApi: ProductsService
  ) { }

  ngOnInit(): void {
    this.refresh();
  }

  refresh() {
    this.loading = true;
    this.error = null;

    // Load stats
    this.dash.stats().subscribe({
      next: (res) => this.stats = res,
      error: (e) => this.error = e?.error?.message ?? 'Failed to load stats',
      complete: () => {
        // Load products for low stock count
        this.productsApi.list().subscribe({
          next: (products) => {
            this.lowStockCount = products.filter(p => p.stockQty <= 5).length;
            this.loading = false;
          },
          error: () => this.loading = false // Ignored, as stats are main priority
        });
      }
    });
  }
}
