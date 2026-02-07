import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, FormArray } from '@angular/forms';
import { OrdersService } from '../../core/services/orders.service';
import { ProductsService } from '../../core/services/products.service';
import { PurchaseOrder, OrderStatus, ShippingAddress } from '../../core/models/order';
import { Product } from '../../core/models/product';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html'
})
export class OrdersComponent implements OnInit {
  orders: PurchaseOrder[] = [];
  products: Product[] = [];

  loading = true;
  error: string | null = null;

  placing = false;
  savingEdit = false;

  filtersForm = this.fb.group({
    q: [''],
    status: ['' as OrderStatus | '']
  });

  editing: PurchaseOrder | null = null;
  editForm = this.fb.group({
    items: this.fb.array([]),
    address: this.fb.group({
      fullName: ['', [Validators.required]],
      phone: ['', [Validators.required]],
      line1: ['', [Validators.required]],
      line2: [''],
      city: ['', [Validators.required]],
      state: ['', [Validators.required]],
      pincode: ['', [Validators.required]],
      country: ['India', [Validators.required]]
    })
  });

  // place order form
  form = this.fb.group({
    items: this.fb.array([]),
    address: this.fb.group({
      fullName: ['', [Validators.required]],
      phone: ['', [Validators.required]],
      line1: ['', [Validators.required]],
      line2: [''],
      city: ['', [Validators.required]],
      state: ['', [Validators.required]],
      pincode: ['', [Validators.required]],
      country: ['India', [Validators.required]]
    })
  });

  constructor(
    private fb: FormBuilder,
    public ordersApi: OrdersService,
    private productsApi: ProductsService
  ) {}

  ngOnInit(): void {
    this.refreshAll();

  }

  get items(): FormArray { return this.form.get('items') as FormArray; }
  get editItems(): FormArray { return this.editForm.get('items') as FormArray; }

  addItem(target: 'place' | 'edit' = 'place') {
    const arr = target === 'place' ? this.items : this.editItems;
    arr.push(this.fb.group({
      productId: [null, [Validators.required]],
      qty: [1, [Validators.required, Validators.min(1)]]
    }));
  }

  removeItem(i: number, target: 'place' | 'edit' = 'place') {
    const arr = target === 'place' ? this.items : this.editItems;
    arr.removeAt(i);
  }

  refreshAll() {
    this.loading = true;
    this.error = null;

    this.productsApi.list().subscribe({
      next: (p) => {
        this.products = p;
        if (this.items.length === 0) this.addItem('place');
      },
      error: (e) => this.error = e?.error?.message ?? 'Failed to load products'
    });

    this.refreshOrders();
  }

  refreshOrders() {
    const q = (this.filtersForm.value.q ?? '').trim();
    const status = (this.filtersForm.value.status ?? '') as OrderStatus | '';

    const filters = {
      status: status || null,
      q: q || null
    } as any;

    const call$ = this.ordersApi.isAdmin()
      ? this.ordersApi.adminOrders(filters)
      : this.ordersApi.myOrders(filters);

    call$.subscribe({
      next: (o) => this.orders = o,
      error: (e) => this.error = e?.error?.message ?? 'Failed to load orders',
      complete: () => this.loading = false
    });
  }

  clearFilters() {
    this.filtersForm.reset({ q: '', status: '' });
    this.refreshOrders();
  }

  place() {
    if (this.form.invalid) return;
    this.placing = true;
    this.error = null;

    const items = this.items.getRawValue().map((x: any) => ({
      productId: Number(x.productId),
      qty: Number(x.qty)
    }));

    const address = this.form.get('address')!.getRawValue() as ShippingAddress;

    this.ordersApi.placeOrder(items, address).subscribe({
      next: () => {
        while (this.items.length) this.items.removeAt(0);
        this.addItem('place');
        this.form.get('address')!.reset({
          fullName: '',
          phone: '',
          line1: '',
          line2: '',
          city: '',
          state: '',
          pincode: '',
          country: 'India'
        });

        this.refreshOrders();
      },
      error: (e) => this.error = e?.error?.message ?? 'Place order failed',
      complete: () => this.placing = false
    });
  }

  // ADMIN only
  setStatus(o: PurchaseOrder, status: OrderStatus) {
    this.ordersApi.adminUpdateStatus(o.orderId, status).subscribe({
      next: (updated) => {
        const idx = this.orders.findIndex(x => x.orderId === updated.orderId);
        if (idx >= 0) this.orders[idx] = updated;
      },
      error: (e) => this.error = e?.error?.message ?? 'Update status failed'
    });
  }

  // USER only
  canEdit(o: PurchaseOrder) {
    return !this.ordersApi.isAdmin() && o.status === 'PLACED';
  }

  canCancel(o: PurchaseOrder) {
    return !this.ordersApi.isAdmin() && o.status === 'PLACED';
  }

  startEdit(o: PurchaseOrder) {
    this.editing = o;
    this.editForm.reset();
    while (this.editItems.length) this.editItems.removeAt(0);

    // items
    (o.items ?? []).forEach(it => {
      this.editItems.push(this.fb.group({
        productId: [it.product.productId, [Validators.required]],
        qty: [it.qty, [Validators.required, Validators.min(1)]]
      }));
    });
    if (this.editItems.length === 0) this.addItem('edit');

    const a = (o as any).shippingAddress;
    if (a) this.editForm.get('address')!.patchValue(a);
  }

  saveEdit() {
    if (!this.editing) return;
    if (this.editForm.invalid) return;
    this.savingEdit = true;
    this.error = null;

    const items = this.editItems.getRawValue().map((x: any) => ({
      productId: Number(x.productId),
      qty: Number(x.qty)
    }));

    const address = this.editForm.get('address')!.getRawValue() as ShippingAddress;

    this.ordersApi.updateMyOrder(this.editing.orderId, items, address).subscribe({
      next: (updated) => {
        const idx = this.orders.findIndex(x => x.orderId === updated.orderId);
        if (idx >= 0) this.orders[idx] = updated;
        this.editing = null;
      },
      error: (e) => this.error = e?.error?.message ?? 'Update order failed',
      complete: () => this.savingEdit = false
    });
  }

  cancelEdit() {
    this.editing = null;
  }

  cancelOrder(o: PurchaseOrder) {
    if (!confirm(`Cancel order #${o.orderId}?`)) return;

    this.ordersApi.cancelMyOrder(o.orderId).subscribe({
      next: (updated) => {
        const idx = this.orders.findIndex(x => x.orderId === updated.orderId);
        if (idx >= 0) this.orders[idx] = updated;
      },
      error: (e) => this.error = e?.error?.message ?? 'Cancel failed'
    });
  }
}