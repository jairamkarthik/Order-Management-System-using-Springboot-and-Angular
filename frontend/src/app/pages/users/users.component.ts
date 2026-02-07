import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, FormControl, ReactiveFormsModule } from '@angular/forms';
import { AdminUsersService } from '../../core/services/admin-users.service';
import { AppUser, Role } from '../../core/models/user';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html'
})
export class UsersComponent implements OnInit {
  users: AppUser[] = [];
  loading = true;
  error: string | null = null;
  saving = false;

  // ✅ Reactive search (replaces ngModel q)
  searchCtrl = new FormControl<string>('', { nonNullable: true });

  // inline edit buffers
  roleEdit: Record<number, Role> = {};
  pwdEdit: Record<number, string> = {};
  updating: Record<number, boolean> = {};

  form = this.fb.group({
    username: ['', [Validators.required, Validators.maxLength(60)]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    role: ['USER' as Role, [Validators.required]]
  });

  constructor(private fb: FormBuilder, private api: AdminUsersService) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh() {
    this.loading = true;
    this.error = null;

    const q = this.searchCtrl.value.trim();

    this.api.list(q || null).subscribe({
      next: (res) => {
        this.users = res;

        // init buffers
        this.roleEdit = {};
        this.pwdEdit = {};
        this.updating = {};

        for (const u of this.users) {
          this.roleEdit[u.userId] = u.role;
          this.pwdEdit[u.userId] = '';
          this.updating[u.userId] = false;
        }
      },
      error: (e) => this.error = e?.error?.message ?? 'Failed to load users',
      complete: () => this.loading = false
    });
  }

  create() {
    if (this.form.invalid) return;
    this.saving = true;
    this.error = null;

    const { username, password, role } = this.form.getRawValue();

    this.api.create(username!, password!, role!).subscribe({
      next: () => {
        this.form.reset({ username: '', password: '', role: 'USER' as Role });
        this.refresh();
      },
      error: (e) => this.error = e?.error?.message ?? 'Create user failed',
      complete: () => this.saving = false
    });
  }

  save(u: AppUser) {
    if (u.username === 'admin') return;

    this.updating[u.userId] = true;
    this.error = null;

    const patch: any = {};
    const newRole = this.roleEdit[u.userId];
    const newPwd = (this.pwdEdit[u.userId] || '').trim();

    if (newRole && newRole !== u.role) patch.role = newRole;
    if (newPwd) patch.password = newPwd;

    if (!patch.role && !patch.password) {
      this.updating[u.userId] = false;
      return;
    }

    this.api.update(u.userId, patch).subscribe({
      next: (updated) => {
        const idx = this.users.findIndex(x => x.userId === updated.userId);
        if (idx >= 0) this.users[idx] = updated;

        this.pwdEdit[u.userId] = '';
        this.roleEdit[u.userId] = updated.role;
      },
      error: (e) => this.error = e?.error?.message ?? 'Update failed',
      complete: () => this.updating[u.userId] = false
    });
  }

  remove(u: AppUser) {
    if (u.username === 'admin') return;
    if (!confirm(`Delete user ${u.username}?`)) return;

    this.api.delete(u.userId).subscribe({
      next: () => this.refresh(),
      error: (e) => this.error = e?.error?.message ?? 'Delete failed'
    });
  }
}